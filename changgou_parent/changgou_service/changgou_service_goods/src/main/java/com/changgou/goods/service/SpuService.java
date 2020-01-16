package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpuService {

    /***
     * 查询所有
     * @return
     */
    List<Spu> findAll();

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Spu findById(String id);

    /***
     * 新增
     * @param goods
     */
    void add(Goods goods);

    /***
     * 修改
     * @param spu
     */
    void update(Goods goods);

    /***
     * 删除
     * @param id
     */
    void delete(String id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Spu> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(Map<String, Object> searchMap, int page, int size);

    /**
     * 根据id查询商品信息
     *
     * @param id
     * @return
     */
    Goods findGoodsById(String id);

    /**
     * 商品审核与自动上架
     * 根据id设置商品的审核信息，
     * 如果是删除状态,则把审核状态设置为已审核，没删除就自动上架
     * 如果是删除状态,则把上下架状态设置为已下架
     *
     * @param id
     */
    void audit(String id);

    /**
     * 商品下架
     *
     * @param id
     */
    void pull(String id);

    /**
     * 商品上架
     *
     * @param id
     */
    void put(String id);

    /**
     * 商品还原
     *
     * @param id
     */
    void restore(String id);

    /**
     * 真正删除
     * @param id
     */
    void realDel(String id);
}
