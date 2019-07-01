package com.zkp.shop.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.entity
 * @time: 2019/6/26 13:47
 * @description: 用户表
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

    @Id
    @GeneratedValue
    private int userId;

    private String userName;

    private String userPassword;

    private String userPhone;

    /**
     * 注册时间
     */
    @CreatedDate
    private Date registerTime;

    /**
     * 个人信息修改(更新)时间
     */
    @LastModifiedDate
    private Date updateTime;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getRegisterTime() {
        return registerTime.toString();
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getUpdateTime() {
        return updateTime.toString();
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
