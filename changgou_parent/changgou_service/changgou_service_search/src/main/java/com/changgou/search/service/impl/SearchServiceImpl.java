package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zps on 2020/1/14 17:20
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    /**
     * 商品多添加查询
     *
     * @param map
     * @return
     */
    @Override
    public Map search(Map<String, String> map) {

        Map<String, Object> searchMap = new HashMap<>();
        if (map != null) {
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            //boolQuery可以多条件查询
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            //按关键字进行模糊查询
            if (StringUtils.isNotEmpty(map.get("keywords"))) {
                //如果map包含该字段就用该字段查询并且是并级查询
                boolQuery.must(QueryBuilders.matchQuery("name", map.get("keywords")).operator(Operator.AND));
            }
            //按品牌进行精确查询
            if (StringUtils.isNotEmpty(map.get("brand"))) {
                //如果map包含该字段就用该字段查询并且是并级查询
                boolQuery.filter(QueryBuilders.termQuery("brandName", map.get("brand")));
            }
            //按规格进行精确查询
            for (String key : map.keySet()) {
                if (key.startsWith("spec_")) {
                    String value = (String) map.get(key);
                    value = value.replace("%2B", "+");
                    boolQuery.filter(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", value));
                }
            }
            //按价格进行分区查询
            if (StringUtils.isNotEmpty(map.get("price"))) {
                if (map.get("price").split("-").length == 2) {
                    //范围查询，如果长度等于二，则代表小于第二个价格
                    boolQuery.filter(QueryBuilders.rangeQuery("price").lt(map.get("price").split("-")[1]));
                }
                //长度为1，则价格大于第一个价格
                boolQuery.filter(QueryBuilders.rangeQuery("price").gt(map.get("price").split("-")[0]));
            }

            nativeSearchQueryBuilder.withQuery(boolQuery);
            //进行聚合查询(当搜索手机时显示手机相关品牌)
            //定义一个列名
            String skuBrand = "skuBrand";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuBrand).field("brandName"));

            //进行聚合查询(当搜索手机时显示手机相关规格信息)
            //定义一个别名
            String skuSpec = "skuSpec";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuSpec).field("spec.keyword"));


            /** 开启查询
             *第一个参数：条件构造
             *第二个参数：操作的实体类
             * 第三个参数：返回结果操作对象
             */
            AggregatedPage<SkuInfo> skuInfo = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                    //操作查询结果
                    List<T> list = new ArrayList<>();
                    SearchHits hits = searchResponse.getHits();
                    if (hits != null) {
                        for (SearchHit hit : hits) {
                            //得到每一个文档信息,解析成对象
                            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                            //添加到集合
                            list.add((T) skuInfo);
                        }
                    }
                    /**
                     * 第一个参数是list(数据)
                     * 第二个参数是分页信息
                     * 第三个参数是总条数
                     * 第四个参数是聚合信息(忽略)
                     */
                    return new AggregatedPageImpl<T>(list, pageable, hits.getTotalHits(), searchResponse.getAggregations());
                }
            });


            //分钟总条数
            searchMap.put("total", skuInfo.getTotalElements());
            //封装总页数
            searchMap.put("totalPages", skuInfo.getTotalPages());
            //封装数据
            searchMap.put("rows", skuInfo.getContent());

            StringTerms brandTerms = (StringTerms) skuInfo.getAggregation(skuBrand);
            //格式数据
            List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            //封装品牌聚合数据
            searchMap.put("brandList", brandList);

            StringTerms specTerms = (StringTerms) skuInfo.getAggregation(skuSpec);
            List<String> specList = specTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            //封装规格聚合数据
            searchMap.put("specList", specList);


        }
        return searchMap;
    }
}
