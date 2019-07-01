package com.zkp.shop.repository;

import com.zkp.shop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.repository
 * @time: 2019/6/27 11:08
 * @description:
 */
public interface CategoryRepository extends JpaRepository<Category, String> {
}
