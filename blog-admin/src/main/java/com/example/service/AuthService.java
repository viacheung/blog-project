package com.example.service;

import com.example.domain.Admin;
import com.example.domain.Permission;
import com.example.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private AdminService adminService;
    public boolean auth(HttpServletRequest request, Authentication authentication){
        String requestURI = request.getRequestURI();
        log.info("request url:{}",requestURI);
        //userdetail信息   当前登录用户信息
        Object principal = authentication.getPrincipal();
        if (principal == null || "anonymousUser".equals(principal)){
            //未登录
            return false;
        }
        //类型转换
        UserDetails userDetails = (UserDetails) principal;
        String username = userDetails.getUsername();
        Admin admin = adminService.findAdminByUserName(username);
        if (admin == null){
            return false;
        }
        //admin=1 所有权限都能用
        if (admin.getId() == 1){
            //认为是超级管理员
            return true;
        }
        //拿到你这个用户的众多权限信息
        List<Permission> permissions = adminService.findPermissionsByAdminId(admin.getId());
        //如果是带参数访问的话 就会有? 所以取问号前面的uri
        requestURI = StringUtils.split(requestURI,'?')[0];
        //只要你这个用户其中的一个权限路径是和requestURI一样的就🆗
        for (Permission permission : permissions) {
            if (requestURI.equals(permission.getPath())){
                log.info("权限通过");
                return true;
            }
        }
        return false;


    }
}
