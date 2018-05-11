package com.wavy.config;

import com.wavy.entity.User;
import com.wavy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义参数解析器
 * Created by WavyPeng on 2018/5/11.
 */
@Service
@Slf4j
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    UserService userService;

    /**
     * 参数类型处理
     * 判断是否支持要转换的参数类型
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz==User.class;  //判断是否是User
    }

    /**
     * 将request中的请求参数解析到当前Controller参数上
     * @param methodParameter 需要被解析的Controller参数，此参数必须首先传给{@link #supportsParameter}并返回true
     * @param modelAndViewContainer 当前request的ModelAndViewContainer
     * @param nativeWebRequest 当前request
     * @param webDataBinderFactory 生成{@link WebDataBinderFactory}实例的工厂
     * @return 解析后的Controller参数
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  @Nullable ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
        //获取request和response
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        //获取token
        String paramToken = request.getParameter(UserService.COOKIE_NAME);
        //获取Cookie中的token
        String cookieToken = getCookieValue(request, UserService.COOKIE_NAME);

        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;

        return userService.getByToken(response, token);
    }

    /**
     * 从Cookie中获取目标值
     * @param request
     * @param cookiName
     * @return
     */
    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[]  cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}