package com.zkp.shop;

import com.zkp.shop.controller.OrderController;
import com.zkp.shop.dto.OrderDto;
import com.zkp.shop.entity.OrderDetail;
import com.zkp.shop.entity.OrderMaster;
import com.zkp.shop.enums.CommentStatusEnum;
import com.zkp.shop.repository.OrderDetailRepository;
import com.zkp.shop.repository.OrderMasterRepository;
import com.zkp.shop.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop
 * @time: 2019/6/28 10:17
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderTest {

    @Autowired
    private OrderController orderController;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Test
    public void create() {
        OrderDto orderDto = new OrderDto();
        orderDto.setBuyerName("小钢炮");
        orderDto.setBuyerAddress("厦门市集美区莲花尚院53号楼1301");
        orderDto.setBuyerPhone("18750945760");
        orderDto.setBuyerOpenid("1");

        //购物车
        List<OrderDetail> orderDetailList = new ArrayList<>();
        OrderDetail o1 = new OrderDetail();
        o1.setProductId("40289f946b97a06e016b97adc13b0006");
        o1.setProductQuantity(20);

        OrderDetail o2 = new OrderDetail();
        o2.setProductId("40289f946b97a06e016b97b54d010018");
        o2.setProductQuantity(20);

        OrderDetail o3 = new OrderDetail();
        o3.setProductId("40289f946b97a06e016b97b71fe0001d");
        o3.setProductQuantity(10);

        OrderDetail o4 = new OrderDetail();
        o4.setProductId("40289f946b97b860016b97ba5b6a0006");
        o4.setProductQuantity(10);

        OrderDetail o5 = new OrderDetail();
        o5.setProductId("40289f946b97b860016b97c070a80017");
        o5.setProductQuantity(10);

        orderDetailList.add(o1);
        orderDetailList.add(o2);
        orderDetailList.add(o3);
        orderDetailList.add(o4);
        orderDetailList.add(o5);

        orderDto.setOrderDetailList(orderDetailList);

        orderController.createOrder(orderDto);
//        Assert.assertNotNull(result);
//        System.out.println(result.toString());
    }

    @Test
    public void commentA() {
        OrderMaster orderMaster = orderMasterRepository.findById("1561689773856882841").get();
        orderMaster.setCommentStatus(CommentStatusEnum.COMMENTED.getCode());
        orderMasterRepository.save(orderMaster);
        List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrderId(orderMaster.getOrderId());
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setCommentStatus(CommentStatusEnum.COMMENTED.getCode());
            orderDetail.setProductComment("东西质量好，配送很快。");
            orderDetailRepository.save(orderDetail);
        }
    }

}
