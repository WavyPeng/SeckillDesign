package com.wavy.dao;

import com.wavy.entity.Goods;
import com.wavy.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    public GoodsVo getGoodsVoByGoodsId();
}