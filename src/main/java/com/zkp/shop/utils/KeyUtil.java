package com.zkp.shop.utils;

import java.util.Random;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.utils
 * @time: 2019/6/28 9:37
 * @description:
 */
public class KeyUtil {

    /**
     * 生成唯一的主键
     * 格式: 时间+随机数
     *
     * @return
     */
    public static synchronized String genUniqueKey() {
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;
        return System.currentTimeMillis() + String.valueOf(number);
    }

}
