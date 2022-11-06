package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.domain.Tag;
import com.example.mapper.TagMapper;
import com.example.service.TagService;
import com.example.vo.Result;
import com.example.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    public List<TagVo> copyList(List<Tag> tagList){
        //New一个结果
        List<TagVo> tagVoList=new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(add(tag));
        }
        return tagVoList;
    }

    /**
     * Tag  转  TagVo
     * @param tag
     * @return
     */
    public TagVo add(Tag tag){
        TagVo tagVo=new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }
    @Override
    public List<TagVo> findTagsByArticleId(Long id) {
        List<Tag> tags=tagMapper.findTagsByArticleId(id);
        return copyList(tags);
    }

    @Override
    public Result hot(int limit) {
        //获取最热标签Id
        List<Long> tagIds=tagMapper.findHotsTagIds(limit);
        //判断是否为空
        if(CollectionUtils.isEmpty(tagIds)){
            return Result.success(Collections.emptyList());
        }
        //最终需求的是tagId 和tagName  tag对象(对象里面也可以只封装两个属性)
        List<Tag> tagList=tagMapper.findTagsByTagIds(tagIds);

        return Result.success(tagList);

    }

    @Override
    public Result findAll() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<>());
        return Result.success(copyList(tags));
    }

    @Override
    public Result findDetailById(Long id) {
        Tag tag = tagMapper.selectById(id);
        return Result.success(add(tag));
    }
}
