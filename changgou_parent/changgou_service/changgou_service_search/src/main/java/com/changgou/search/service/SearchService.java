package com.changgou.search.service;

import java.util.Map;

/**
 * Created by zps on 2020/1/14 17:17
 */
public interface SearchService {

    //商品多条件查询
    public Map search(Map<String, String> map);
}
