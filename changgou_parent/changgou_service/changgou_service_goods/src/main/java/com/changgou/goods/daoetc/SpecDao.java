package com.changgou.goods.daoetc;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface SpecDao {

    Page<Map> findPage(@Param("templateId") int templateId);
}
