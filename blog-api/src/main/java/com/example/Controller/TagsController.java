package com.example.Controller;

import com.example.service.TagService;
import com.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tags")
public class TagsController {
    @Autowired
    private TagService tagService;
    @GetMapping("/hot")
    public Result listHotTags(){
        int limit=6;
        return tagService.hot(limit);
    }

    /**
     * // 对应前端的写文章时候的文章类比下拉框
     * @return
     */
    @GetMapping
    public Result findAll(){
        return tagService.findAll();
    }


    /**
     * 标签详情
     * @return
     */
    @GetMapping("detail")
    public Result findAllDetail(){
        return tagService.findAll();
    }
    @GetMapping("detail/{id}")
    public Result findDetailById(@PathVariable("id") Long id){
        return tagService.findDetailById(id);
    }

}
