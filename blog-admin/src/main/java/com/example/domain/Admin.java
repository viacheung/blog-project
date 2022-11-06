package com.example.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Admin {
//后台管理没必要用分布式id
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;
}
