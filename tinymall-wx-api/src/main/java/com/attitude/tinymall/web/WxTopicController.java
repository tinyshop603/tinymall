package com.attitude.tinymall.web;

import com.attitude.tinymall.util.ResponseUtil;
import com.attitude.tinymall.service.LitemallTopicService;
import com.attitude.tinymall.domain.LitemallTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/{storeId}/topic")
public class WxTopicController {
    @Autowired
    private LitemallTopicService topicService;

    /**
     * 专题列表
     *
     * @param page 分页页数
     * @param size 分页大小
     * @return 专题列表
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data:
     *          {
     *              data: xxx,
     *              count: xxx
     *          }
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("list")
    public Object list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<LitemallTopic> topicList = topicService.queryList(page, size);
        int total = topicService.queryTotal();
        Map<String, Object> data = new HashMap();
        data.put("data", topicList);
        data.put("count", total);
        return ResponseUtil.ok(data);
    }

    /**
     * 专题详情
     *
     * @param id 专题ID
     * @return 专题详情
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data: xxx
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("detail")
    public Object detail(Integer id) {
        if(id == null){
            return ResponseUtil.badArgument();
        }

        LitemallTopic topic = topicService.findById(id);
        return ResponseUtil.ok(topic);
    }

    /**
     * 相关专题
     *
     * @param id 专题ID
     * @return 相关专题
     *   成功则
     *  {
     *      errno: 0,
     *      errmsg: '成功',
     *      data: xxx
     *  }
     *   失败则 { errno: XXX, errmsg: XXX }
     */
    @GetMapping("related")
    public Object related(Integer id) {
        if(id == null){
            return ResponseUtil.fail402();
        }

        List<LitemallTopic> topicRelatedList = topicService.queryRelatedList(id, 0, 4);
        return ResponseUtil.ok(topicRelatedList);
    }
}
