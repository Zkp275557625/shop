package com.zkp.shop.controller;

import com.zkp.shop.config.Constant;
import com.zkp.shop.dto.OrderDto;
import com.zkp.shop.entity.OrderDetail;
import com.zkp.shop.entity.OrderMaster;
import com.zkp.shop.entity.Product;
import com.zkp.shop.enums.CommentStatusEnum;
import com.zkp.shop.enums.OrderStatusEnum;
import com.zkp.shop.enums.PayStatusEnum;
import com.zkp.shop.repository.OrderDetailRepository;
import com.zkp.shop.repository.OrderMasterRepository;
import com.zkp.shop.response.Response;
import com.zkp.shop.service.ProductService;
import com.zkp.shop.utils.CookieUtil;
import com.zkp.shop.utils.KeyUtil;
import com.zkp.shop.utils.TimeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.PageRequest.of;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.controller
 * @time: 2019/6/28 9:45
 * @description:
 */
@RestController
public class OrderController {

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/order/create", name = "创建订单", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Response<OrderDto> createOrder(@RequestParam(name = "orderDto") OrderDto orderDto,
                                          HttpServletRequest httpServletRequest) {
        Response<OrderDto> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(orderDto.getBuyerPhone())) {
                //生成订单编号
                String orderId = KeyUtil.genUniqueKey();
                BigDecimal orderPrice = new BigDecimal(BigInteger.ZERO);

                //1. 查询商品（数量, 价格）
                for (OrderDetail orderDetail : orderDto.getOrderDetailList()) {
                    Product product = productService.findOne(orderDetail.getProductId());
                    if (product == null) {
                        response.setErrorCode(Constant.RESULT_ERROR);
                        response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_NOT_EXIST);
                        break;
                    } else {
                        //2. 计算订单总价
                        orderPrice = product.getProductPrice()
                                .multiply(new BigDecimal(orderDetail.getProductQuantity()))
                                .add(orderPrice);

                        //订单详情入库
                        orderDetail.setDetailId(KeyUtil.genUniqueKey());
                        orderDetail.setOrderId(orderId);
                        BeanUtils.copyProperties(product, orderDetail);
                        orderDetailRepository.save(orderDetail);
                    }
                }

                //3. 写入订单数据库（orderMaster和orderDetail）
                OrderMaster orderMaster = new OrderMaster();
                orderDto.setOrderId(orderId);
                BeanUtils.copyProperties(orderDto, orderMaster);
                orderMaster.setOrderPrice(orderPrice);
                orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
                orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
                orderMaster = orderMasterRepository.save(orderMaster);

                //4. 扣库存
                for (OrderDetail orderDetail : orderDto.getOrderDetailList()) {
                    Product product = productService.findOne(orderDetail.getProductId());
                    if (product.getProductStock() < orderDetail.getProductQuantity()) {
                        response.setErrorCode(Constant.RESULT_ERROR);
                        response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_STOCK_NOT_ENOUGH);
                        break;
                    } else {
                        product.setProductStock(product.getProductStock() - orderDetail.getProductQuantity());
                        productService.save(product);
                    }
                }

                BeanUtils.copyProperties(orderMaster, orderDto);
                orderDto.setOrderDetailList(orderDetailRepository.findAllByOrderId(orderId));
                response.setData(orderDto);
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

    @Transactional(rollbackFor = Exception.class)
    public Response<OrderDto> createOrder(OrderDto orderDto) {
        Response<OrderDto> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //生成订单编号
        String orderId = KeyUtil.genUniqueKey();
        BigDecimal orderPrice = new BigDecimal(BigInteger.ZERO);

        //1. 查询商品（数量, 价格）
        for (OrderDetail orderDetail : orderDto.getOrderDetailList()) {
            Product product = productService.findOne(orderDetail.getProductId());
            if (product == null) {
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_NOT_EXIST);
                break;
            } else {
                //2. 计算订单总价
                orderPrice = product.getProductPrice()
                        .multiply(new BigDecimal(orderDetail.getProductQuantity()))
                        .add(orderPrice);

                //订单详情入库
                orderDetail.setDetailId(KeyUtil.genUniqueKey());
                orderDetail.setOrderId(orderId);
                BeanUtils.copyProperties(product, orderDetail);
                orderDetailRepository.save(orderDetail);
            }
        }

        //3. 写入订单数据库（orderMaster和orderDetail）
        OrderMaster orderMaster = new OrderMaster();
        orderDto.setOrderId(orderId);
        BeanUtils.copyProperties(orderDto, orderMaster);
        orderMaster.setOrderPrice(orderPrice);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMaster = orderMasterRepository.save(orderMaster);

        //4. 扣库存
        for (OrderDetail orderDetail : orderDto.getOrderDetailList()) {
            Product product = productService.findOne(orderDetail.getProductId());
            if (product.getProductStock() < orderDetail.getProductQuantity()) {
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_PRODUCT_STOCK_NOT_ENOUGH);
                break;
            } else {
                product.setProductStock(product.getProductStock() - orderDetail.getProductQuantity());
                productService.save(product);
            }
        }

        BeanUtils.copyProperties(orderMaster, orderDto);
        orderDto.setOrderDetailList(orderDetailRepository.findAllByOrderId(orderId));
        response.setData(orderDto);
        response.setErrorCode(Constant.RESULT_OK);
        response.setErrorMsg(Constant.RESULT_OK_STRING);
        return response;
    }

    @RequestMapping(value = "/order/cancel", name = "取消订单", method = RequestMethod.POST)
    @ResponseBody
    public Response<OrderDto> cancelOrder(@RequestParam(name = "phone") String phone,
                                          @RequestParam(name = "orderId") String orderId,
                                          HttpServletRequest httpServletRequest) {

        Response<OrderDto> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));

            assert value != null;
            if (value.equals(phone)) {

                OrderMaster orderMaster = orderMasterRepository.getOne(orderId);
                if (orderMaster == null) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NOT_EXIST);
                } else {

                    if (orderMaster.getOrderStatus().equals(OrderStatusEnum.CANCEL.getCode())) {
                        response.setErrorCode(Constant.RESULT_ERROR);
                        response.setErrorMsg(Constant.RESULT_ERROR_ORDER_ALREADY_CANCEL);
                    } else if (orderMaster.getOrderStatus().equals(OrderStatusEnum.FINISHED.getCode())) {
                        response.setErrorCode(Constant.RESULT_ERROR);
                        response.setErrorMsg(Constant.RESULT_ERROR_ORDER_ALREADY_FINISHED);
                    } else {
                        orderMaster.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
                        orderMaster = orderMasterRepository.save(orderMaster);

                        OrderDto orderDto = new OrderDto();
                        BeanUtils.copyProperties(orderMaster, orderDto);
                        orderDto.setOrderDetailList(orderDetailRepository.findAllByOrderId(orderId));

                        response.setData(orderDto);
                        response.setErrorCode(Constant.RESULT_OK);
                        response.setErrorMsg(Constant.RESULT_OK_STRING);
                    }
                }

            } else {
                //还没有登录
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
            }
        } else {
            //还没有登录
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
        }
        return response;
    }

    @RequestMapping(value = "/order/finish", name = "完成订单", method = RequestMethod.POST)
    @ResponseBody
    public Response<OrderDto> finishOrder(@RequestParam(name = "phone") String phone,
                                          @RequestParam(name = "orderId") String orderId,
                                          HttpServletRequest httpServletRequest) {

        Response<OrderDto> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));

            assert value != null;
            if (value.equals(phone)) {

                OrderMaster orderMaster = orderMasterRepository.getOne(orderId);
                if (orderMaster == null) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NOT_EXIST);
                } else {

                    if (orderMaster.getOrderStatus().equals(OrderStatusEnum.CANCEL.getCode())) {
                        response.setErrorCode(Constant.RESULT_ERROR);
                        response.setErrorMsg(Constant.RESULT_ERROR_ORDER_ALREADY_CANCEL);
                    } else if (orderMaster.getOrderStatus().equals(OrderStatusEnum.FINISHED.getCode())) {
                        response.setErrorCode(Constant.RESULT_ERROR);
                        response.setErrorMsg(Constant.RESULT_ERROR_ORDER_ALREADY_FINISHED);
                    } else {
                        orderMaster.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
                        orderMaster = orderMasterRepository.save(orderMaster);

                        OrderDto orderDto = new OrderDto();
                        BeanUtils.copyProperties(orderMaster, orderDto);
                        orderDto.setOrderDetailList(orderDetailRepository.findAllByOrderId(orderId));

                        response.setData(orderDto);
                        response.setErrorCode(Constant.RESULT_OK);
                        response.setErrorMsg(Constant.RESULT_OK_STRING);
                    }
                }

            } else {
                //还没有登录
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
            }
        } else {
            //还没有登录
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_LOGIN_FIRST);
        }
        return response;
    }

    /**
     * OrderMaster转换成OrderDto
     *
     * @param orderMaster
     * @return
     */
    private OrderDto convert(OrderMaster orderMaster) {
        OrderDto orderDTO = new OrderDto();
        BeanUtils.copyProperties(orderMaster, orderDTO);
        return orderDTO;
    }


    /**
     * 获取该用户的所有订单列表
     *
     * @param page
     * @param size
     * @param phone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/order/list", name = "获取该用户的所有订单列表", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<OrderDto>> list(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         @RequestParam(name = "phone") String phone,
                                         HttpServletRequest httpServletRequest) {

        Response<Page<OrderDto>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(phone)) {
                //查询该用户的所有订单
                //分页查询
                PageRequest request = of(page, size);
                Page<OrderMaster> orderMasterList =
                        orderMasterRepository.findAllByBuyerPhone(phone, request);

                if (orderMasterList.getTotalElements() == 0) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NO_LIST);
                } else {

                    Page<OrderDto> orderDtoPage =
                            new PageImpl<>(orderMasterList.stream().map(this::convert).collect(Collectors.toList()),
                                    request, orderMasterList.getTotalElements());

                    for (OrderDto orderDto : orderDtoPage.getContent()) {
                        List<OrderDetail> orderDetail = orderDetailRepository.findAllByOrderId(orderDto.getOrderId());
                        orderDto.setOrderDetailList(orderDetail);
                    }
                    response.setData(orderDtoPage);
                    response.setErrorCode(Constant.RESULT_OK);
                    response.setErrorMsg(Constant.RESULT_OK_STRING);
                }
            } else {
                //还没有登录
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
     * 获取该用户的所有已支付的订单列表
     *
     * @param phone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/order/paiedlist", name = "获取该用户的所有已支付的订单列表", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<OrderDto>> listPaied(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size,
                                              @RequestParam(name = "phone") String phone,
                                              HttpServletRequest httpServletRequest) {

        Response<Page<OrderDto>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(phone)) {
                //查询该用户的所有订单
                PageRequest request = of(page, size);
                Page<OrderMaster> orderMasterList =
                        orderMasterRepository.findAllByBuyerPhoneAndPayStatus(phone, PayStatusEnum.SUCCESS.getCode(), request);

                if (orderMasterList.getTotalElements() == 0) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NO_LIST);
                } else {
                    Page<OrderDto> orderDtoPage =
                            new PageImpl<>(orderMasterList.stream().map(this::convert).collect(Collectors.toList()),
                                    request, orderMasterList.getTotalElements());

                    for (OrderDto orderDto : orderDtoPage.getContent()) {
                        List<OrderDetail> orderDetail = orderDetailRepository.findAllByOrderId(orderDto.getOrderId());
                        orderDto.setOrderDetailList(orderDetail);
                    }
                    response.setData(orderDtoPage);
                    response.setErrorCode(Constant.RESULT_OK);
                    response.setErrorMsg(Constant.RESULT_OK_STRING);
                }
            } else {
                //还没有登录
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
     * 获取该用户的所有未支付的订单列表
     *
     * @param phone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/order/unpaiedlist", name = "获取该用户的所有未支付的订单列表", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<OrderDto>> listUnPaied(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                @RequestParam(name = "phone") String phone,
                                                HttpServletRequest httpServletRequest) {

        Response<Page<OrderDto>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(phone)) {
                //查询该用户的所有订单
                PageRequest request = of(page, size);
                Page<OrderMaster> orderMasterList =
                        orderMasterRepository.findAllByBuyerPhoneAndPayStatus(phone, PayStatusEnum.WAIT.getCode(), request);

                if (orderMasterList.getTotalElements() == 0) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NO_LIST);
                } else {
                    Page<OrderDto> orderDtoPage =
                            new PageImpl<>(orderMasterList.stream().map(this::convert).collect(Collectors.toList()),
                                    request, orderMasterList.getTotalElements());

                    for (OrderDto orderDto : orderDtoPage.getContent()) {
                        List<OrderDetail> orderDetail = orderDetailRepository.findAllByOrderId(orderDto.getOrderId());
                        orderDto.setOrderDetailList(orderDetail);
                    }
                    response.setData(orderDtoPage);
                    response.setErrorCode(Constant.RESULT_OK);
                    response.setErrorMsg(Constant.RESULT_OK_STRING);
                }
            } else {
                //还没有登录
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
     * 获取该用户的所有新订单列表
     *
     * @param phone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/order/neworderlist", name = "获取该用户的所有新订单列表", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<OrderDto>> listNewOrder(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                 @RequestParam(name = "phone") String phone,
                                                 HttpServletRequest httpServletRequest) {

        Response<Page<OrderDto>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(phone)) {
                //查询该用户的所有订单
                PageRequest request = of(page, size);
                Page<OrderMaster> orderMasterList =
                        orderMasterRepository.findAllByBuyerPhoneAndOrderStatus(phone, OrderStatusEnum.NEW.getCode(), request);

                if (orderMasterList.getTotalElements() == 0) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NO_LIST);
                } else {
                    Page<OrderDto> orderDtoPage =
                            new PageImpl<>(orderMasterList.stream().map(this::convert).collect(Collectors.toList()),
                                    request, orderMasterList.getTotalElements());

                    for (OrderDto orderDto : orderDtoPage.getContent()) {
                        List<OrderDetail> orderDetail = orderDetailRepository.findAllByOrderId(orderDto.getOrderId());
                        orderDto.setOrderDetailList(orderDetail);
                    }
                    response.setData(orderDtoPage);
                    response.setErrorCode(Constant.RESULT_OK);
                    response.setErrorMsg(Constant.RESULT_OK_STRING);
                }
            } else {
                //还没有登录
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
     * 获取该用户的所有已完成订单列表
     *
     * @param phone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/order/finishedlist", name = "获取该用户的所有已完成订单列表", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<OrderDto>> listFinishedOrder(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                      @RequestParam(name = "phone") String phone,
                                                      HttpServletRequest httpServletRequest) {

        Response<Page<OrderDto>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(phone)) {
                //查询该用户的所有订单
                PageRequest request = of(page, size);
                Page<OrderMaster> orderMasterList =
                        orderMasterRepository.findAllByBuyerPhoneAndOrderStatus(phone, OrderStatusEnum.FINISHED.getCode(), request);

                if (orderMasterList.getTotalElements() == 0) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NO_LIST);
                } else {
                    Page<OrderDto> orderDtoPage =
                            new PageImpl<>(orderMasterList.stream().map(this::convert).collect(Collectors.toList()),
                                    request, orderMasterList.getTotalElements());

                    for (OrderDto orderDto : orderDtoPage.getContent()) {
                        List<OrderDetail> orderDetail = orderDetailRepository.findAllByOrderId(orderDto.getOrderId());
                        orderDto.setOrderDetailList(orderDetail);
                    }
                    response.setData(orderDtoPage);
                    response.setErrorCode(Constant.RESULT_OK);
                    response.setErrorMsg(Constant.RESULT_OK_STRING);
                }
            } else {
                //还没有登录
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
     * 获取该用户的所有已取消订单列表
     *
     * @param phone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/order/canceledlist", name = "获取该用户的所有已取消订单列表", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<OrderDto>> listCanceledOrder(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                      @RequestParam(name = "phone") String phone,
                                                      HttpServletRequest httpServletRequest) {

        Response<Page<OrderDto>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(phone)) {
                //查询该用户的所有订单
                PageRequest request = of(page, size);
                Page<OrderMaster> orderMasterList =
                        orderMasterRepository.findAllByBuyerPhoneAndOrderStatus(phone, OrderStatusEnum.CANCEL.getCode(), request);

                if (orderMasterList.getTotalElements() == 0) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NO_LIST);
                } else {
                    Page<OrderDto> orderDtoPage =
                            new PageImpl<>(orderMasterList.stream().map(this::convert).collect(Collectors.toList()),
                                    request, orderMasterList.getTotalElements());

                    for (OrderDto orderDto : orderDtoPage.getContent()) {
                        List<OrderDetail> orderDetail = orderDetailRepository.findAllByOrderId(orderDto.getOrderId());
                        orderDto.setOrderDetailList(orderDetail);
                    }
                    response.setData(orderDtoPage);
                    response.setErrorCode(Constant.RESULT_OK);
                    response.setErrorMsg(Constant.RESULT_OK_STRING);
                }
            } else {
                //还没有登录
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
     * 获取该用户的所有已评论订单列表
     *
     * @param phone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/order/commentlist", name = "获取该用户的所有已评论订单列表", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<OrderDto>> listCommentedOrder(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                       @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                       @RequestParam(name = "phone") String phone,
                                                       HttpServletRequest httpServletRequest) {

        Response<Page<OrderDto>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(phone)) {
                //查询该用户的所有订单
                PageRequest request = of(page, size);
                Page<OrderMaster> orderMasterList =
                        orderMasterRepository.findAllByBuyerPhoneAndCommentStatus(phone, CommentStatusEnum.COMMENTED.getCode(), request);

                if (orderMasterList.getTotalElements() == 0) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NO_LIST);
                } else {
                    Page<OrderDto> orderDtoPage =
                            new PageImpl<>(orderMasterList.stream().map(this::convert).collect(Collectors.toList()),
                                    request, orderMasterList.getTotalElements());

                    for (OrderDto orderDto : orderDtoPage.getContent()) {
                        List<OrderDetail> orderDetail = orderDetailRepository.findAllByOrderId(orderDto.getOrderId());
                        orderDto.setOrderDetailList(orderDetail);
                    }
                    response.setData(orderDtoPage);
                    response.setErrorCode(Constant.RESULT_OK);
                    response.setErrorMsg(Constant.RESULT_OK_STRING);
                }
            } else {
                //还没有登录
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
     * 获取该用户的所有已评论订单列表
     *
     * @param phone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/order/uncommentlist", name = "获取该用户的所有已评论订单列表", method = RequestMethod.GET)
    @ResponseBody
    public Response<Page<OrderDto>> listUnCommentedOrder(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                         @RequestParam(name = "phone") String phone,
                                                         HttpServletRequest httpServletRequest) {

        Response<Page<OrderDto>> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(phone)) {
                //查询该用户的所有订单
                PageRequest request = of(page, size);
                Page<OrderMaster> orderMasterList =
                        orderMasterRepository.findAllByBuyerPhoneAndCommentStatus(phone, CommentStatusEnum.UNCOMMENTED.getCode(), request);

                if (orderMasterList.getTotalElements() == 0) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NO_LIST);
                } else {
                    Page<OrderDto> orderDtoPage =
                            new PageImpl<>(orderMasterList.stream().map(this::convert).collect(Collectors.toList()),
                                    request, orderMasterList.getTotalElements());

                    for (OrderDto orderDto : orderDtoPage.getContent()) {
                        List<OrderDetail> orderDetail = orderDetailRepository.findAllByOrderId(orderDto.getOrderId());
                        orderDto.setOrderDetailList(orderDetail);
                    }
                    response.setData(orderDtoPage);
                    response.setErrorCode(Constant.RESULT_OK);
                    response.setErrorMsg(Constant.RESULT_OK_STRING);
                }
            } else {
                //还没有登录
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
     * 提交评论
     *
     * @param orderDto
     * @param phone
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/order/comment", name = "提交评论", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Response<OrderDto> comment(@RequestParam(name = "orderDto") OrderDto orderDto,
                                      @RequestParam(name = "phone") String phone,
                                      HttpServletRequest httpServletRequest) {

        Response<OrderDto> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(phone)) {
                OrderMaster orderMaster = orderMasterRepository.getOne(orderDto.getOrderId());
                if (orderMaster == null) {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NO_LIST);
                } else {
                    if (orderMaster.getCommentStatus().equals(CommentStatusEnum.COMMENTED.getCode())) {
                        response.setErrorCode(Constant.RESULT_ERROR);
                        response.setErrorMsg(Constant.RESULT_ERROR_ALREADY_COMMENT);
                    } else {
                        BeanUtils.copyProperties(orderDto, orderMaster);
                        for (OrderDetail orderDetail : orderDto.getOrderDetailList()) {
                            orderDetail = orderDetailRepository.getOne(orderDetail.getDetailId());
                            if (orderDetail == null) {
                                response.setErrorCode(Constant.RESULT_ERROR);
                                response.setErrorMsg(Constant.RESULT_ERROR_ORDER_NO_LIST);
                                break;
                            } else {
                                orderDetail.setCommentStatus(CommentStatusEnum.COMMENTED.getCode());
                                orderDetailRepository.save(orderDetail);
                            }
                        }
                        orderMaster.setCommentStatus(CommentStatusEnum.COMMENTED.getCode());
                        orderMaster.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
                        orderMasterRepository.save(orderMaster);
                        response.setData(orderDto);
                        response.setErrorCode(Constant.RESULT_OK);
                        response.setErrorMsg(Constant.RESULT_OK_STRING);
                    }
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
}
