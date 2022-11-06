package com.example.domain.dos;

import lombok.Data;

//dos指的是do们 加s是因为do是关键字不能做类名 也是数据库查出来的对象 但是不需要持久化
@Data
public class Archives {
    private Integer year;
    private Integer month;
    private Long count;
}
