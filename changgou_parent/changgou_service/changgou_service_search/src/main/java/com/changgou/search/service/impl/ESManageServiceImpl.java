package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.ESManageMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.ESManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zps on 2020/1/12 13:16
 */
@Service
public class ESManageServiceImpl implements ESManageService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private ESManageMapper esManageMapper;

    @Override
    public void createMappingAndIndex() {
        //根据实体类创建索引
        elasticsearchTemplate.createIndex(SkuInfo.class);
        //根据实体类创建映射
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }


    @Override
    public void importAll() {
        List<Sku> skuList = skuFeign.findList("all");
        if (skuList == null || skuList.size() < 0) {
            throw new RuntimeException("当前没有查询到数据，无法导入索引");
        }
        //把skuList装成json
        String json = JSON.toJSONString(skuList);
        List<SkuInfo> list = JSON.parseArray(json, SkuInfo.class);
        //处理规格信息
        for (SkuInfo skuInfo : list) {
            //处理规格信息保存到规格map中
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        //保存到索引库
        esManageMapper.saveAll(list);
    }

    @Override
    public void importSkuListBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findList(spuId);
        if (skuList == null || skuList.size() < 0) {
            throw new RuntimeException("当前没有查询到数据，无法导入索引");
        }
        //把skuList装成json
        String json = JSON.toJSONString(skuList);
        List<SkuInfo> list = JSON.parseArray(json, SkuInfo.class);
        //处理规格信息
        for (SkuInfo skuInfo : list) {
            //处理规格信息保存到规格map中
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        //保存到索引库
        System.out.println("准备存储");
        esManageMapper.saveAll(list);
    }

    @Override
    public void delSkuListBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findList(spuId);
        if (skuList == null || skuList.size() < 0) {
            throw new RuntimeException("当前没有查询到数据，无法导入索引");
        }
        for (Sku sku : skuList) {
            //删除
            esManageMapper.deleteById(Long.parseLong(sku.getId()));
        }
    }
}
