package com.zkp.shop.controller;

import com.zkp.shop.config.Constant;
import com.zkp.shop.entity.DeliveryAddress;
import com.zkp.shop.repository.DeliveryAddressRepository;
import com.zkp.shop.response.Response;
import com.zkp.shop.utils.CookieUtil;
import com.zkp.shop.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.data.domain.PageRequest.of;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.entity
 * @time: 2019/6/28 17:52
 * @description: 收货地址
 */
@RestController
public class DeliveruAddressController {

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 创建收货地址
     *
     * @param ownerPhone
     * @param receiverPhone
     * @param receiverAddress
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/address/create", method = RequestMethod.POST)
    @ResponseBody
    public Response<DeliveryAddress> create(@RequestParam(name = "ownerPhone") String ownerPhone,
                                            @RequestParam(name = "receiverPhone") String receiverPhone,
                                            @RequestParam(name = "receiverAddress") String receiverAddress,
                                            HttpServletRequest httpServletRequest) {

        Response<DeliveryAddress> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(ownerPhone)) {

                DeliveryAddress address = new DeliveryAddress();
                address.setOwnerPhone(ownerPhone);
                address.setReceiverAddress(receiverAddress);
                address.setReceiverPhone(receiverPhone);
                address = deliveryAddressRepository.save(address);

                response.setData(address);
                response.setErrorCode(Constant.RESULT_OK);
                response.setErrorMsg(Constant.RESULT_OK_STRING);

            } else {
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
            }
        } else {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
        }

        return response;
    }

    /**
     * 获取某个用户所有的收货地址
     *
     * @param ownerPhone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/address/list", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<DeliveryAddress>> create(@RequestParam(name = "ownerPhone") String ownerPhone,
                                                  @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                  @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                  HttpServletRequest httpServletRequest) {

        Response<Page<DeliveryAddress>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(ownerPhone)) {

                PageRequest request = of(page, size);
                Page<DeliveryAddress> deliveryAddressPage =
                        deliveryAddressRepository.findAllByOwnerPhone(ownerPhone, request);

                response.setData(deliveryAddressPage);
                response.setErrorCode(Constant.RESULT_OK);
                response.setErrorMsg(Constant.RESULT_OK_STRING);

            } else {
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
            }
        } else {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
        }

        return response;
    }

    /**
     * 修改收货地址
     *
     * @param ownerPhone
     * @param receiverPhone
     * @param receiverAddress
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/address/update", method = RequestMethod.POST)
    @ResponseBody
    public Response<DeliveryAddress> update(@RequestParam(name = "id") String id,
                                            @RequestParam(name = "ownerPhone") String ownerPhone,
                                            @RequestParam(name = "receiverPhone") String receiverPhone,
                                            @RequestParam(name = "receiverAddress") String receiverAddress,
                                            HttpServletRequest httpServletRequest) {

        Response<DeliveryAddress> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(ownerPhone)) {

                DeliveryAddress address = deliveryAddressRepository.getOne(id);
                if (address != null) {

                    address.setReceiverPhone(receiverPhone);
                    address.setReceiverAddress(receiverAddress);
                    address = deliveryAddressRepository.save(address);

                    response.setData(address);
                    response.setErrorCode(Constant.RESULT_OK);
                    response.setErrorMsg(Constant.RESULT_OK_STRING);

                } else {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ADDRESS_NOT_EXIST);
                }

            } else {
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
            }
        } else {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
        }

        return response;
    }

    /**
     * 删除收货地址
     *
     * @param id
     * @param ownerPhone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/address/delete", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> delete(@RequestParam(name = "id") String id,
                                   @RequestParam(name = "ownerPhone") String ownerPhone,
                                   HttpServletRequest httpServletRequest) {

        Response<String> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(ownerPhone)) {

                deliveryAddressRepository.deleteById(id);
                response.setData("删除成功");
                response.setErrorCode(Constant.RESULT_OK);
                response.setErrorMsg(Constant.RESULT_OK_STRING);

            } else {
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
            }
        } else {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
        }

        return response;
    }

}
