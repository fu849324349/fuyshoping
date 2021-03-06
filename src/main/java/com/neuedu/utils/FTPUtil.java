package com.neuedu.utils;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {

    //ip   username   password
    /*读取配置文件中对应的吃ip  user  psw*/
    private static final String FTPIP = PropertiesUtils.readByKey("ftp.server.ip");
    private static final String FTPUSER = PropertiesUtils.readByKey("ftp.server.user");
    private static final String FTPPASSWORD = PropertiesUtils.readByKey("ftp.server.password");

    //ip地址
    private String ftpIp;
    //用户名
    private String ftpUser;
    //密码
    private String ftpPassword;
    //端口号
    private Integer port;



    public FTPUtil(String ftpIp,String ftpUser,String ftpPassword,Integer port) {
        this.ftpIp = ftpIp;
        this.ftpUser = ftpUser;
        this.ftpPassword = ftpPassword;
        this.port = port;
    }

    /**
     * 图片上传到FTP
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        /*ftp服务器默认21端口*/
        FTPUtil ftpUtil = new FTPUtil(FTPIP,FTPUSER,FTPPASSWORD,21);

        System.out.println("开始连接FTP服务器...");

        /*把异常抛到业务逻辑层，让业务逻辑层实现*/
        ftpUtil.uploadFile("img",fileList);

        return false;
    }

    public boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        FileInputStream fileInputStream = null;
        //连接ftp服务器
        if (connectFTPServer(ftpIp,ftpUser,ftpPassword)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                //设置目录的缓存区，缓存大小
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                //设置文件类型，二进制
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //打开被动传输模式
                ftpClient.enterLocalPassiveMode();
                //遍历要上传的文件集合
                for (File file : fileList){
                    fileInputStream = new FileInputStream(file);
                    //调用storeFile，将流写到服务器上，是客户端发送连接请求到服务器，服务器被动接受
                    ftpClient.storeFile(file.getName(),fileInputStream);
                }
                System.out.println("====文件上传成功=====");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("文件上传出错...");
            }finally {
                fileInputStream.close();
                ftpClient.disconnect();
            }
        }
        return false;
    }

    /**
     * 连接ftp服务器
     */
     FTPClient ftpClient = null;
    public boolean connectFTPServer(String ip,String user,String password){
        ftpClient = new FTPClient();

        try {
            ftpClient.connect(ip);
            return ftpClient.login(user,password);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("连接FTP服务器异常...");
        }
        return false;
    }

    public String getFtpIp() {
        return ftpIp;
    }

    public void setFtpIp(String ftpIp) {
        this.ftpIp = ftpIp;
    }

    public String getFtpUser() {
        return ftpUser;
    }

    public void setFtpUser(String ftpUser) {
        this.ftpUser = ftpUser;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
