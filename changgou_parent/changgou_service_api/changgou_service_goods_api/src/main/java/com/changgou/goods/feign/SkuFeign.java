package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Created by zps on 2020/1/12 9:00
 */
@FeignClient(name = "goods")
public interface SkuFeign {

    /**
     *
     * @param spuId
     * @return
     */
    @GetMapping(value = "/sku/spu/{spuId}")
    public List<Sku> findList(@PathVariable("spuId") String spuId);
}
