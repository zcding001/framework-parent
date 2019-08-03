package com.yirun.framework.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.yirun.framework.oss.exception.NoFileInfoBindException;
import com.yirun.framework.oss.exception.NoFileStateInitException;
import com.yirun.framework.oss.model.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description : OSS工具类,提供一系列的工具方法,能够完成将文件上传到阿里云,可使用连缀调用
 * 使用示例
*  FileInfo fileInfo = OSSLoader.getInstance()
* .bindingUploadFile(unUploadFile)
* .setUseRandomName(true)
* .setAllowUploadType(FileType.EXT_TYPE_IMAGE)
* .setFileState(FileState.UN_UPLOAD)
* .setBucketName(OSSBuckets.HKJF)
* .setFilePath(PointConstants.QR_CORD_FILE_PATH)
* .doUpload();
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.oss.OSSLoader
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public class OSSLoader {
    private static final Logger logger = Logger.getLogger(OSSLoader.class);
    /**
     *单例工具类
     */
    private static volatile OSSLoader Instance;
    /**
     *文件解析器
     */
    private static FileParser fileParser;
    /**
     *OSS的客户端
     */
    private OSSClient client = null;

    /**
     * 是否使用随机的名称
     */
    private ThreadLocal<Boolean> useRandomName = ThreadLocal.withInitial(() -> true);

    /**
     * 允许上传的文件类型，默认 FileType.NO_LIMIT 没有限制
     */
    private ThreadLocal<FileType> allowUploadType = ThreadLocal.withInitial(() -> FileType.NO_LIMIT);


    /**
     * 待保存的文件信息
     */
    private ThreadLocal<FileInfo> unSaveFileInfo = new ThreadLocal<>();


    private OSSLoader() {
        /**
         * empty construstor
         */
    }

    public OSSLoader(String ossEndpoint, String accessId, String accessKey) {
        this.client = new OSSClient(ossEndpoint, accessId, accessKey);
    }


    /**
    *  @Description    ：获取OSSLoader的构造器，单例模式
    *  @Method_Name    ：getInstance
    *  @return com.yirun.framework.oss.OSSLoader
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public static OSSLoader getInstance() {
        if (OSSLoader.Instance == null) {
            synchronized (OSSLoader.class) {
                if (OSSLoader.Instance == null) {
                    //初始化
                    OssConfig config = new OSSProLoader().initOSSConfig(new OssConfig());
                    OSSLoader.Instance = new OSSLoader(
                            //oss地址
                            config.getOSSEndpoint(),
                            config.getAccessId(),
                            config.getAccessKey());
                    //初始化文件解析器
                    fileParser = new FileParser();
                }
            }
        }
        return OSSLoader.Instance;
    }

   /**
   *  @Description    ：绑定上传对象
   *  @Method_Name    ：bindingUploadFile
   *  @param fileInfo  文件对象
   *  @return com.yirun.framework.oss.OSSLoader
   *  @Creation Date  ：2018/4/13
   *  @Author         ：zhongpingtang@hongkun.com.cn
   */
    public OSSLoader bindingUploadFile(FileInfo fileInfo) {
        unSaveFileInfo.set(fileInfo);
        return getInstance();
    }

    /**
    *  @Description    ：验证文件的状态
    *  @Method_Name    ：validateFileInfoState
    *  @param fileInfo
    *  @return void
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    private void validateFileInfoState(FileInfo fileInfo){
        if (fileInfo.getFileState() == null){
            throw new NoFileStateInitException();
        }
    }


    /**执行上传文件
    *  @Description    ：
    *  @Method_Name    ：doUpload
    *
    *  @return com.yirun.framework.oss.model.FileInfo
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public FileInfo doUpload() {
        FileInfo fileInfo = volatileCurrentThreadHasContent();
        //校验文件状态是否初始化
        validateFileInfoState(fileInfo);
        logger.debug("开始上传前的文件信息:" + fileInfo);
        if (fileInfo.getFileState().equals(FileState.UN_UPLOAD)) {
            //尝试上传文件
            fileInfo = uploadFile(fileInfo);
            //清除当前线程的绑定对象
            cleanCurrent();
        }
        //删除内存中的临时文件，避免存储过大
        FileUtils.deleteQuietly(fileInfo.getFileContent());
        return fileInfo;

    }


   /**
   *  @Description    ：删除阿里云上的资源
   *  @Method_Name    ：doDelete
   *
   *  @return com.yirun.framework.oss.model.FileInfo
   *  @Creation Date  ：2018/4/13
   *  @Author         ：zhongpingtang@hongkun.com.cn
   */
    public FileInfo doDelete() {
        FileInfo unDeleteFileInfo = volatileCurrentThreadHasContent();
        //校验文件状态是否初始化
        validateFileInfoState(unDeleteFileInfo);
        if (unDeleteFileInfo.getFileState().equals(FileState.SAVED)) {
            unDeleteFileInfo = deleteFile(unDeleteFileInfo);
            //清除当前线程信息
            cleanCurrent();
        }
        return unDeleteFileInfo;
    }


   /**
   *  @Description    ：先删除阿里云上的资源，然后重新上传一个
   *  @Method_Name    ：doDeleteAndUpload
   *
   *  @return com.yirun.framework.oss.model.FileInfo
   *  @Creation Date  ：2018/4/13
   *  @Author         ：zhongpingtang@hongkun.com.cn
   */
    public FileInfo doDeleteAndUpload() {
        FileInfo deleteAndUploadFile = volatileCurrentThreadHasContent();
        //校验文件状态是否初始化
        validateFileInfoState(deleteAndUploadFile);
        //执行删除之后执行上传
        //delete
        deleteAndUploadFile = deleteFile(deleteAndUploadFile);
        if (deleteAndUploadFile.getFileState().equals(FileState.DELETE)) {
            //upload
            deleteAndUploadFile = uploadFile(deleteAndUploadFile);
        }
        //清除当前线程
        cleanCurrent();
        return deleteAndUploadFile;
    }

    /**
    *  @Description    ：绑定buckeName
    *  @Method_Name    ：setBucketName
    *  @param bucketName
    *  @return com.yirun.framework.oss.OSSLoader
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public OSSLoader setBucketName(String bucketName) {
        volatileCurrentThreadHasContent()
                .setBucketName(bucketName);
        return getInstance();
    }

    /**
    *  @Description    ：设置文件路径
    *  @Method_Name    ：setFilePath
    *  @param setFilePath
    *  @return com.yirun.framework.oss.OSSLoader
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public OSSLoader setFilePath(String setFilePath) {
        volatileCurrentThreadHasContent()
                .setFilePath(setFilePath);
        return getInstance();
    }

    /**
    *  @Description    ：设置文件名称
    *  @Method_Name    ：setFileName
    *  @param fileName
    *  @return com.yirun.framework.oss.OSSLoader
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public OSSLoader setFileName(String fileName){
        volatileCurrentThreadHasContent().setName(fileName);
        return getInstance();
    }

    /**
    *  @Description    ：设置文件状态
    *  @Method_Name    ：setFileState
    *  @param fileState
    *  @return com.yirun.framework.oss.OSSLoader
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public OSSLoader setFileState(FileState fileState){
        volatileCurrentThreadHasContent()
                .setFileState(fileState);
        return getInstance();
    }

    /**
    *  @Description    ：设置是否使用随机名称
    *  @Method_Name    ：setUseRandomName
    *  @param useRandomName
    *  @return com.yirun.framework.oss.OSSLoader
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public OSSLoader setUseRandomName(boolean useRandomName) {
        this.useRandomName.set(useRandomName);
        return getInstance();
    }

    /**
    *  @Description    ：设置允许上传的文件类型
    *  @Method_Name    ：setAllowUploadType
    *  @param allowUploadType
    *  @return com.yirun.framework.oss.OSSLoader
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public OSSLoader setAllowUploadType(FileType allowUploadType) {
        this.allowUploadType.set(allowUploadType);
        return getInstance();
    }

    /**
    *  @Description    ：获取某个path下的所有的文件列表
    *  @Method_Name    ：listPathFileName
    *  @param bucketName
    *  @param prefix
    *  @return java.util.List<java.lang.String>
    *  @Creation Date  ：2018/5/4
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public List<String> listPathFileName(String bucketName,String prefix){
        if (!StringUtils.isEmpty(bucketName)&&!StringUtils.isEmpty(prefix)) {
            if (prefix.startsWith("/")) {
                prefix = prefix.substring(1, prefix.length());
            }
            ObjectListing objectListing = null;
            try {
                objectListing = client.listObjects(bucketName,prefix);
            } catch (OSSException e) {
                if ("RequestTimeTooSkewed".equals(e.getErrorCode())) {
                    //ignore...时间的问题,有可能是在测试环境
                    return Collections.emptyList();
                }else{
                    //其他问题抛出
                    throw e;
                }
            }

            List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            if (!CollectionUtils.isEmpty(objectSummaries)) {
                return objectSummaries.stream().map(e -> e.getKey()).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }


    /**
    *  @Description    ：清除当前线程的相关数据
    *  @Method_Name    ：cleanCurrent
    *
    *  @return void
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    private void cleanCurrent() {
        unSaveFileInfo.remove();
        useRandomName.remove();
        allowUploadType.remove();
    }

    /**
    *  @Description    ：验证当前线程是否已经绑定了FileInfo
    *  @Method_Name    ：volatileCurrentThreadHasContent
    *
    *  @return com.yirun.framework.oss.model.FileInfo
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    private FileInfo volatileCurrentThreadHasContent() {
        FileInfo fileInfo = unSaveFileInfo.get();
        //验证当前线程是否有资源绑定
        if (fileInfo == null) {
            throw new NoFileInfoBindException();
        }
        return fileInfo;
    }

    /**
    *  @Description    ：执行上传操作
    *  @Method_Name    ：uploadFile
    *  @param fileInfo
    *  @return com.yirun.framework.oss.model.FileInfo
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    private FileInfo uploadFile(FileInfo fileInfo) {
        fileInfo = fileParser.parseFile(
                //获取当前线程绑定对象
                unSaveFileInfo.get()
                , useRandomName.get(), allowUploadType.get());
        fileInfo.setFileState(FileState.UPLOADING);
        try {
            //本地文件上传
            if (fileInfo.getUploadType() == 0){
                client.putObject(fileInfo.getBucketName(), fileInfo.getSaveKey(), fileInfo.getFileContent());
            }
            //文件流上传
            if (fileInfo.getUploadType() == 1){
                client.putObject(fileInfo.getBucketName(), fileInfo.getSaveKey(),fileInfo.getFileStream());
            }
        } catch (OSSException e) {
            logger.error("上传出现oss异常:", e);
            fileInfo.setUploadMessage("上传错误，请重试,OSSException");
            fileInfo.setFileState(FileState.ABANDON);
            return fileInfo;
        } catch (ClientException e) {
            logger.error("ClientException:", e);
            fileInfo.setUploadMessage("上传错误，请重试,ClientException");
            fileInfo.setFileState(FileState.ABANDON);
            return fileInfo;
        }
        fileInfo.setFileState(FileState.SAVED);
        return fileInfo;

    }

   /**
   *  @Description    ：实际执行的delete
   *  @Method_Name    ：deleteFile
   *  @param unDeleteFileInfo
   *  @return com.yirun.framework.oss.model.FileInfo
   *  @Creation Date  ：2018/4/13
   *  @Author         ：zhongpingtang@hongkun.com.cn
   */
    private FileInfo deleteFile(FileInfo unDeleteFileInfo) {
        try {
            client.deleteObject(unDeleteFileInfo.getBucketName(), unDeleteFileInfo.getSaveKeySetted());
        } catch (OSSException e) {
            logger.error("OSSException:", e);
            unDeleteFileInfo.setFileState(FileState.ABANDON);
            unDeleteFileInfo.setUploadMessage("删除出错，请重试,OSSException");
            return unDeleteFileInfo;
        } catch (ClientException e) {
            logger.error("ClientException:", e);
            unDeleteFileInfo.setFileState(FileState.ABANDON);
            unDeleteFileInfo.setUploadMessage("删除出错，请重试,ClientException");
            return unDeleteFileInfo;
        }
        //设置为已经删除状态
        unDeleteFileInfo.setFileState(FileState.DELETE);
        return unDeleteFileInfo;
    }

    /**
     *  @Description    ：获取key对应的OSSObject
     *  @Method_Name    ：getOSSObject
     *  @param bucket  桶名
     *  @param key  文件路径
     *  @return com.aliyun.oss.model.OSSObject
     *  @Creation Date  ：2018/7/8
     *  @Author         ：pengwu@hongkun.com.cn
     */
    public OSSObject getOSSObject(String bucket,String key){
        return client.getObject(bucket,key);
    }

    /**
     *  @Description    ：判断oss上的文件是否存在
     *  @Method_Name    ：doesObjectExist
     *  @param bucketName  桶的名称
     *  @param key  文件路径
     *  @return boolean
     *  @Creation Date  ：2018/7/18
     *  @Author         ：pengwu@hongkun.com.cn
     */
    public boolean doesObjectExist(String bucketName,String key){
        return client.doesObjectExist(bucketName,key);
    }
}
