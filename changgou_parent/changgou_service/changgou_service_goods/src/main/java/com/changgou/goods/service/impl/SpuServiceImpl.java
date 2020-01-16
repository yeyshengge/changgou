package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.utils.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    /**
     * 查询全部列表
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     *
     * @param goods
     */
    @Override
    @Transactional
    public void add(Goods goods) {
        Spu spu = goods.getSpu();
        long spuId = idWorker.nextId();
        spu.setId(spuId + "");      //设置id
        spu.setStatus("0");         //设置审核状态
        spu.setIsDelete("0");       //设置是否删除
        spu.setIsMarketable("0");   //设置是否上架
        spuMapper.insertSelective(spu);  //保存spu

        this.saveSku(goods);
    }

    /**
     * 保存sku信息
     *
     * @param goods
     */
    private void saveSku(Goods goods) {
        /**
         * 存储数据格式 ：设置sku名称(商品名称 + 规格) 华为 HUAWEI 麦芒7 6G+64G 魅海蓝 全网通  前置智慧双摄  移动联通电信4G手机 双卡双待
         */
        List<Sku> skuList = goods.getSkuList();     //获取sku数据
        Category category = categoryMapper.selectByPrimaryKey(goods.getSpu().getCategory3Id());    //获取分类信息
        Brand brand = brandMapper.selectByPrimaryKey(goods.getSpu().getBrandId());  //获取品牌信息
        //判断品牌表与分类表是否关联
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrand_id(goods.getSpu().getBrandId());
        categoryBrand.setCategory_id(goods.getSpu().getCategory3Id());
        //查询数据库是否存在
        int count = categoryBrandMapper.selectCount(categoryBrand);
        if (count == 0) {
            //不存在，进行添加操作
            categoryBrandMapper.insert(categoryBrand);
        }


        if (skuList != null && skuList.size() > 0) {
            for (Sku sku : skuList) {
                //设置sku主键ID
                sku.setId(idWorker.nextId() + "");
                //设置sku规格
                if (StringUtils.isEmpty(sku.getSpec())) {
                    sku.setSpec("{}");
                }
                //设置sku名称(商品名称 + 规格)
                String name = goods.getSpu().getName();
                String spec = sku.getSpec();
                //将规格json字符串转换成Map
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                for (String value : map.values()) {
                    //数据库中名称都是按空格来间隔
                    name += " " + value;
                }
                sku.setName(name);
                sku.setSpuId(goods.getSpu().getId());//设置spu的ID
                sku.setCreateTime(new Date());//创建日期
                sku.setUpdateTime(new Date());//修改日期
                sku.setCategoryId(category.getId());//商品分类ID
                sku.setCategoryName(category.getName());//商品分类名称
                sku.setBrandName(brand.getName());//品牌名称
                skuMapper.insertSelective(sku);//插入sku表数据
            }
        }
    }


    /**
     * 修改
     *
     * @param goods
     */
    @Override
    public void update(Goods goods) {
        //删除spu再保存
        Spu spu = goods.getSpu();
        spuMapper.deleteByPrimaryKey(spu);
        //删除sku再保存
        Example example = new Example(Sku.class);
        //把spuId的字段全部删除
        example.createCriteria().andEqualTo("spuId", spu.getId());
        skuMapper.deleteByExample(example);
        //进行保存
        this.saveSku(goods);
    }

    /**
     * 逻辑删除
     *
     * @param id
     */
    @Transactional
    @Override
    public void delete(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!spu.getIsMarketable().equals("0")) {
            throw new RuntimeException("商品未下架，请先下架");
        }
        spu.setStatus("0"); //设置为未审核
        spu.setIsDelete("1");   //设置为逻辑删除状态
        spuMapper.updateByPrimaryKeySelective(spu); //执行修改操作，
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Spu>) spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Spu>) spuMapper.selectByExample(example);
    }

    /**
     * 根据id查询商品信息：两个字段
     * 把spu跟sku封装并返回
     *
     * @param id
     * @return
     */
    @Override
    public Goods findGoodsById(String id) {
        Goods goods = new Goods();
        //设置spu信息
        Spu spu = spuMapper.selectByPrimaryKey(id);
        goods.setSpu(spu);
        //设置sku信息
        //定义条件
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        //根据Sku的spuId字段查询
        criteria.andEqualTo("spuId", id);
        List<Sku> skus = skuMapper.selectByExample(example);
        //调取保存sku方法
        goods.setSkuList(skus);
        return goods;
    }

    /**
     * 根据id设置商品的审核信息，
     * 如果是删除状态,则把审核状态设置为已审核，没删除就自动上架
     * 如果是删除状态,则把上下架状态设置为已下架
     *
     * @param id
     */
    @Transactional
    @Override
    public void audit(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new RuntimeException("商品不存在");
        }
        String isDelete = spu.getIsDelete();
        if ("1".equals(isDelete)) {
            //代表已删除
            throw new RuntimeException("商品已删除");
        }
        //没删除设置商品的审核为已审核
        spu.setStatus("1");
        //没删除设置商品自动上架
        spu.setIsMarketable("1");
        //进行更新操作
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品下架
     *
     * @param id
     */
    @Transactional
    @Override
    public void pull(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new RuntimeException("商品不存在");
        }
        String isDelete = spu.getIsDelete();
        if ("1".equals(isDelete)) {
            //代表已删除
            throw new RuntimeException("商品已删除");
        }
        //设置商品已下架
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品上架
     * 已审核的商品才能上架
     *
     * @param id
     */
    @Transactional
    @Override
    public void put(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!spu.getStatus().equals("1")) {
            throw new RuntimeException("商品未审核");
        }
        //进行更新操作
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品还原
     * 商品还原要确定商品已经逻辑删除
     *
     * @param id
     */
    @Transactional
    @Override
    public void restore(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new RuntimeException("商品不存在");
        }
        //判断是否是已删除商品
        if (!spu.getIsDelete().equals("1")) {
            throw new RuntimeException("商品未删除，请先删除");
        }
        //设置参数
        spu.setIsDelete("0");
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 真正删除
     *
     * @param id
     */
    @Transactional
    @Override
    public void realDel(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(spu.getIsDelete())) {
            throw new RuntimeException("商品未删除，请先删除");
        }
        //执行删除操作
        spuMapper.deleteByPrimaryKey(id);

    }

    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 主键
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 货号
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andEqualTo("sn", searchMap.get("sn"));
            }
            // SPU名
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 副标题
            if (searchMap.get("caption") != null && !"".equals(searchMap.get("caption"))) {
                criteria.andLike("caption", "%" + searchMap.get("caption") + "%");
            }
            // 图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // 售后服务
            if (searchMap.get("saleService") != null && !"".equals(searchMap.get("saleService"))) {
                criteria.andLike("saleService", "%" + searchMap.get("saleService") + "%");
            }
            // 介绍
            if (searchMap.get("introduction") != null && !"".equals(searchMap.get("introduction"))) {
                criteria.andLike("introduction", "%" + searchMap.get("introduction") + "%");
            }
            // 规格列表
            if (searchMap.get("specItems") != null && !"".equals(searchMap.get("specItems"))) {
                criteria.andLike("specItems", "%" + searchMap.get("specItems") + "%");
            }
            // 参数列表
            if (searchMap.get("paraItems") != null && !"".equals(searchMap.get("paraItems"))) {
                criteria.andLike("paraItems", "%" + searchMap.get("paraItems") + "%");
            }
            // 是否上架
            if (searchMap.get("isMarketable") != null && !"".equals(searchMap.get("isMarketable"))) {
                criteria.andEqualTo("isMarketable", searchMap.get("isMarketable"));
            }
            // 是否启用规格
            if (searchMap.get("isEnableSpec") != null && !"".equals(searchMap.get("isEnableSpec"))) {
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
            }
            // 审核状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andEqualTo("status", searchMap.get("status"));
            }

            // 品牌ID
            if (searchMap.get("brandId") != null) {
                criteria.andEqualTo("brandId", searchMap.get("brandId"));
            }
            // 一级分类
            if (searchMap.get("category1Id") != null) {
                criteria.andEqualTo("category1Id", searchMap.get("category1Id"));
            }
            // 二级分类
            if (searchMap.get("category2Id") != null) {
                criteria.andEqualTo("category2Id", searchMap.get("category2Id"));
            }
            // 三级分类
            if (searchMap.get("category3Id") != null) {
                criteria.andEqualTo("category3Id", searchMap.get("category3Id"));
            }
            // 模板ID
            if (searchMap.get("templateId") != null) {
                criteria.andEqualTo("templateId", searchMap.get("templateId"));
            }
            // 运费模板id
            if (searchMap.get("freightId") != null) {
                criteria.andEqualTo("freightId", searchMap.get("freightId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
