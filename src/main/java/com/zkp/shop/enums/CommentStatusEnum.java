package com.zkp.shop.enums;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.enums
 * @time: 2019/6/27 16:19
 * @description:
 */
public enum CommentStatusEnum implements CodeEnum {
    UNCOMMENTED(0, "未评论"),
    COMMENTED(1, "已评论"),
    ;

    private Integer code;

    private String message;

    CommentStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }
}
