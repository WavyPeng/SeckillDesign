package com.wavy.Exception;

import com.wavy.Result.CodeMsg;

/**
 * 全局异常
 * Created by WavyPeng on 2018/5/11.
 */
public class GlobalException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}