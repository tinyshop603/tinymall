package com.attitude.tinymall.web;

import com.attitude.tinymall.domain.baidu.address.Location;
import com.attitude.tinymall.domain.baidu.address.PoiAddress;
import com.attitude.tinymall.domain.baidu.geocode.GeoDecodingAddress;
import com.attitude.tinymall.service.BaiduFenceService;
import com.attitude.tinymall.util.CoodinateCovertorUtil;
import com.attitude.tinymall.util.ResponseUtil;
import com.attitude.tinymall.service.LitemallAdminService;
import com.attitude.tinymall.vo.LocationVO;
import com.attitude.tinymall.vo.PoiAddressVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.util.RegexUtil;
import com.attitude.tinymall.domain.LitemallAddress;
import com.attitude.tinymall.service.LitemallAddressService;
import com.attitude.tinymall.service.LitemallRegionService;
import com.attitude.tinymall.annotation.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wx/{storeId}/address")
public class WxAddressController {
    private final Log logger = LogFactory.getLog(WxAddressController.class);

    @Autowired
    private LitemallAddressService addressService;
    @Autowired
    private LitemallRegionService regionService;
    @Autowired
    private BaiduFenceService baiduFenceService;
    @Autowired
    private LitemallAdminService adminService;

    /**
     * 用户收货地址列表
     *
     * @param userId 用户ID
     * @return 收货地址列表
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data: xxx
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("list")
    public Object list(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        List<LitemallAddress> addressList = addressService.queryByUid(userId);
        List<Map<String, Object>> addressVoList = new ArrayList<>(addressList.size());
        for (LitemallAddress address : addressList) {
            Map<String, Object> addressVo = new HashMap<>();
            addressVo.put("id", address.getId());
            addressVo.put("name", address.getName());
            addressVo.put("mobile", address.getMobile());
            addressVo.put("isDefault", address.getIsDefault());
            String province = regionService.findById(address.getProvinceId()).getName();
            String city = regionService.findById(address.getCityId()).getName();
            String area = regionService.findById(address.getAreaId()).getName();
            String addr = address.getAddress();
            String detailedAddress = province + city + area + " " + addr;
            addressVo.put("detailedAddress", detailedAddress);

            addressVoList.add(addressVo);
        }
        return ResponseUtil.ok(addressVoList);
    }

    /**
     * 收货地址详情
     *
     * @param userId 用户ID
     * @param id     收获地址ID
     * @return 收货地址详情
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * id: xxx,
     * name: xxx,
     * provinceId: xxx,
     * cityId: xxx,
     * districtId: xxx,
     * mobile: xxx,
     * address: xxx,
     * isDefault: xxx,
     * provinceName: xxx,
     * cityName: xxx,
     * areaName: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, Integer id) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        LitemallAddress address = addressService.findById(id);
        if (address == null) {
            return ResponseUtil.badArgumentValue();
        }

        Map<Object, Object> data = new HashMap<Object, Object>();
        data.put("id", address.getId());
        data.put("name", address.getName());
        data.put("provinceId", address.getProvinceId());
        data.put("cityId", address.getCityId());
        data.put("districtId", address.getAreaId());
        data.put("mobile", address.getMobile());
        data.put("address", address.getAddress());
        data.put("isDefault", address.getIsDefault());
        String pname = regionService.findById(address.getProvinceId()).getName();
        data.put("provinceName", pname);
        String cname = regionService.findById(address.getCityId()).getName();
        data.put("cityName", cname);
        String dname = regionService.findById(address.getAreaId()).getName();
        data.put("areaName", dname);
        return ResponseUtil.ok(data);
    }

    /**
     * 添加或更新收货地址
     *
     * @param userId  用户ID
     * @param address 用户收货地址
     * @return 添加或更新操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("save")
    public Object save(@LoginUser Integer userId, @RequestBody LitemallAddress address, @PathVariable("storeId") String appId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (address == null) {
            return ResponseUtil.badArgument();
        }

        // 测试收货手机号码是否正确
        String mobile = address.getMobile();
        if (!RegexUtil.isMobileExact(mobile)) {
            return ResponseUtil.badArgument();
        }

        // 验证地址是否在合法的范围内
      try {
        boolean isValidAddress = baiduFenceService
            .isValidLocationWithinFence(userId.toString(), address.getAddress(),
                adminService.findAdminByOwnerId(appId).getShopFenceId());
        if (!isValidAddress) {
          return ResponseUtil.unReachAddress();
        }
      } catch (Exception e) {
        e.printStackTrace();
        return ResponseUtil.badArgument();
      }

        if (address.getIsDefault()) {
            // 重置其他收获地址的默认选项
            addressService.resetDefault(userId);
        }

        if (address.getId() == null || address.getId().equals(0)) {
            address.setId(null);
            address.setUserId(userId);
            addressService.add(address, appId);
        } else {
            address.setUserId(userId);
            addressService.update(address);
        }
        return ResponseUtil.ok(address.getId());
    }

    /**
     * 删除收货地址
     *
     * @param userId  用户ID
     * @param address 用户收货地址
     * @return 删除结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("delete")
    public Object delete(@LoginUser Integer userId, @RequestBody LitemallAddress address) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (address == null) {
            return ResponseUtil.badArgument();
        }

        addressService.delete(address.getId());
        return ResponseUtil.ok();
    }

    @GetMapping("location")
    public Object getLoacationByGeoParams(@LoginUser Integer userId,
                                          @RequestParam String lng,
                                          @RequestParam String lat,
                                          String keyword,
                                          @PathVariable("storeId") String appId) {

        Location bd09Location = CoodinateCovertorUtil.gcj02ToBd09(new Location(Double.valueOf(lng), Double.valueOf(lat)));
        try {
            GeoDecodingAddress geoDecodingAddress = baiduFenceService.reverseGeocoding(bd09Location);
            LocationVO locationVO = new LocationVO();
            BeanUtils.copyProperties(geoDecodingAddress, locationVO);
            boolean isValidAddress = baiduFenceService
                    .isValidLocationWithinFence(userId.toString(), geoDecodingAddress.getLocation(),
                            adminService.findAdminByOwnerId(appId).getShopFenceId());
            locationVO.setDistributionStatus(isValidAddress ? "in" : "out");

            if (StringUtils.isEmpty(keyword)){
                locationVO.setKeywordsNearbyAddresses(Collections.emptyList());
            }else {
                // TODO region应根据店铺位置来, 目前只是在北京
                List<PoiAddress> poiAddresses = baiduFenceService.listPlacesByKeywords(keyword, "北京");
                locationVO.setKeywordsNearbyAddresses(poiAddresses
                        .stream()
                        .map(it -> new PoiAddressVO(it.getAddress(), it.getName()))
                        .collect(Collectors.toList()));
            }

            return ResponseUtil.ok(locationVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail();
        }
    }

}
