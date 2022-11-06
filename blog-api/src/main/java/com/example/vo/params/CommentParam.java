package com.example.vo.params;

import lombok.Data;
//前端传参
@Data
public class CommentParam {

    private Long articleId;

    private String content;

    private Long parent;

    private Long toUserId;
}
