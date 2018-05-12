package com.wavy.Exception;

import com.wavy.Result.CodeMsg;
import com.wavy.Result.Result;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常处理器
 * Created by WavyPeng on 2018/5/11.
 */
@ControllerAdvice //以控制器作为切面，aop拦截异常
@ResponseBody
public class GlobalExceptionHandler {

    //拦截所有异常
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request,Exception e){
//        e.printStackTrace();
        if(e instanceof GlobalException) {          //全局异常处理
            GlobalException ex = (GlobalException)e;
            return Result.error(ex.getCm());

        }else if(e instanceof BindException) {      //绑定异常处理
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));

        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}