package com.zkp.shop.utils;

import com.zkp.shop.enums.CodeEnum;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.utils
 * @time: 2019/6/26 17:25
 * @description:
 */
public class EnumUtil {
    public static <T extends CodeEnum> T getByCode(Integer code, Class<T> enumClass) {
        for (T each: enumClass.getEnumConstants()) {
            if (code.equals(each.getCode())) {
                return each;
            }
        }
        return null;
    }
}
