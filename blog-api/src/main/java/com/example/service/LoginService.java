package com.example.service;

import com.example.domain.SysUser;
import com.example.vo.Result;
import com.example.vo.params.LoginParam;
import org.springframework.transaction.annotation.Transactional;
//加上事务操作
@Transactional
public interface LoginService {
    /**
     * 登录功能
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    /**
     * 退出登录
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 注册
     * @param loginParam
     * @return
     */
    Result register(LoginParam loginParam);

    SysUser checkToken(String token);
}
