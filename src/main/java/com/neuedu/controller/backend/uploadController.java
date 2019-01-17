package com.neuedu.controller.backend;

import com.neuedu.common.ServerResponse;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/manage/product")
public class uploadController {

    @Autowired
    IProductService productService;

    /**
     * springboot框架，通过提交方式区分调用的那个方法
     * get提交方式，是通过浏览器访问的
     * post提交方式，是通过页面提交来访问的
     * @return
     */

    @RequestMapping(value = "/upload",method = RequestMethod.GET)
    public String upload(){

        return "upload";//逻辑视图   通过前缀+逻辑视图+后缀,返回的字符串必须和HTML页面的名字一致
    }

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload2(@RequestParam(value = "upload_file",required = false)MultipartFile file){


        String path="E:\\photos";
        return productService.upload(file,path);//逻辑视图   通过前缀+逻辑视图+后缀,返回的字符串必须和HTML页面的名字一致
    }

}
