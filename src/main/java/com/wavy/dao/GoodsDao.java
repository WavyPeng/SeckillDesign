package com.wavy.dao;

import com.wavy.entity.SeckillGoods;
import com.wavy.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,sg.stock_count, sg.start_date, sg.end_date,sg.seckill_price " +
            "from t_seckill_goods sg " +
            "left join t_goods g " +
            "on sg.goods_id = g.id")
    public List<GoodsVo> getGoodsVoList();

    @Select("select g.*,sg.stock_count, sg.start_date, sg.end_date,sg.seckill_price " +
            "from t_seckill_goods sg " +
            "left join t_goods g " +
            "on sg.goods_id = g.id " +
            "where g.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);

    @Update("update t_seckill_goods " +
            "set stock_count = stock_count - 1 " +
            "where goods_id = #{goodsId}")
    public int reduceStock(SeckillGoods seckillGoods);
}