package com.changgou.business.dao;

import com.changgou.business.pojo.Ad;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AdMapper extends Mapper<Ad> {

    /**
     * 轮播图广告置顶中的批量修改
     * @param batchAdList
     */
    @Update("<script><foreach collection='batchAdList' item='item' index='index'>update tb_ad set sequence=#{item.sequence} where id=#{item.id};</foreach></script>")
    void batchUpDate(@Param("batchAdList")List<Ad> batchAdList);
}
