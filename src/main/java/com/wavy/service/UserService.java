package com.wavy.service;

import com.wavy.Exception.GlobalException;
import com.wavy.Result.CodeMsg;
import com.wavy.dao.UserDao;
import com.wavy.entity.User;
import com.wavy.redis.RedisService;
import com.wavy.Prefix.UserKey;
import com.wavy.utils.MD5Util;
import com.wavy.utils.UUIDUtil;
import com.wavy.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
public class UserService {

    public static final String COOKIE_NAME = "token";

    @Autowired
    UserDao userDao;

    @Autowired
    RedisService redisService;

    /**
     * 通过id获取用户
     * @param id
     * @return
     */
    public User getById(long id){
        // ---- 添加对象缓存 ----
        // 取缓存
        User user = redisService.get(UserKey.cacheToken,""+id,User.class);
        if(user!=null){
            return user;
        }
        // 取数据库
        user = userDao.getById(id);
        if(user != null){
            //存入缓存
            redisService.set(UserKey.cacheToken,""+id,user);
        }
        return user;
    }

    /**
     * 更新密码
     * @param token
     * @param id
     * @param formPass
     * @return
     */
    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        User user = getById(id);
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        User toBeUpdate = new User();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDbPass(formPass, user.getSalt()));
        userDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(UserKey.cacheToken, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(UserKey.token, token, user);
        return true;
    }

    /**
     * 通过token获取
     * @param token
     * @return
     */
    public User getByToken(HttpServletResponse response,String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        User user = redisService.get(UserKey.token,token,User.class);
        //延长有效期
        if(user!=null){
            addCookie(response,token,user);
        }
        return user;
    }

    private void addCookie(HttpServletResponse response,String token,User user){
        redisService.set(UserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME,token);
        //设置cookie有效期，这里与redis的key的有效期一致
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 登录
     * @param loginVo
     */
    public String login(HttpServletResponse response, LoginVo loginVo) {
        if(loginVo == null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //判断用户是否存在
        User user = getById(Long.parseLong(mobile));
        //如果用户不存在
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();  //获取数据库中的密码
        String dbSalt = user.getSalt();      //获取数据库中的盐值
        String tmpPass = MD5Util.formPassToDbPass(password,dbSalt); //对输入的密码进行MD5加密

        if(!tmpPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);  //由异常处理器进行处理
        }

        //生成Cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return token;
    }
}