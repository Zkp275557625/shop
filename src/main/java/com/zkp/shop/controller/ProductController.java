package com.zkp.shop.controller;

import com.zkp.shop.config.Constant;
import com.zkp.shop.entity.Product;
import com.zkp.shop.enums.ProductStatusEnum;
import com.zkp.shop.response.Response;
import com.zkp.shop.service.ProductService;
import com.zkp.shop.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.data.domain.PageRequest.of;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.controller
 * @time: 2019/6/26 17:53
 * @description:
 */
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 添加商品入库
     *
     * @param productName
     * @param productPrice
     * @param productStock
     * @param productDescription
     * @param productIcon
     * @param categoryType
     * @return
     */
    @RequestMapping(value = "/product/add", name = "添加商品入库", method = RequestMethod.POST)
    @ResponseBody
    public Response<Product> addProduct(@RequestParam(name = "productName") String productName,
                                        @RequestParam(name = "productPrice") BigDecimal productPrice,
                                        @RequestParam(name = "productStock") Integer productStock,
                                        @RequestParam(name = "productDescription") String productDescription,
                                        @RequestParam(name = "productIcon") String productIcon,
                                        @RequestParam(name = "categoryType") String categoryType) {

        Product product = new Product();
        product.setProductName(productName);
        product.setProductPrice(productPrice);
        product.setProductStock(productStock);
        product.setProductDescription(productDescription);
        product.setProductIcon(productIcon);
        product.setCategoryType(categoryType);

        Response<Product> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        product = productService.save(product);
        response.setData(product);
        response.setErrorCode(Constant.RESULT_OK);
        response.setErrorMsg(Constant.RESULT_OK_STRING);
        return response;
    }

    /**
     * 更新商品信息
     *
     * @param productId
     * @param productName
     * @param productPrice
     * @param productStock
     * @param productDescription
     * @param productIcon
     * @param categoryType
     * @return
     */
    @RequestMapping(value = "/product/updateProduct", name = "更新商品信息", method = RequestMethod.POST)
    @ResponseBody
    public Response<Product> updateProduct(@RequestParam(name = "productId") String productId,
                                           @RequestParam(name = "productName") String productName,
                                           @RequestParam(name = "productPrice") BigDecimal productPrice,
                                           @RequestParam(name = "productStock") Integer productStock,
                                           @RequestParam(name = "productDescription") String productDescription,
                                           @RequestParam(name = "productIcon") String productIcon,
                                           @RequestParam(name = "categoryType") String categoryType) {

        Response<Product> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        Product product = productService.findOne(productId);
        if (product == null) {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_NOT_EXIST);
        } else {
            product.setProductName(productName);
            product.setProductPrice(productPrice);
            product.setProductStock(productStock);
            product.setProductDescription(productDescription);
            product.setProductIcon(productIcon);
            product.setCategoryType(categoryType);
            product = productService.save(product);
            response.setData(product);
            response.setErrorCode(Constant.RESULT_OK);
            response.setErrorMsg(Constant.RESULT_OK_STRING);
        }
        return response;
    }

    /**
     * 更改商品状态 下架-->在售
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/product/onSale", name = "更改商品状态 下架-->在售", method = RequestMethod.POST)
    @ResponseBody
    public Response<Product> onSale(@RequestParam(name = "productId") String productId) {

        Response<Product> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        Product product = productService.findOne(productId);
        if (product == null) {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_NOT_EXIST);
        } else {
            if (product.getProductStatus().equals(ProductStatusEnum.UP.getCode())) {
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_ALREADY_ONSALE);
            } else {
                product.setProductStatus(ProductStatusEnum.UP.getCode());
                product = productService.save(product);
                response.setData(product);
                response.setErrorCode(Constant.RESULT_OK);
                response.setErrorMsg(Constant.RESULT_OK_STRING);
            }
        }
        return response;
    }

    /**
     * 更改商品状态 在售-->下架
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/product/offSale", name = "更改商品状态 在售-->下架", method = RequestMethod.POST)
    @ResponseBody
    public Response<Product> offSale(@RequestParam(name = "productId") String productId) {
        Response<Product> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        Product product = productService.findOne(productId);
        if (product == null) {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_NOT_EXIST);
        } else {
            if (product.getProductStatus().equals(ProductStatusEnum.DOWN.getCode())) {
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_ALREADY_OFFSALE);
            } else {
                product.setProductStatus(ProductStatusEnum.DOWN.getCode());
                product = productService.save(product);
                response.setData(product);
                response.setErrorCode(Constant.RESULT_OK);
                response.setErrorMsg(Constant.RESULT_OK_STRING);
            }
        }
        return response;
    }

    /**
     * 获取商品列表(包括在售和下架的商品，分页显示)
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/product/getProductList", name = "获取商品列表(包括在售和下架的商品，分页显示)", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<Product>> getProductList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Response<Page<Product>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        PageRequest request = of(page, size);
        response.setData(productService.findAll(request));
        response.setErrorCode(Constant.RESULT_OK);
        response.setErrorMsg(Constant.RESULT_OK_STRING);

        return response;
    }

    /**
     * 获取所有在售商品列表
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/product/getOnSaleList", name = "获取所有在售商品列表", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<Product>> getOnSaleList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Response<Page<Product>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        PageRequest request = of(page, size);
        response.setData(productService.findUpAll(request));
        response.setErrorCode(Constant.RESULT_OK);
        response.setErrorMsg(Constant.RESULT_OK_STRING);

        return response;
    }

    /**
     * 获取所有下架商品
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/product/getOffSaleList", name = "获取所有下架商品", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<Product>> getOffSaleList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Response<Page<Product>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        PageRequest request = of(page, size);
        response.setData(productService.findOffAll(request));
        response.setErrorCode(Constant.RESULT_OK);
        response.setErrorMsg(Constant.RESULT_OK_STRING);

        return response;
    }

    /**
     * 增加库存
     *
     * @param productId
     * @param number
     * @return
     */
    @RequestMapping(value = "/product/increaseStock", name = "增加库存", method = RequestMethod.POST)
    @ResponseBody
    public Response<Product> increaseStock(@RequestParam(name = "productId") String productId,
                                           @RequestParam(name = "number") Integer number) {

        Response<Product> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        Product product = productService.findOne(productId);

        if (product == null) {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_NOT_EXIST);
        } else {
            product.setProductStock(product.getProductStock() + number);
            product = productService.save(product);
            response.setData(product);
            response.setErrorCode(Constant.RESULT_OK);
            response.setErrorMsg(Constant.RESULT_OK_STRING);
        }
        return response;
    }

    /**
     * 减少库存
     *
     * @param productId
     * @param number
     * @return
     */
    @RequestMapping(value = "/product/decreaseStock", name = "减少库存", method = RequestMethod.POST)
    @ResponseBody
    public Response<Product> decreaseStock(@RequestParam(name = "productId") String productId,
                                           @RequestParam(name = "number") Integer number) {

        Response<Product> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        Product product = productService.findOne(productId);

        if (product == null) {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_NOT_EXIST);
        } else {
            if (product.getProductStock() < number) {
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_STOCK_NOT_ENOUGH);
            } else {
                product.setProductStock(product.getProductStock() - number);
                product = productService.save(product);
                response.setData(product);
                response.setErrorCode(Constant.RESULT_OK);
                response.setErrorMsg(Constant.RESULT_OK_STRING);
            }
        }
        return response;
    }

}
