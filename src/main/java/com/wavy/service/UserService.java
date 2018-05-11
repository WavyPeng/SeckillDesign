package com.wavy.service;

import com.wavy.Exception.GlobalException;
import com.wavy.Result.CodeMsg;
import com.wavy.dao.UserDao;
import com.wavy.entity.User;
import com.wavy.utils.MD5Util;
import com.wavy.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(long id){
        return userDao.getById(id);
    }

    /**
     * 登录
     * @param loginVo
     */
    public boolean login(LoginVo loginVo) {
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

        return true;
    }
}