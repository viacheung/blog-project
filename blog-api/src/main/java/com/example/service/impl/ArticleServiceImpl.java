package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.Article;
import com.example.domain.ArticleBody;
import com.example.domain.ArticleTag;
import com.example.domain.SysUser;
import com.example.domain.dos.Archives;
import com.example.mapper.ArticleBodyMapper;
import com.example.mapper.ArticleTagMapper;
import com.example.service.*;
import com.example.mapper.ArticleMapper;
import com.example.utils.UserThreadLocal;
import com.example.vo.ArticleVo;
import com.example.vo.Result;
import com.example.vo.TagVo;
import com.example.vo.params.ArticleParam;
import com.example.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author viacheung
* @description 针对表【ms_article】的数据库操作Service实现
* @createDate 2022-10-06 17:05:32
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService{
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ArticleBodyService articleBodyService;
    @Autowired
    private CategoryService categoryService;
    //使用mybatis来实现
/*    @Override
    public Result listArticle(PageParams pageParams) {
        *//**
         * 分页查询article数据库表
         *//*
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //如果传过来的categoryId不为空的话，加上一个一个匹配categoryId的查询条件  查到对应类别的文章
        //我的理解是  http://localhost:8080/#/category/3  里面加了一个http://localhost:8888/articles的接口   点击图标 listArticle和categoriesDetailById两个接口都传参  都查到东西了
        if(pageParams.getCategoryId()!=null){
            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
        }
        //如果传来的标签id不为空
        List<Long> articleIdList=new ArrayList<>();
        if(pageParams.getTagId()!=null){
            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper=new LambdaQueryWrapper<>();
            //在Article-Tag表里面查询到articleTags集合
            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
            for (ArticleTag articleTag : articleTags) {
                articleIdList.add(articleTag.getArticleId());
            }
            if(articleIdList.size()!=0){
                queryWrapper.in(Article::getId,articleIdList);
            }

        }
        //降序排列 order by creat_date desc
        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
        //1 page 2 条件
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        //获取List
        List<Article> records = articlePage.getRecords();
        //实际和页面相关的是VO 所以需要把domain转为VO 也就是把article转为articleVolist
        List<ArticleVo> articleVoList =copyList(records,true,true);

        return Result.success(articleVoList);
    }*/
    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage = this.articleMapper.listArticle(page,pageParams.getCategoryId(),pageParams.getTagId(),pageParams.getYear(),pageParams.getMonth());
        return Result.success(copyList(articleIPage.getRecords(),true,true));
    }

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        //挑选需要的字段 id 和 title
        queryWrapper.select(Article::getId,Article::getTitle);
        //limit后面要有空格
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));

    }

    @Override
    public Result newArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        //挑选需要的字段 id 和 title
        queryWrapper.select(Article::getId,Article::getTitle);
        //limit后面要有空格
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archivesList=articleMapper.listArchives();
        return Result.success(archivesList);
    }
    @Autowired
    private ThreadService threadService;
    @Override
    public ArticleVo findArticleById(Long id) {
        Article article=articleMapper.selectById(id);
        //改变article里面的viewCount
//        查看完文章了，新增阅读数，有没有问题呢?
//查看完文章之后，本应该直接返回数据了，这时候做了一个更新操作，更新时加写锁，阻塞其他的读操作，性能就会比较低//更新增加了此次接口的耗时如果一旦更新出问题，不能影响查看文章的操作
///线程池可以把更新操作扔到线程池中去执行，和主线程就不相关了
        threadService.updateArticleViewCount(article);
        return copy(article, true, true, true, true);
    }
    /*
    1.发布文章,目的 构建Article对象
    2.作者id 当前的登录用户
    3．标签 要将标签加入到    关联列表当中
    4. body 内容存储*/
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    @Override
    public Result publish(ArticleParam articleParam) {
        //1 2
        Article article=new Article();
        //此接口要加入登录拦截器中  在webConfig里面
        SysUser sysUser = UserThreadLocal.get();
//        AuthorId存进去
        article.setAuthorId(sysUser.getId());
        article.setCreateDate(System.currentTimeMillis());
        article.setCategoryId(articleParam.getCategory().getId().intValue());
        article.setCommentCounts(0);
        //这个地方枚举没看懂
        article.setWeight(0);
        article.setTitle(articleParam.getTitle());
        article.setSummary(articleParam.getSummary());
        article.setViewCounts(0);
        //因为后面关联表Article_tag和ArticleBody都需要ArticleId 所以需要Insert一下Article
        articleMapper.insert(article);
        //3
        List<TagVo> tags = articleParam.getTags();
        //封装一个ArticleTag的实体类 对应关联表Article_tag
        if(tags!=null){
            for (TagVo tag : tags) {
                ArticleTag articleTag=new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tag.getId());
                articleTagMapper.insert(articleTag);
            }
        }
        //4
        ArticleBody articleBody=new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);
        //同时设置article的bodyId
        article.setBodyId(articleBody.getId());
        //这里说要更新 但不知道为什么  BodyId初始为Null
        //测试了一下 必须更新，不更新的话可能没有这个数据，数据库是有 但是获取不到id
        articleMapper.updateById(article);

        Map<String,String> map=new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
    }

    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor,boolean isCategory,boolean isArticleBody) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,isCategory,isArticleBody));
        }
        return articleVoList;

    }

    //方法重载
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,false,false));
        }
        return articleVoList;

    }
    private ArticleVo copy(Article article,boolean isTag,boolean isAuthor,boolean isCategory,boolean isArticleBody){
        ArticleVo articleVo = new ArticleVo();
        //使用工具类直接转过来
        BeanUtils.copyProperties(article,articleVo);
        //但是有类型不一样的无法转 只能手动
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //并不是所有接口都需要标签和作者
        if(isTag){
            //文章的tag 根据文章id查标签
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor){
            Long authorId = article.getAuthorId();//得到作者Id
            //查sys_user表 此时authorid就是id  .getNickname获取作者名字 也就是李四
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        //此处ArticleBodyVo 和CategoryVo 从ArticleBody Category里面挑选一部分属性
        if(isArticleBody){
            Long bodyId = article.getBodyId();
            articleVo.setBody(articleBodyService.findArticleBodyByBodyId(bodyId));
        }
        if(isCategory){
            Long categoryId = article.getCategoryId().longValue();
            articleVo.setCategory(categoryService.findCategoryByCategoryId(categoryId));
        }
        return articleVo;
    }
}




