package com.zkp.shop.config;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.config
 * @time: 2019/6/26 14:23
 * @description:
 */
public class Constant {

    public static final String TOKEN_PREFIX = "token_%s";
    public static final String TOKEN = "token";

    /**
     * cookie 2小时过期
     */
    public static final Integer EXPIRE = 7200;

    /**
     * 正常返回结果
     */
    public static final int RESULT_OK = 0;

    /**
     * 没有正常返回结果
     */
    public static final int RESULT_ERROR = 1;

    /**
     * 正常返回结果
     */
    public static final String RESULT_OK_STRING = "";

    /**
     * 手机号已经被注册
     */
    public static final String RESULT_ERROR_ALREADY_REGISTER = "该手机号已被注册";
    /**
     * 用户不存在
     */
    public static final String RESULT_ERROR_USER_NOT_EXIST = "用户不存在";
    /**
     * 密码错误
     */
    public static final String RESULT_ERROR_PASSWORD_INCORRECT = "密码错误";
    /**
     * 没有缓存信息
     */
    public static final String RESULT_ERROR_NO_REDIS = "没有缓存";
    /**
     * 请先登录
     */
    public static final String RESULT_ERROR_LOGIN_FIRST = "请先登录";
    /**
     * 分类不存在
     */
    public static final String RESULT_ERROR_CATEGORY_NOT_EXIST = "分类不存在";
    /**
     * 商品不存在
     */
    public static final String RESULT_ERROR_PRODUCT_NOT_EXIST = "商品不存在";
    /**
     * 商品已经在售
     */
    public static final String RESULT_ERROR_PRODUCT_ALREADY_ONSALE = "商品已经在售";
    /**
     * 商品已经不在售
     */
    public static final String RESULT_ERROR_PRODUCT_ALREADY_OFFSALE = "商品已经不在售";
    /**
     * 库存不足
     */
    public static final String RESULT_ERROR_PRODUCT_STOCK_NOT_ENOUGH = "库存不足";
    /**
     * 您还没有订单哦
     */
    public static final String RESULT_ERROR_ORDER_NO_LIST = "您还没有订单哦";
    /**
     * 订单不存在
     */
    public static final String RESULT_ERROR_ORDER_NOT_EXIST = "订单不存在";
    /**
     * 订单已被取消
     */
    public static final String RESULT_ERROR_ORDER_ALREADY_CANCEL = "订单已被取消";
    /**
     * 订单已完结
     */
    public static final String RESULT_ERROR_ORDER_ALREADY_FINISHED = "订单已完结";
    /**
     * 收货地址不存在
     */
    public static final String RESULT_ERROR_ADDRESS_NOT_EXIST = "收货地址不存在";
    /**
     * 该订单已评论
     */
    public static final String RESULT_ERROR_ALREADY_COMMENT = "该订单已评论";
}
