package com.changgou.business.service.impl;

import com.changgou.business.dao.AdMapper;
import com.changgou.business.dao.PositionMapper;
import com.changgou.business.pojo.Position;
import com.changgou.business.service.AdService;
import com.changgou.business.pojo.Ad;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class AdServiceImpl implements AdService {

    @Autowired
    private AdMapper adMapper;
    @Autowired
    private PositionMapper positionMapper;

    /**
     * 查询全部列表
     *
     * @return
     */
    @Override
    public List<Ad> findAll() {
        return adMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Ad findById(Integer id) {
        return adMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     *
     * @param ad
     */
    @Override
    public void add(Ad ad) {
        //新增广告默认上线
        ad.setStatus("1");
        adMapper.insert(ad);
    }


    /**
     * 修改
     *
     * @param ad
     */
    @Override
    public void update(Ad ad) {
        adMapper.updateByPrimaryKey(ad);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Integer id) {
        adMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Ad> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return adMapper.selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Ad> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Ad>) adMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param currentPage      页码
     * @param pageSize      页大小
     * @return 分页结果
     */
    @Override
    public Page<Ad> findPage(Map searchMap, int currentPage, int pageSize) {
        PageHelper.startPage(currentPage,pageSize);

        Example example = new Example(Ad.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap!=null){
            String name = (String) searchMap.get("name");
            if (name !=null && !"".equals(name)){
                criteria.andLike("name",name);
            }
            String position = (String) searchMap.get("position");
            if (position!=null && !"".equals(position)){
                criteria.andEqualTo("position",position);
            }
            String endTime = (String) searchMap.get("endTime");
            if (endTime!=null && !"".equals(endTime)){
                if (endTime.equals("1天内")){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+1);
                    Date date = calendar.getTime();
                    criteria.andBetween("endTime",new Date(),date);
                }
                if (endTime.equals("3天内")){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+3);
                    Date date = calendar.getTime();
                    criteria.andBetween("endTime",new Date(),date);
                }
                if (endTime.equals("1周内")){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+7);
                    Date date = calendar.getTime();
                    criteria.andBetween("endTime",new Date(),date);
                }
            }
        }
        Page<Ad> list = (Page<Ad>) adMapper.selectByExample(example);
        return list;
    }

    @Override
    public List<Position> showPosition() {
        List<Position> positions = positionMapper.selectAll();
        return positions;
    }

    /**
     * 轮播图广告置顶
     * @param id
     * @return
     */
    @Transactional
    @Override
    public void goUp(Integer id) {
        Ad ad = adMapper.selectByPrimaryKey(id);
        if (ad==null){
            throw new NullPointerException();
        }
        ad.setSequence(1);
        adMapper.updateByPrimaryKeySelective(ad);

        List<Ad> batchAdList = new ArrayList<>();
        List<Ad> ads = adMapper.selectAll();
        for (Ad adc : ads) {
            if (adc.getId()==id){
                continue;
            }
            adc.setSequence(adc.getSequence()+1);
            batchAdList.add(adc);
        }

        //将sequence加一之后的全部Ad进行批量修改
        adMapper.batchUpDate(batchAdList);
    }

    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Ad.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 广告名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 广告位置
            if (searchMap.get("position") != null && !"".equals(searchMap.get("position"))) {
                criteria.andLike("position", "%" + searchMap.get("position") + "%");
            }
            // 状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andEqualTo("status", searchMap.get("status"));
            }
            // 图片地址
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // URL
            if (searchMap.get("url") != null && !"".equals(searchMap.get("url"))) {
                criteria.andLike("url", "%" + searchMap.get("url") + "%");
            }
            // 备注
            if (searchMap.get("remarks") != null && !"".equals(searchMap.get("remarks"))) {
                criteria.andLike("remarks", "%" + searchMap.get("remarks") + "%");
            }

            // ID
            if (searchMap.get("id") != null) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }

        }
        return example;
    }

}
