package com.example.vo.params;

import lombok.Data;

@Data
public class LoginParam {
    private String account;
    private String password;
    //注册功能要用
    private String nickname;

}
