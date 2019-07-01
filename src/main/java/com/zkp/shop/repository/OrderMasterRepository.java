package com.zkp.shop.repository;

import com.zkp.shop.entity.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.repository
 * @time: 2019/6/27 16:33
 * @description:
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster, String> {

    /**
     * 查询某个用户的所有订单
     *
     * @param phone
     * @param pageable
     * @return
     */
    Page<OrderMaster> findAllByBuyerPhone(String phone, Pageable pageable);


    /**
     * 查询该用户支付/未支付的订单列表
     *
     * @param phone
     * @param payStatus
     * @param pageable
     * @return
     */
    Page<OrderMaster> findAllByBuyerPhoneAndPayStatus(String phone, Integer payStatus, Pageable pageable);

    /**
     * 查询该用户新订单/已完成/已取消订单
     *
     * @param phone
     * @param orderStatus
     * @param pageable
     * @return
     */
    Page<OrderMaster> findAllByBuyerPhoneAndOrderStatus(String phone, Integer orderStatus, Pageable pageable);

    /**
     * 查询该用户未评论/已评论订单
     *
     * @param phone
     * @param commentStatus
     * @param pageable
     * @return
     */
    Page<OrderMaster> findAllByBuyerPhoneAndCommentStatus(String phone, Integer commentStatus, Pageable pageable);
}
