package com.zkp.shop.repository;

import com.zkp.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.repository
 * @time: 2019/6/26 13:51
 * @description:
 */
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据手机号查找用户
     *
     * @param userPhone 手机号
     * @return User
     */
    User findByUserPhone(String userPhone);

}
