package com.zkp.shop.enums;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.enums
 * @time: 2019/6/26 17:23
 * @description:
 */
public enum ProductStatusEnum implements CodeEnum {
    UP(0, "在架"),
    DOWN(1, "下架");

    private Integer code;

    private String message;

    ProductStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }
}
