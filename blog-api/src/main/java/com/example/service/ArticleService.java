package com.example.service;

import com.example.domain.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vo.ArticleVo;
import com.example.vo.Result;
import com.example.vo.params.ArticleParam;
import com.example.vo.params.PageParams;

/**
* @author viacheung
* @description 针对表【ms_article】的数据库操作Service
* @createDate 2022-10-06 17:05:32
*/
public interface ArticleService extends IService<Article> {
    /**
     * 分页查询     文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /**
     * 最热文章
     * @param limit
     * @return
     */
    Result hotArticle(int limit);
    /**
     * 最新文章
     * @param limit
     * @return
     */
    Result newArticle(int limit);

    /**
     * 文章归档
     * @return
     */
    Result listArchives();

    ArticleVo findArticleById(Long id);

    Result publish(ArticleParam articleParam);
}
