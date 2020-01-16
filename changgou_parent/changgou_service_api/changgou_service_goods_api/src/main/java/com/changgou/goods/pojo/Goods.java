package com.changgou.goods.pojo;

/**
 * Created by zps on 2020/1/9 20:01
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品组合实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    private Spu spu;
    private List<Sku> skuList;
    
}
