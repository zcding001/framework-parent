package com.yirun.framework.oss.model;

/**
 * @Description : 文件状态枚举类
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.oss.model.FileState
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public enum FileState {
    /**
     * 未上传
     */
    UN_UPLOAD,
    /**
     * 正在上传
     */
    UPLOADING,
    /**
     * 文件已经遗弃
     */
    ABANDON,
    /**
     * 已保存
     */
    SAVED,
    /**
     * 阿里云上已经是删除状态
     */
    DELETE
}
