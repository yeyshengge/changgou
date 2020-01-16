package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.search.service.ESManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zps on 2020/1/12 13:44
 */
@RequestMapping("/manage")
@RestController
public class ESManageController {
    @Autowired
    private ESManageService esManageService;

    @GetMapping("/create")
    public Result create(){
        esManageService.createMappingAndIndex();
        return new Result(true, StatusCode.OK,"创建索引映射成功");
    }

    @GetMapping("/importAll")
    public Result importAll(){
        esManageService.importAll();
        return new Result(true, StatusCode.OK,"上传成功");
    }
}
