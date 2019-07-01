package com.zkp.shop.service.impl;

import com.zkp.shop.entity.Product;
import com.zkp.shop.enums.ProductStatusEnum;
import com.zkp.shop.repository.ProductRepository;
import com.zkp.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.service.impl
 * @time: 2019/6/26 17:42
 * @description:
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product findOne(String productId) {
        return productRepository.getOne(productId);
    }

    @Override
    public Page<Product> findUpAll(Pageable pageable) {
        return productRepository.findByProductStatus(ProductStatusEnum.UP.getCode(), pageable);
    }

    @Override
    public Page<Product> findOffAll(Pageable pageable) {
        return productRepository.findByProductStatus(ProductStatusEnum.DOWN.getCode(), pageable);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }
}
