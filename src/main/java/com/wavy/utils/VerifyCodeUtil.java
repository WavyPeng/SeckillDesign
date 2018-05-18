package com.wavy.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Random;

/**
 * 生成验证码工具类
 * Created by WavyPeng on 2018/5/18.
 */
public class VerifyCodeUtil {
    /**
     * + - *
     */
    private static char[] ops = new char[]{'+','-','*'};

    /**
     * 生成数学公式
     * @param random
     * @return
     */
    public static String generateVerifyCode(Random random){
        // 随机生成操作数
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        // 随机生成符号
        char op1 = ops[random.nextInt(3)];
        char op2 = ops[random.nextInt(3)];
        String expression = "" + num1 + op1 + num2 + op2 + num3;
        return expression;
    }

    /**
     * 计算表达式结果
     * @param exp
     * @return
     */
    public static int calculate(String exp){
        try{
            // 创建ScriptEngineManager
            ScriptEngineManager manager = new ScriptEngineManager();
            // 获取ScriptEngine对象
            ScriptEngine engine = manager.getEngineByName("js");
            return (Integer)engine.eval(exp);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

}