package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.domain.Article;
import com.example.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {
    @Autowired
    private ArticleMapper articleMapper;
    @Async("taskExecutor")
    public void updateArticleViewCount(Article article) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,article.getId());
        //此处保证线程安全  乐观锁
        queryWrapper.eq(Article::getViewCounts,article.getViewCounts());
        article.setViewCounts(article.getViewCounts()+1);
        //只要是articleMapper就是对Article所做的操作;
        articleMapper.update(article,queryWrapper);
    }
}
