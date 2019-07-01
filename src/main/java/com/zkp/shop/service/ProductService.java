package com.zkp.shop.service;

import com.zkp.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.service
 * @time: 2019/6/26 17:37
 * @description:
 */
public interface ProductService {

    /**
     * 查找一个
     *
     * @param productId
     * @return
     */
    Product findOne(String productId);

    /**
     * 查询所有在架商品列表
     *@param pageable
     * @return
     */
    Page<Product> findUpAll(Pageable pageable);

    /**
     * 查询所有下架商品列表
     *@param pageable
     * @return
     */
    Page<Product> findOffAll(Pageable pageable);

    /**
     * 分页查找
     *
     * @param pageable
     * @return
     */
    Page<Product> findAll(Pageable pageable);

    /**
     * 保存
     *
     * @param product
     * @return
     */
    Product save(Product product);

}
