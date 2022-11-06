package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.domain.Comment;
import com.example.domain.SysUser;
import com.example.mapper.CommentMapper;
import com.example.service.CommentsService;
import com.example.service.SysUserService;
import com.example.utils.UserThreadLocal;
import com.example.vo.CommentVo;
import com.example.vo.Result;
import com.example.vo.UserVo;
import com.example.vo.params.CommentParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {
/*
1．根据文章id查询评论列表(一个文章有很多评论 所以返回List<Comment>)    从comment表中查询
    但是返回数据不同于Comment，所以创建CommentVo  and UserVo(搞个头像 id 名字就可以，感觉后续可以增加一个点头像进主页的功能)
    然后把comment里面属性copy到commentVo里面，不太一样的是作者信息 也就是UserVo，和子评论，还要给谁评论
2．根据作者的id查询作者的信息
3.判断如果level =1要去查询它有没有子评论(子评论可能有多个)
4、如果有根据评论id进行查询(parent_id>
*/
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SysUserService sysUserService;
    @Override
    public Result commentsByArticleId(Long articleId) {
        //mp要用Lambda查
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,articleId);
        queryWrapper.eq(Comment::getLevel,1);
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        //Comment转CommentVo
        return Result.success(CopyList(comments));

    }

    @Override
    public Result comment(CommentParam commentParam) {
        //从UserThreadLocal里面取登录对象
        SysUser sysUser = UserThreadLocal.get();
        Comment comment=new Comment();
        comment.setArticleId(commentParam.getArticleId());
        comment.setContent(commentParam.getContent());
        comment.setAuthorId(sysUser.getId());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParam.getParent();
        if(parent==null||parent==0){
            comment.setLevel(1);
        }else{
            comment.setLevel(2);
        }
        comment.setParentId(parent==null?0:parent);
        Long toUserId = commentParam.getToUserId();
        comment.setToUid(toUserId==null?0:toUserId);
        commentMapper.insert(comment);
        return Result.success(null);
    }

    private List<CommentVo> CopyList(List<Comment> comments) {
        //新建结果集
        List<CommentVo> commentVoList=new ArrayList<>();
        for (Comment comment : comments) {
            commentVoList.add(copy(comment));
        }
        return commentVoList;

    }

    private CommentVo copy(Comment comment) {
        //新建结果集
        CommentVo commentVo=new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        //作者信息
        Long authorId = comment.getAuthorId();
        UserVo userVo=sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);
        //子评论
        Integer level = comment.getLevel();
        if(1==level){
            Long id = comment.getId();
            List<CommentVo> commentVoList=findCommentsByParentId(id);
            commentVo.setChildrens(commentVoList);
        }
        //to user
        if(level>1){
            Long toUid = comment.getToUid();
            UserVo toUseVo=sysUserService.findUserVoById(toUid);
            commentVo.setAuthor(toUseVo);
        }
        return commentVo;
    }

    private List<CommentVo> findCommentsByParentId(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);
        //递归查找
        return CopyList(commentMapper.selectList(queryWrapper));
    }
}
