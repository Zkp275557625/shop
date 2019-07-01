package com.zkp.shop.repository;

import com.zkp.shop.entity.DeliveryAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.repository
 * @time: 2019/6/28 17:50
 * @description:
 */
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, String> {

    /**
     * 通过用户手机号修改收货地址
     *
     * @param ownerPhone
     * @return
     */
    DeliveryAddress findByOwnerPhone(String ownerPhone);

    /**
     * 查询某个用户所有的收货地址信息
     *
     * @param phone
     * @param pageable
     * @return
     */
    Page<DeliveryAddress> findAllByOwnerPhone(String phone, Pageable pageable);

}
