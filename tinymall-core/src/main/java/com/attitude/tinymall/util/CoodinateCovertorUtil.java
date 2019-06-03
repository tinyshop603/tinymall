package com.attitude.tinymall.util;

import com.attitude.tinymall.domain.baidu.address.Location;

import java.math.BigDecimal;

import static java.lang.StrictMath.*;

/**
 * GCJ-02(火星坐标) 和 BD-09 （百度坐标）
 * 之间的相互转换
 *
 * @author yang
 */
public class CoodinateCovertorUtil {


    private static final double X_PI = 3.14159265358979324 * 3000.0 / 180.0;



    /**
     * 将火星坐标转变成百度坐标
     *
     * @param gclngLat 火星坐标（高德、腾讯地图坐标等）
     * @return 百度坐标
     */

    public static Location gcj02ToBd09(Location gclngLat) {
        double x = gclngLat.getLng(), y = gclngLat.getLat();
        double z = sqrt(x * x + y * y) + 0.00002 * sin(y * X_PI);
        double theta = atan2(y, x) + 0.000003 * cos(x * X_PI);
        return new Location(dataDigit(6, z * cos(theta) + 0.0065), dataDigit(6, z * sin(theta) + 0.006));

    }

    /**
     * 将百度坐标转变成火星坐标
     *
     * @param bdlngLat 百度坐标（百度地图坐标）
     * @return 火星坐标(高德 、 腾讯地图等)
     */
    public  static Location bd09ToGcj02(Location bdlngLat) {
        double x = bdlngLat.getLng() - 0.0065, y = bdlngLat.getLat() - 0.006;
        double z = sqrt(x * x + y * y) - 0.00002 * sin(y * X_PI);
        double theta = atan2(y, x) - 0.000003 * cos(x * X_PI);
        return new Location(dataDigit(6, z * cos(theta)), dataDigit(6, z * sin(theta)));

    }

    /**
     * 对double类型数据保留小数点后多少位
     * 高德地图转码返回的就是 小数点后6位，为了统一封装一下
     *
     * @param digit 位数
     * @param in    输入
     * @return 保留小数位后的数
     */
    static double dataDigit(int digit, double in) {
        return new BigDecimal(in).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();

    }


    //测试代码
    public static void main(String[] args) {
        Location lngLat_bd = new Location(120.153192, 30.25897);
        System.out.println(bd09ToGcj02(lngLat_bd));
    }

}
