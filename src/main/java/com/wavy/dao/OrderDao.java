package com.wavy.dao;

import com.wavy.entity.OrderInfo;
import com.wavy.entity.SeckillOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from t_seckill_order " +
            "where user_id = #{userId} and goods_id = #{goodsId}")
    public SeckillOrder getSeckillOrderByUserIdGoodsId(@Param("userId")long userId,@Param("goodsId")long goodsId);

    @Insert("insert into t_order_info" +
            "(user_id, goods_id, goods_name, " +
            "goods_count, goods_price, order_channel, " +
            "status, create_date)" +
            "values" +
            "(#{userId}, #{goodsId}, #{goodsName}, " +
            "#{goodsCount}, #{goodsPrice}, #{orderChannel}," +
            "#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    public long insertOrder(OrderInfo orderInfo);

    @Insert("insert into t_seckill_order " +
            "(user_id, goods_id, order_id)" +
            "values(#{userId}, #{goodsId}, #{orderId})")
    public int insertSeckillOrder(SeckillOrder seckillOrder);
}