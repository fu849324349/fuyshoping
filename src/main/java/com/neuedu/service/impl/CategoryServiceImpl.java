package com.neuedu.service.impl;

import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.pojo.Category;
import com.neuedu.service.ICategoryService;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {

    //注入dao层
    @Autowired
    CategoryMapper categoryMapper;

    /**
     * 获取品类的子节点（平级）
     *
     * @param categoryId
     */
    @Override
    public ServerResponse get_category(Integer categoryId) {
        //step1:非空校验
        if (categoryId==null){
            return ServerResponse.creatServerResponseByError("参数不能为空");
        }
        //step2：根据category查询类别
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category==null){
            return ServerResponse.creatServerResponseByError("查询类别不存在");
        }
        //step3：查询子类别
        List<Category> categoryList = categoryMapper.findChildCategory(categoryId);
        //step4:返回结果
        return ServerResponse.creatServerResponseBySuccess(null,categoryList);
    }

    @Override
    public ServerResponse add_category(Integer parentId, String categoryName) {

        //1:参数校验
        if (categoryName==null || categoryName.equals("")){
            return ServerResponse.creatServerResponseByError("类别名称不能为空");
        }
        //2：添加节点,
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(1);
        int insert = categoryMapper.insert(category);
        //3：返回结果
        if (insert>0){
            return ServerResponse.creatServerResponseBySuccess();
        }
        return ServerResponse.creatServerResponseByError("添加失败");
    }

    /**
     * 修改节点
     *
     * @param categoryId
     * @param categoryName
     */
    @Override
    public ServerResponse set_category_name(Integer categoryId, String categoryName) {

        //1:参数非空检验
        if (categoryId==null || categoryId.equals("")){
            return ServerResponse.creatServerResponseByError("类别id不能为空");
        }
        if (categoryName==null || categoryName.equals("")){
            return ServerResponse.creatServerResponseByError("类别名称不能为空");
        }
        //2：根据cateoryId查询
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category==null){
            return ServerResponse.creatServerResponseByError("要修改的类别不存在");
        }
        //3：修改
        category.setName(categoryName);
        int i = categoryMapper.updateByPrimaryKey(category);
        //4：返回结果
        if (i>0){
            //修改成功
            return ServerResponse.creatServerResponseBySuccess();
        }
        return ServerResponse.creatServerResponseByError("修改失败");
    }

    /**
     * 获取当前分类id及递归子节点categoryId
     *
     * @param categoryId
     */
    @Override
    public ServerResponse get_deep_category(Integer categoryId) {

        //1：参数的非空校验
        if (categoryId==null){
            return ServerResponse.creatServerResponseByError("类别id不能为空");
        }
        //2：查询
        Set<Category> categorySet= Sets.newHashSet();
        categorySet = findAllChildCategory(categorySet, categoryId);
        //通过迭代器拿到所有category下的所有子节点的id
        Set<Integer> integerSet=Sets.newHashSet();

        //迭代器，遍历integerSet集合
        Iterator<Category> categoryIterator = categorySet.iterator();
        while (categoryIterator.hasNext()) {
            Category category = categoryIterator.next();
            integerSet.add(category.getId());
        }
        return ServerResponse.creatServerResponseBySuccess(null,integerSet);
    }
    //封装了递归结束的条件
    private Set<Category> findAllChildCategory(Set<Category> categorySet,Integer categoryId){
        //查找本节点
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            categorySet.add(category);//id一样就认为是同一个类
        }
        //查找categoryId下的子节点（平级）
        List<Category> categoryList = categoryMapper.findChildCategory(categoryId);
        if (categoryList!=null && categoryList.size()>0){
            for (Category category1:categoryList) {
                findAllChildCategory(categorySet,category1.getId());
            }
        }
        return categorySet;
    }
}
