package com.example.service;

import com.example.vo.Result;
import com.example.vo.params.CommentParam;

public interface CommentsService {
    Result commentsByArticleId(Long articleId);

    Result comment(CommentParam commentParam);
}
