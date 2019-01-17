package com.neuedu.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 响应前端的高复用对象
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)//排除null值
public class ServerResponse<T>{

    private Integer status;//响应状态码,当值为0时3就是成功，非0为失败

    private  T data;//是一个泛型的变量，因为data可能是一个对象，可能是一个字符串类型或是数组
                        //当status=0时data对应的接口响应的数据

    private String msg;//接口的提示信息

    //把构造方法定义为私有的，在其他类中就不能在new这个ServerResponse了
   //无参
    private ServerResponse(){}

    //只有状态码的构造器
    private ServerResponse(Integer status){
        this.status = status;
    }

    //含有状态码和提示信息的构造器，响应失败的时候调用
    private ServerResponse(Integer status,String msg){
        this.status = status;
        this.msg = msg;
    }

    //含有状态码、提示信息和data的构造器
    private ServerResponse(Integer status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 判断接口是否访问成功
     */
    @JsonIgnore
    public boolean isSuccess(){

        return this.status==ResponseCode.SUCCESS;
    }

    //返回修饰符

    /**
     *成功往前端返回了值，就直接返回一个新的ServerResponse()
     * 返回{"status":0}
     * @return
     *当接口调用成功并返回{"status":0}这种格式就调用这个方法
     */
    public static ServerResponse creatServerResponseBySuccess(){
        return new ServerResponse(ResponseCode.SUCCESS);
    }

    /**
     * {"statud":0,"msg":"aaa"}
     * @param msg
     * @return
     */
    public static ServerResponse creatServerResponseBySuccess(String msg){
        return new ServerResponse(ResponseCode.SUCCESS,msg);
    }

    /**
     * {"status":0,"msg":"asa","data":{泛型}}
     * @param msg
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ServerResponse creatServerResponseBySuccess(String msg,T  data){
        return new ServerResponse(ResponseCode.SUCCESS,msg,data);
    }

    /**
     * {"status":1}
     * @return
     */
    public static ServerResponse creatServerResponseByError(){

        return new ServerResponse(ResponseCode.ERROR);
    }

    /**
     * {"status":custom(可自定义status的状态是成功还是失败)}
     * @param status
     * @return
     */
    public static ServerResponse creatServerResponseByError(Integer status){

        return new ServerResponse(status);
    }

    /**
     * {"statud":1,"msg":"aaa"}
     * @param msg
     * @return
     */
    public static ServerResponse creatServerResponseByError(String msg){
        return new ServerResponse(ResponseCode.ERROR,msg);
    }

    /**
     * {"status":custom(可自定义status的状态是成功还是失败),"mag":"aaa"}
     * @param status
     * @param msg
     * @return
     */
    public static ServerResponse creatServerResponseByError(Integer status,String msg){

        return new ServerResponse(status,msg);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
