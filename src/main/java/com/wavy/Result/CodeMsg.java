package com.wavy.Result;

/**
 * 消息码
 * Created by WavyPeng on 2018/5/11.
 */
public class CodeMsg {
    //消息码
    private int code;
    //消息内容
    private String msg;

    //通用消息码
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101,"参数校验异常：%s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102,"非法请求");
    public static CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500103,"访问太频繁");

    //登录模块 5002XX
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500210, "手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500211, "手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500212, "手机号不存在");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500213, "登录密码不能为空");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500214, "密码错误");
    public static CodeMsg SESSION_ERROR = new CodeMsg(500215, "Session不存在或者已经失效");

    //秒杀模块 5003XX
    public static CodeMsg SECKILL_OVER = new CodeMsg(500300,"商品被其他小伙伴秒杀完了");
    public static CodeMsg SECKILL_REPEAT = new CodeMsg(500301,"不能重复秒杀");
    public static CodeMsg SECKILL_FAIL = new CodeMsg(500302,"秒杀失败");

    //订单模块 5004XX
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400,"订单不存在");

    private CodeMsg(){
    }

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
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

    /**
     * 返回带参数的信息码
     * @param args
     * @return
     */
    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        //将原始的message添加上参数作为新的message
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}