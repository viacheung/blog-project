package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domain.Permission;
import com.example.vo.Result;
import com.example.vo.param.PageParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


public interface PermissionService {

    Result listPermission(PageParam pageParam);

    Result add(Permission permission);

    Result update(Permission permission);

    Result delete(Long id);

}
