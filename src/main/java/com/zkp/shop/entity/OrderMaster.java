package com.zkp.shop.entity;

import com.zkp.shop.enums.CommentStatusEnum;
import com.zkp.shop.enums.OrderStatusEnum;
import com.zkp.shop.enums.PayStatusEnum;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.entity
 * @time: 2019/6/27 16:16
 * @description: 订单表
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class OrderMaster {

    /**
     * 订单id
     */
    @Id
    private String orderId;

    /**
     * 买家名字
     */
    private String buyerName;

    /**
     * 买家手机号
     */
    private String buyerPhone;

    /**
     * 买家地址
     */
    private String buyerAddress;

    /**
     * 买家微信Openid
     */
    private String buyerOpenid;

    /**
     * 订单总金额
     */
    private BigDecimal orderPrice;

    /**
     * 订单状态, 默认为0新下单
     */
    private Integer orderStatus = OrderStatusEnum.NEW.getCode();

    /**
     * 支付状态, 默认为0未支付
     */
    private Integer payStatus = PayStatusEnum.WAIT.getCode();

    /**
     * 评论状态
     */
    private Integer commentStatus = CommentStatusEnum.UNCOMMENTED.getCode();

    /**
     * 创建时间
     */
    @CreatedDate
    private Date createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    private Date updateTime;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getBuyerOpenid() {
        return buyerOpenid;
    }

    public void setBuyerOpenid(String buyerOpenid) {
        this.buyerOpenid = buyerOpenid;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(Integer commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getCreateTime() {
        return createTime.toString();
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime.toString();
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
