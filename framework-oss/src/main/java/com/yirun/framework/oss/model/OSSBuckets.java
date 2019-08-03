package com.yirun.framework.oss.model;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import sun.misc.ClassLoaderUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * @Description : oss上存储的桶的名称,固定只有3个桶，文件夹在各自模块维护
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.oss.enums.OSSBuckets
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public class OSSBuckets {

    static {

        try {
            ClassPathResource resource = new ClassPathResource("OSSBuckets.properties");
            if (resource==null||!resource.exists()) {
                resource = new ClassPathResource("OSSBuckets.properties",OSSBuckets.class);
            }
            Properties bucketsPro = PropertiesLoaderUtils.loadProperties(resource);
            //设置初始值
            setHKJF(bucketsPro.getProperty("bucket_hkjf"));
            setQSH(bucketsPro.getProperty("bucket_qsh"));
            setUNIVERSAL(bucketsPro.getProperty("bucket_universal"));

        } catch (IOException ex) {
            throw new IllegalStateException("Could not load 'OSSProLoader.properties': " + ex.getMessage());
        }
    }


    /**
     * 前生活平台
     */
    public static  String QSH;
    /**
     * 鸿坤金服平台
     */
    public static  String HKJF;
    /**
     * 通用平台
     */
    public static  String UNIVERSAL;

    private static void setQSH(String QSH) {
        OSSBuckets.QSH = QSH;
    }

    private static void setHKJF(String HKJF) {
        OSSBuckets.HKJF = HKJF;
    }

    private static void setUNIVERSAL(String UNIVERSAL) {
        OSSBuckets.UNIVERSAL = UNIVERSAL;
    }
}
