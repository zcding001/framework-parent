package com.yirun.framework.oss.model;

import java.io.Serializable;

/***
 * @Description
 * @author zhongpingtang
 * @version 0.1
 *
 * */
public class OssConfig implements Serializable {

    private static final long serialVersionUID = 308100507135603850L;

    /**
     *阿里云访问验证相关配置
     */
    private String accessId;
    private String accessKey;
    private String OSSEndpoint;
    /**
     *URL默认失效时延(分钟)
     */
    private static int DEFAULT_EXPIRES = 40;

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getOSSEndpoint() {
        return OSSEndpoint;
    }

    public void setOSSEndpoint(String OSSEndpoint) {
        this.OSSEndpoint = OSSEndpoint;
    }

    public static int getDefaultExpires() {
        return DEFAULT_EXPIRES;
    }

    public static void setDefaultExpires(int defaultExpires) {
        DEFAULT_EXPIRES = defaultExpires;
    }
}
