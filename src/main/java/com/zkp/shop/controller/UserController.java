package com.zkp.shop.controller;

import com.zkp.shop.config.Constant;
import com.zkp.shop.entity.User;
import com.zkp.shop.repository.UserRepository;
import com.zkp.shop.response.Response;
import com.zkp.shop.utils.CookieUtil;
import com.zkp.shop.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: hmc
 * @project: shop
 * @package: com.zkp.shop.controller
 * @time: 2019/6/26 14:07
 * @description:
 */
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 注册
     *
     * @param userName
     * @param password
     * @param phone
     * @return
     */
    @RequestMapping(name = "/user/register", method = RequestMethod.POST)
    @ResponseBody
    public Response<User> register(@RequestParam(name = "userName") String userName,
                                   @RequestParam(name = "password") String password,
                                   @RequestParam(name = "phone") String phone) {

        Response<User> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        User user = userRepository.findByUserPhone(phone);
        if (user == null) {
            user = new User();
            user.setUserName(userName);
            user.setUserPassword(password);
            user.setUserPhone(phone);
            user = userRepository.save(user);
            response.setErrorCode(Constant.RESULT_OK);
            response.setErrorMsg(Constant.RESULT_OK_STRING);
            response.setData(user);
        } else {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_ALREADY_REGISTER);
        }

        return response;
    }

    /**
     * 获取用户信息
     *
     * @param phone
     * @return
     */
    @RequestMapping(value = "/user/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public Response<User> getUserInfo(@RequestParam(name = "phone") String phone) {
        Response<User> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        User user = userRepository.findByUserPhone(phone);
        if (user != null) {
            response.setErrorCode(Constant.RESULT_OK);
            response.setErrorMsg(Constant.RESULT_OK_STRING);
            response.setData(user);
        } else {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_USER_NOT_EXIST);
        }

        return response;
    }

    /**
     * 更新用户信息
     *
     * @param phone
     * @param userName
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "/user/updateUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public Response<User> updateUserInfo(@RequestParam(name = "phone") String phone,
                                         @RequestParam(name = "userName") String userName,
                                         HttpServletRequest httpServletRequest) {
        Response<User> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            String value = redisTemplate.opsForValue().get(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            assert value != null;
            if (value.equals(phone)) {
                User user = userRepository.findByUserPhone(phone);
                if (user != null) {
                    user.setUserName(userName);
                    user = userRepository.save(user);
                    response.setErrorCode(Constant.RESULT_OK);
                    response.setErrorMsg(Constant.RESULT_OK_STRING);
                    response.setData(user);
                } else {
                    response.setErrorCode(Constant.RESULT_ERROR);
                    response.setErrorMsg(Constant.RESULT_ERROR_USER_NOT_EXIST);
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
     * 登录
     *
     * @param password
     * @param phone
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public Response<User> login(@RequestParam(name = "password") String password,
                                @RequestParam(name = "phone") String phone,
                                HttpServletResponse httpServletResponse) {

        Response<User> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());
        User user = userRepository.findByUserPhone(phone);
        if (user == null) {
            //用户不存在
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_USER_NOT_EXIST);
        } else {
            if (password.equals(user.getUserPassword())) {
                response.setErrorCode(Constant.RESULT_OK);
                response.setErrorMsg(Constant.RESULT_OK_STRING);
                response.setData(user);

                // 设置token至redis
                String token = UUID.randomUUID().toString();
                Integer expire = Constant.EXPIRE;
                redisTemplate.opsForValue().set(String.format(Constant.TOKEN_PREFIX, token), phone, expire, TimeUnit.SECONDS);
                //设置token至cookie
                CookieUtil.set(httpServletResponse, Constant.TOKEN, token, expire);
            } else {
                response.setErrorCode(Constant.RESULT_ERROR);
                response.setErrorMsg(Constant.RESULT_ERROR_PASSWORD_INCORRECT);
            }
        }

        return response;

    }

    /**
     * 退出登录
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> logout(HttpServletRequest httpServletRequest,
                                   HttpServletResponse httpServletResponse) {

        Response<String> response = new Response<>();
        response.setCurrentTime(TimeUtils.getCurrentTime());

        //从cookie里查询
        Cookie cookie = CookieUtil.get(httpServletRequest, Constant.TOKEN);
        if (cookie != null) {
            //清除redis
            redisTemplate.opsForValue().getOperations().delete(String.format(Constant.TOKEN_PREFIX, cookie.getValue()));
            //清除cookie
            CookieUtil.set(httpServletResponse, Constant.TOKEN, null, 0);

            response.setErrorCode(Constant.RESULT_OK);
            response.setErrorMsg(Constant.RESULT_OK_STRING);
        } else {
            response.setErrorCode(Constant.RESULT_ERROR);
            response.setErrorMsg(Constant.RESULT_ERROR_NO_REDIS);
        }
        return response;

    }

}
