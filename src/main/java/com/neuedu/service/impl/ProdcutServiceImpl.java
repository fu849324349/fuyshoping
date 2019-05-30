package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICategoryService;
import com.neuedu.service.IProductService;
import com.neuedu.utils.DateUtils;
import com.neuedu.utils.FTPUtil;
import com.neuedu.utils.PropertiesUtils;
import com.neuedu.vo.ProductDetailVO;
import com.neuedu.vo.ProductListVO;
import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ProdcutServiceImpl implements IProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    ICategoryService categoryService;

    /**
     * 添加或更新商品
     * @param product
     * @return
     */
    @Override
    public ServerResponse saveOrUpdate(Product product) {
        //step1:参数的非空校验
        if (product==null){
            return ServerResponse.creatServerResponseByError("参数为空");
        }
        //step2：设置商品的主图sub_images -->1.jsp,2.jsp,3.jsp(取出三张中的第一张)
        String subImages = product.getSubImages();
        if (subImages!=null && subImages.equals("")){
            String[] subImageArr = subImages.split(",");
            if (subImageArr.length>0){
                //设置商品的主图
                product.setMainImage(subImageArr[0]);
            }
        }
        //step3：商品的save  or  update    并返回结果
        if (product.getId()==null){
            //添加
            int insert = productMapper.insert(product);
            if (insert>0){
                return ServerResponse.creatServerResponseBySuccess();
            }else {
                return ServerResponse.creatServerResponseByError("添加失败");
            }
        }else{
            //更新
            int insert = productMapper.updateByPrimaryKey(product);
            if (insert>0){
                return ServerResponse.creatServerResponseBySuccess();
            }else {
                return ServerResponse.creatServerResponseByError("更新失败");
            }
        }
    }

    /**
     * 商品的上下架
     *
     * @param productId 商品id
     * @param status    商品状态
     * @return
     */
    @Override
    public ServerResponse set_sale_status(Integer productId, Integer status) {

        //1：参数的非空校验
        if (productId==null){
            return ServerResponse.creatServerResponseByError("商品Id参数不能为空");
        }
        if (status==null){
            return ServerResponse.creatServerResponseByError("商品状态参数不能为空");
        }
        //2：更新商品的状态
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int i = productMapper.updateProductSelective(product);
        //3：返回结果
        if (i>0){
            return ServerResponse.creatServerResponseBySuccess();
        }else {
            return ServerResponse.creatServerResponseByError("更新失败");
        }
    }

    /**
     * 商品详情
     *
     * @param productId
     * @return
     */
    @Override
    public ServerResponse detail(Integer productId) {

        //1:参数的校验
        if (productId==null){
            return ServerResponse.creatServerResponseByError("商品id参数不能为空");
        }
        //2：查询product
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.creatServerResponseByError("商品不存在");

        }
        //3：product-->productDetailVO
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
        //4：返回结果
        return ServerResponse.creatServerResponseBySuccess(null,productDetailVO);
    }




    private ProductDetailVO assembleProductDetailVO(Product product){

        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setCreatTime(DateUtils.dateToStr(product.getCreateTime()));
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setImageHost(PropertiesUtils.readByKey("imagehost"));
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setId(product.getId());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setUpdateTime(DateUtils.dateToStr(product.getUpdateTime()));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category!=null){
            productDetailVO.setParentCategoryId(category.getParentId());
        }else{
            //如果都等于空默认根节点
            productDetailVO.setParentCategoryId(0);
        }

        return productDetailVO;
    }

    /**
     * 后台-商品列表，分页
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {

        //在查所有之前加这句话，对应的是select *  from product limit （pageNum-1）*pageSize,pageSize，在selectAll之后就不起作用了
        PageHelper.startPage(pageNum,pageSize);
        //1：查询商品数据
        List<Product> productList = productMapper.selectAll();
        List<ProductListVO> productListVoList = Lists.newArrayList();
        if (productList!=null&&productList.size()>0){
            for (Product product:productList) {
                ProductListVO productListVO = assemProductListVO(product);
                productListVoList.add(productListVO);
            }
        }
        PageInfo<ProductListVO> pageInfo = new PageInfo<>(productListVoList);
        return ServerResponse.creatServerResponseBySuccess(null,pageInfo);
    }

    private ProductListVO assemProductListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());

        return productListVO;
    }

    /**
     * 后台-搜索商品
     *
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse search(Integer productId, String productName,
                                 Integer pageNum, Integer pageSize) {
        //select * from product where productId ? or  productName  like %name%
        // 加了这句话默认查询的时候按limit查询
        PageHelper.startPage(pageNum,pageSize);

        if (productName!=null&&!productName.equals("")){
            productName="%"+productName+"%";
        }
        List<Product> productList = productMapper.findProductByProductIdOrproductName(productId, productName);
        List<ProductListVO> productListVOList = Lists.newArrayList();
        if (productList!=null&&productList.size()>0){
            for (Product product:productList) {
                ProductListVO productListVO = assemProductListVO(product);
                productListVOList.add(productListVO);
            }
        }
        PageInfo<ProductListVO> pageInfo = new PageInfo<>(productListVOList);
        return ServerResponse.creatServerResponseBySuccess(null,pageInfo);
    }

    /**
     * 图片上传
     *
     * @param file
     * @param path
     */
    @Override
    public ServerResponse upload(MultipartFile file, String path) {

        //非空判断
        if (file==null){
            return ServerResponse.creatServerResponseByError();
        }
        //file总不为空   以下为上边的补充  非空判断
        if (file.getOriginalFilename().equals("")){
            return ServerResponse.creatServerResponseByError();
        }
        //1：获取图片名称
        String originalFilename = file.getOriginalFilename();
        //获取图片的扩展名
        String exName = originalFilename.substring(originalFilename.lastIndexOf("."));//.jsp
        //为图片生成新的唯一的名字
        String newFileName = UUID.randomUUID().toString()+exName;
        File pathFile = new File(path);
        if (!pathFile.exists()){
            //把这个文件的权限改为可写的权限
            pathFile.setWritable(true);
            //不存在则创建一个
            pathFile.mkdirs();
        }
        File file1 = new File(path,newFileName);
        try {
            file.transferTo(file1);
            //后期上传到图片服务器
            FTPUtil.uploadFile(Lists.newArrayList(file1));

            Map<String, String> map = Maps.newHashMap();
            map.put("uri",newFileName);
            map.put("url",PropertiesUtils.readByKey("imagehost")+"/"+newFileName);

            //删除应用服务器上的图片
            file1.delete();

            return ServerResponse.creatServerResponseBySuccess(null,map);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * 前台-商品详情
     *
     * @param productId
     * @return
     */
    @Override
    public ServerResponse detail_portal(Integer productId) {

        //1：参数的非空校验
        if (productId == null) {
            return ServerResponse.creatServerResponseByError("商品id参数不能为空");
        }
        //2:查询product
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.creatServerResponseByError("商品不存在");
        }
        //3：校验商品状态
        if (product.getStatus()== Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
            return ServerResponse.creatServerResponseByError("商品已下架或删除");
        }
        //4：获取productDetailVO
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
        //5：返回结果
        return ServerResponse.creatServerResponseBySuccess(null,productDetailVO);
    }

    /**
     * 前台-商品搜索
     *
     * @param categoryId
     * @param keyword
     * @param pageNum
     * @param pagesize
     * @param orderBy
     * @return
     */
    @Override
    public ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pagesize, String orderBy) {

        //1：参数的校验   categoryId和Keyword不能同时为空
        if (categoryId==null&&(keyword==null || keyword.equals(""))){
            return ServerResponse.creatServerResponseByError("参数错误");
        }
        //2：categoryId
        Set<Integer> integerSet =Sets.newHashSet();
        if (categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category==null&&(keyword==null || keyword.equals(""))){
                //说明没有商品数据
                PageHelper.startPage(pageNum,pagesize);
                List<ProductListVO> productListVOList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVOList);
                return ServerResponse.creatServerResponseBySuccess(null,pageInfo);
            }

            ServerResponse serverResponse = categoryService.get_deep_category(categoryId);
            if (serverResponse.isSuccess()){
                integerSet =(Set<Integer>) serverResponse.getData();
            }
        }
        //3：keyword
        if (keyword!=null&&!keyword.equals("")){
            keyword="%"+keyword+"%";
        }
        if(orderBy.equals("")) {
            PageHelper.startPage(pageNum,pagesize);
        } else {
           String[] orderByArr = orderBy.split("_");
           if(orderByArr.length>1) {
               PageHelper.startPage(pageNum,pagesize,orderByArr[0]+" "+orderByArr[1]);
           } else {
               PageHelper.startPage(pageNum,pagesize);
           }
        }
        //4：List<Product> -->  List<ProductList>
        List<Product> productList=productMapper.searchProduct(integerSet,keyword);
        List<ProductListVO> productListVOList = Lists.newArrayList();
        if(productList != null&&productList.size()>0) {
            for(Product product:productList) {
                ProductListVO productListVO = assemProductListVO(product);
                productListVOList.add(productListVO);
            }
        }
        //5:分页
        PageInfo pageInfo = new PageInfo();
        pageInfo.setList(productListVOList);
        //6：返回
        return ServerResponse.creatServerResponseBySuccess(null,pageInfo);
    }


}
