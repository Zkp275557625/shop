package com.zkp.shop.entity;

import com.zkp.shop.enums.ProductStatusEnum;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.entity
 * @time: 2019/6/26 17:19
 * @description: 商品表
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product implements Serializable {

    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @GeneratedValue(generator="idGenerator")
    private String productId;

    /**
     * 名字
     */
    private String productName;

    /**
     * 单价
     */
    private BigDecimal productPrice;

    /**
     * 库存
     */
    private Integer productStock;

    /**
     * 描述
     */
    private String productDescription;

    /**
     * 小图
     */
    private String productIcon;

    /**
     * 状态, 0正常1下架
     */
    private Integer productStatus = ProductStatusEnum.UP.getCode();

    /**
     * 分类编号
     */
    private String categoryType;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;

    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }

    public Integer getProductStatus() {
        return productStatus;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
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
