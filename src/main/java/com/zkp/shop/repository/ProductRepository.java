package com.zkp.shop.repository;

import com.zkp.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.repository
 * @time: 2019/6/26 17:32
 * @description:
 */
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * 查询在售/下架商品
     *
     * @param productStatusEnumCode 0/1
     * @param pageable 分页显示
     * @return Product
     */
    Page<Product> findByProductStatus(Integer productStatusEnumCode, Pageable pageable);

}
