package com.example.service;

import com.example.vo.Result;
import com.example.vo.TagVo;

import java.util.List;

public interface TagService {
    /**
     *
     * @param id
     * @return
     */
    List<TagVo> findTagsByArticleId(Long id);

    /**
     * 最热标签
     * @param limit
     * @return
     */
    Result hot(int limit);

    Result findAll();

    Result findDetailById(Long id);
}
