package com.guyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guyi.entity.Goods;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface GoodsMapper extends BaseMapper<Goods> {

    /** 带库存条件扣减，避免并发超卖；同时累加销量。 */
    @Update("UPDATE goods SET stock = stock - #{quantity}, sales = sales + #{quantity} "
            + "WHERE id = #{goodsId} AND status = 1 AND stock >= #{quantity}")
    int deductStock(@Param("goodsId") Integer goodsId, @Param("quantity") Integer quantity);

    /** 取消订单时回滚库存，销量不减少。 */
    @Update("UPDATE goods SET stock = stock + #{quantity} WHERE id = #{goodsId}")
    int restoreStock(@Param("goodsId") Integer goodsId, @Param("quantity") Integer quantity);
}
