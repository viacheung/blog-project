package com.example.service;

import com.example.domain.SysUser;
import com.example.vo.Result;
import com.example.vo.UserVo;

public interface SysUserService {
    SysUser findUserById(Long id);

    /**
     *根据账密查user
     * @return
     */
    SysUser findUser(String account, String pwd);

    Result getUserInfoByToken(String token);


    SysUser findUserByAccount(String account);

    void save(SysUser sysUser);

    UserVo findUserVoById(Long authorId);
}
