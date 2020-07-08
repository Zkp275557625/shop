package com.zkp.shop.controller;

import com.zkp.shop.config.Constant;
import com.zkp.shop.entity.Category;
import com.zkp.shop.repository.CategoryRepository;
import com.zkp.shop.response.Response;
import com.zkp.shop.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.controller
 * @time: 2019/6/27 11:09
 * @description:
 */
@RestController
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 添加分类
     *
     * @param categoryName
     * @param categoryType
     * @return
     */
    @RequestMapping(value = "/category/add", name = "添加分类", method = RequestMethod.POST)
    @ResponseBody
    public Response<Category> addCategory(@RequestParam(name = "categoryName") String categoryName,
                                          @RequestParam(name = "categoryType") Integer categoryType) {

        Category category = new Category();
        category.setCategoryType(categoryType);
        category.setCategoryName(categoryName);

        category = categoryRepository.save(category);
        Response<Category> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        response.setData(category);
        response.setErrorCode(Constant.RESULT_OK);
        response.setErrorMsg(Constant.RESULT_OK_STRING);

        return response;
    }

    /**
     * 获取所有商品分类
     *
     * @return
     */
    @RequestMapping(value = "/category/list", name = "获取所有商品分类", method = RequestMethod.GET)
    @ResponseBody
    public Response<List<Category>> getCategories() {
        Response<List<Category>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        List<Category> result = categoryRepository.findAll();
        result.sort(Comparator.comparingInt(Category::getCategoryType));
        response.setData(result);
        response.setErrorCode(Constant.RESULT_OK);
        response.setErrorMsg(Constant.RESULT_OK_STRING);
        return response;
    }

    /**
     * 更新分类信息
     *
     * @param categoryId
     * @param categoryName
     * @param categoryType
     * @return
     */
    @RequestMapping(value = "/category/update", name = "更新商品分类", method = RequestMethod.POST)
    @ResponseBody
    public Response<Category> updateCategory(@RequestParam(name = "categoryId") String categoryId,
                                             @RequestParam(name = "categoryName") String categoryName,
                                             @RequestParam(name = "categoryType") Integer categoryType) {

        Response<Category> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        Category category = categoryRepository.getOne(categoryId);
        if (category == null) {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_CATEGORY_NOT_EXIST);
        } else {
            //分类存在的情况
            category.setCategoryName(categoryName);
            category.setCategoryType(categoryType);
            category = categoryRepository.save(category);
            response.setErrorCode(Constant.RESULT_OK);
            response.setErrorMsg(Constant.RESULT_OK_STRING);
            response.setData(category);
        }
        return response;
    }

}
