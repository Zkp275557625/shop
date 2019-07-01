package com.zkp.shop.response;

/**
 * @author: hmc
 * @project: booking
 * @package: com.zkp.booking.response
 * @time: 2019/6/24 16:14
 * @description: 返回的数据实体类
 */
public class Response<T> {

    /**
     * 错误码
     */
    private int errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 服务器当前时间
     */
    private String currentTime;

    /**
     * 返回的数据
     */
    private T data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
