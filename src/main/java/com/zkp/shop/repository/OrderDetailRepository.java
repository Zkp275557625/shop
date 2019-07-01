package com.zkp.shop.repository;

import com.zkp.shop.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.repository
 * @time: 2019/6/27 16:34
 * @description:
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    /**
     * 根据订单号查找所有商品
     *
     * @param orderId
     * @return
     */
    List<OrderDetail> findAllByOrderId(String orderId);

}
