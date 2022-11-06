package com.example.Controller;
import com.example.service.CategoryService;
import com.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * //对应前端的写文章时候的文章类比下拉框
     * @return
     */
    @GetMapping
    public Result listCategory() {
        return categoryService.findAll();
    }

    /**
     * 分类详情
     * @return
     */
    @GetMapping("detail")
    public Result categoriesDetail(){
        return categoryService.findAll();
    }

    /**
     * 分类文章列表  根据分类Id查对应Id
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public Result categoriesDetailById(@PathVariable("id") Long id){
        return categoryService.categoriesDetailById(id);
    }


}
