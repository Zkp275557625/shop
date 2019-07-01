package com.zkp.shop.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: hmc
 * @project: booking
 * @package: com.zkp.booking.utils
 * @time: 2019/6/24 16:27
 * @description:
 */
public class TimeUtils {

    /**
     * 获取当前时间
     * @return 2019-06-24 16:32:22
     */
    public static String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

}
