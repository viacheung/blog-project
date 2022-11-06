package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.domain.ArticleBody;
import com.example.mapper.ArticleBodyMapper;
import com.example.service.ArticleBodyService;
import com.example.vo.ArticleBodyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleBodyServiceImpl implements ArticleBodyService {
    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    @Override
    public ArticleBodyVo findArticleBodyByBodyId(Long bodyId) {
        //准备查哪个表 泛型里面就放谁
        LambdaQueryWrapper<ArticleBody> queryWrapper=new LambdaQueryWrapper<>();
        //把Article里面的bodyId和ArticleBody里面的id作对比
        queryWrapper.eq(ArticleBody::getId,bodyId);
        //继承了BaseMapper就可以用selectOne
        ArticleBody articleBody = articleBodyMapper.selectOne(queryWrapper);
        //新建对象
        ArticleBodyVo articleBodyVo=new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }
}
