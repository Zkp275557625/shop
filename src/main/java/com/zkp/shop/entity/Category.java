package com.zkp.shop.entity;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.entity
 * @time: 2019/6/27 9:01
 * @description: 商品分类表
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Category {

    /**
     * 类目id
     */
    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid")
    @GeneratedValue(generator="idGenerator")
    private String categoryId;

    /**
     * 类目名字
     */
    private String categoryName;

    /**
     * 类目编号
     */
    private Integer categoryType;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
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
