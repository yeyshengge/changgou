package com.changgou.goods.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * Created by zps on 2020/1/9 21:26
 */
@Table(name = "tb_category_brand")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBrand {

    private Integer category_id;
    private Integer brand_id;
}
