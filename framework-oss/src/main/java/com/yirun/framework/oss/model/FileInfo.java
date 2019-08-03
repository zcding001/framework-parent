package com.yirun.framework.oss.model;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @Description : 上传文件的信息
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.oss.model.FileInfo
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 308100507135603850L;
    private static final String SEPARATOR = "/";
    /**
     *文件名称
     */
    private String name;
    /**
     *文件名后缀
     */
    private String suffix;
    /**
     *文件内容
     */
    private File fileContent;
    /**
     *桶名称
     */
    private String bucketName;
    /**
     *文件路径
     */
    private String filePath;
    /**
     *代表文件状态的封装
     */
    private volatile FileState fileState;

    /**
     *上传后文件的名称(带路径)
     */
    private String saveKey;

    /**
     *文件上传信息
     */
    private String uploadMessage;

    /**
     *文件流
     */
    private InputStream fileStream;

    /**
     *上传方式：0-本地文件上传，1-文件流上传
     */
    private int uploadType;

    public FileInfo(InputStream fileStream) {
        this.fileStream = fileStream;
        this.uploadType = 1;
    }

    public FileInfo(File fileContent) {
        this.fileContent = fileContent;
        this.uploadType = 0;
        setName(fileContent.getName());
    }

    public FileInfo(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public File getFileContent() {
        return fileContent;
    }

    public void setFileContent(File fileContent) {
        this.fileContent = fileContent;
    }

    public FileState getFileState() {
        return fileState;
    }

    public void setFileState(FileState fileState) {
        this.fileState = fileState;
    }

    public String getUploadMessage() {
        return uploadMessage;
    }

    public void setUploadMessage(String uploadMessage) {
        this.uploadMessage = uploadMessage;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public InputStream getFileStream() {
        return fileStream;
    }

    public void setFileStream(InputStream fileStream) {
        this.fileStream = fileStream;
    }

    public int getUploadType() {
        return uploadType;
    }

    public void setUploadType(int uploadType) {
        this.uploadType = uploadType;
    }

    /**
    *  @Description    ：获取保存信息的存储路径
    *  @Method_Name    ：getSaveKey
    *
    *  @return java.lang.String
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public String getSaveKey() {
        if (this.fileState != FileState.ABANDON) {
            String UnSaveFilePath = filePath;
            if (UnSaveFilePath.startsWith(SEPARATOR)) {
                //不允许有头
                UnSaveFilePath = UnSaveFilePath.substring(1);
            }
            if (!UnSaveFilePath.endsWith(SEPARATOR)) {
                //必须有结尾
                UnSaveFilePath = UnSaveFilePath.concat(SEPARATOR);
            }
            return UnSaveFilePath + name;
        }
        return null;
    }

    public void setSaveKey(String saveKey) {
        this.saveKey = saveKey;
    }

    /**
    *  @Description    ：直接获取savekey
    *  @Method_Name    ：getSaveKeySetted
    *
    *  @return java.lang.String
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public String getSaveKeySetted(){
        return this.saveKey;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", suffix='" + suffix + '\'' +
                ", fileContent=" + fileContent +
                ", bucketName='" + bucketName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileState=" + fileState +
                ", uploadMessage='" + uploadMessage + '\'' +
                '}';
    }
}
