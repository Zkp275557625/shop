package com.zkp.shop.enums;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.enums
 * @time: 2019/6/27 16:18
 * @description:
 */
public enum OrderStatusEnum implements CodeEnum {
    NEW(0, "新订单"),
    FINISHED(1, "完结"),
    CANCEL(2, "已取消"),
    ;

    private Integer code;

    private String message;

    OrderStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }
}
