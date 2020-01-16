package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by zps on 2020/1/12 13:13
 */
public interface ESManageMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
