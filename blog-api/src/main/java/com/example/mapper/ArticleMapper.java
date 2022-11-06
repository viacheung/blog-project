package com.example.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domain.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.domain.dos.Archives;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
* @author viacheung
* @description 针对表【ms_article】的数据库操作Mapper
* @createDate 2022-10-06 17:05:32
* @Entity com.example.domain.Article
*/
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    List<Archives> listArchives();
    IPage<Article> listArticle(Page<Article> page,
                               @Param("categoryId") Long categoryId,
                               @Param("tagId") Long tagId,
                               @Param("year") String year,
                               @Param("month") String month);
}




