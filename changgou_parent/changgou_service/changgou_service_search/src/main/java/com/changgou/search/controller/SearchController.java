package com.changgou.search.controller;


import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * Created by zps on 2020/1/15 13:21
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public Map search(@RequestParam Map<String, String> map) {
        //处理特殊符号
        this.handSearchMap(map);
        Map search = searchService.search(map);
        return search;
    }

    private void handSearchMap(Map<String, String> map) {

        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (entry.getKey().startsWith("spec_")) {
                map.put(entry.getKey(), entry.getValue().replace("+", "%2B"));
            }
        }

    }
}
