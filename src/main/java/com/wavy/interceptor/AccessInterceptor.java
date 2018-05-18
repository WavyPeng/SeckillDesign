package com.wavy.interceptor;

import com.alibaba.fastjson.JSON;
import com.wavy.Prefix.AccessKey;
import com.wavy.Result.CodeMsg;
import com.wavy.Result.Result;
import com.wavy.annotation.AccessLimit;
import com.wavy.entity.User;
import com.wavy.redis.RedisService;
import com.wavy.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 访问拦截器
 * Created by WavyPeng on 2018/5/18.
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter{

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    /**
     * 方法执行之前做一个拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            User user = getUser(request,response);
            UserContext.setUser(user);  // 将用户保存起来
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取方法上的注解
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            // 如果没有注解
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            // 判断是否需要登录
            if(needLogin) {
                if(user == null) {  // 未登录
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            }

            // 创建AccessKey前缀
            AccessKey accessKey = AccessKey.withExpire(seconds);
            // 查询访问次数
            Integer count = redisService.get(accessKey, key, Integer.class);
            if(count == null) {  // 说明首次访问
                redisService.set(accessKey, key, 1);
            }else if(count < maxCount) {
                redisService.incr(accessKey, key);
            }else {  // 访问超限
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取用户信息
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(UserService.COOKIE_NAME);
        String cookieToken = getCookieValue(request, UserService.COOKIE_NAME);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return userService.getByToken(response, token);
    }

    /**
     * 获取Cookie值
     * @param request
     * @param cookiName
     * @return
     */
    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[]  cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 渲染界面
     * @param response
     * @param cm
     * @throws Exception
     */
    private void render(HttpServletResponse response, CodeMsg cm)throws Exception {
        // 注意这里要指定输出编码方式，不然会出现乱码
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}