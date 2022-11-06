package com.example.utils;

import com.example.domain.SysUser;

public class UserThreadLocal {
    //私有构造 不允许new出来
    private UserThreadLocal(){};
    private static final ThreadLocal<SysUser> LOCAL=new ThreadLocal<>();
    //放
    public static void put(SysUser sysUser){
        LOCAL.set(sysUser);
    }
    //取
    public static SysUser get(){
        return LOCAL.get();
    }
    //删
    public static void remove(){
        LOCAL.remove();
    }
}
