package com.wavy.Result;

/**
 * 消息码处理
 * Created by WavyPeng on 2018/5/11.
 */
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result(int code) {
        this.code = code;
    }

    private Result(String msg) {
        this.msg = msg;
    }

    private Result(T data) {
        this.data = data;
    }

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private Result(CodeMsg codeMsg){
        if(codeMsg!=null){
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功时调用
     * @param data
     * @param <T>
     * @return
     */
    public static  <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    /**
     * 失败时调用
     * @param codeMsg
     * @param <T>
     * @return
     */
    public static  <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }
}