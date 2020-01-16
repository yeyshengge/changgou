package com.changgou.search.service;

/**
 * Created by zps on 2020/1/12 13:14
 */
public interface ESManageService {

    //创建索引加映射
    void createMappingAndIndex();

    //导入全部信息到es
    void importAll();

    //通过spuId查询skuList保存的es
    void importSkuListBySpuId(String spuId);

    //根据spuId从es中删除
    void delSkuListBySpuId(String spuId);
}
