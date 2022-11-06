package com.example.service;

import com.example.vo.CategoryVo;
import com.example.vo.Result;

public interface CategoryService {
    CategoryVo findCategoryByCategoryId(Long categoryId);

    Result findAll();

    Result categoriesDetailById(Long id);
}
