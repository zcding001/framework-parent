import com.yirun.framework.oss.OSSLoader;
import com.yirun.framework.oss.model.FileInfo;
import com.yirun.framework.oss.model.FileState;
import com.yirun.framework.oss.model.FileType;
import com.yirun.framework.oss.model.OSSBuckets;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @Description : oss工具测试类
 * @Project : framework-parent
 * @Program Name  : PACKAGE_NAME.OssUtilTest
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public class OssUtilTest {

    /**
     * 测试上传
     * 说明：
     * 1.uploadFile方法中folderCasCade参数的路径格式可以为多样，如 /test1/test2,test1/test2,test1/test2/都可以
     * 2.uploadFile 方法不抛出异常，上传结果通过FileInfo对象传递信息。
     * 3.setUseRandomName/setAllowUploadType 是连缀方法，可以在调用完该方法之后直接进行调用upload方法（如下测试）
     *
     * @throws FileNotFoundException
     */
    @Test
    public void testUpload() throws FileNotFoundException {
        FileInfo fileInfo = OSSLoader.getInstance()
                .setUseRandomName(true)
                .setAllowUploadType(FileType.NO_LIMIT)/*设置允许上传的类型*/
                .bindingUploadFile(new FileInfo(new File("/a.jpg")))
                .setBucketName("test-yr-platform-universal")
                .setFilePath("/test/test5/")
                .doUpload();

        //获取保存的路径名称，格式：test/test2/urxxx2phcsx2.jpg
        System.out.println(fileInfo.getSaveKey());
        System.out.println("fileInfo:" + fileInfo);
    }


    /**
     * 测试OSSBUcket的属性注入
     */
    @Test
    public void testOOSBuckets() {
        System.out.println(OSSBuckets.HKJF);
        System.out.println(OSSBuckets.UNIVERSAL);
        System.out.println(OSSBuckets.QSH);
    }


    /**
     * 测试列出指定路径下的文件
     */
    @Test
    public void testOOSFileList() {
        System.out.println(OSSLoader.getInstance().listPathFileName("yr-news-pr", "/upload/image/"));
    }

    /**
     * 测试删除阿里云记录
     */
    @Test
    public void testDelteFile() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileState(FileState.SAVED);
        fileInfo.setBucketName(OSSBuckets.QSH);
        fileInfo.setSaveKey("agjs-test/info/s0x9ga4f3n8j.jpg");
        OSSLoader.getInstance().bindingUploadFile(fileInfo).doDelete();
    }



}
