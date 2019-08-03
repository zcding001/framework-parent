package com.yirun.framework.oss.model;

import java.util.HashMap;

/**
 * @Description : 文件类型的枚举类
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.oss.model.FileType
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public enum FileType {
    /**
     * 图片类型
     */
    EXT_TYPE_IMAGE("image"),
    /**
     * flash类型
     */
    EXT_TYPE_FLASH("flash"),
    /**
     * 媒体类型
     */
    EXT_TYPE_MEDIA("media"),
    /**
     * 文件类型
     */
    EXT_TYPE_FILE("file"),
    /**
     * 没有限制
     */
    NO_LIMIT("noLimit");

    String fileTypeName;

    FileType(String fileTypeName) {
        this.fileTypeName = fileTypeName;
    }

    /**
     * 初始化的文件类型
     */
    public static final HashMap<FileType, String> extType = new HashMap<FileType, String>() {
        {
            put(EXT_TYPE_IMAGE, "gif,jpg,jpeg,png,bmp");
            put(EXT_TYPE_FLASH, "swf,flv");
            put(EXT_TYPE_MEDIA, "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
            put(EXT_TYPE_FILE, "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");
        }
    };

    public String getFileTypeName() {
        return fileTypeName;
    }
}
