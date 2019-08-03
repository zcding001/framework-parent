package com.yirun.framework.oss;

import com.yirun.framework.oss.model.FileInfo;
import com.yirun.framework.oss.model.FileState;
import com.yirun.framework.oss.model.FileType;
import org.springframework.util.Assert;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description : 文件解析器
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.oss.FileParser
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public class FileParser {

    /**
     *文件后缀  (demo).(png)
     */
    private Pattern fileSuffix = Pattern.compile("(.*)\\.(.*)");

    /**
     * 解析文件信息
     *
     * @param unSaveFileInfo
     * @param useRandomName
     * @param allowUploadType
     * @return
     */
    public FileInfo parseFile(FileInfo unSaveFileInfo, boolean useRandomName, FileType allowUploadType) {
        //前置文件状态
        unSaveFileInfo.setFileState(FileState.ABANDON);
        //检查文件是否存在
        if (unSaveFileInfo == null || (unSaveFileInfo.getUploadType() == 0 && (unSaveFileInfo.getFileContent() == null
                || !unSaveFileInfo.getFileContent().exists())) ||
                (unSaveFileInfo.getUploadType() == 1 && unSaveFileInfo.getFileStream() == null)){
            unSaveFileInfo.setUploadMessage("文件不存在！");
            return unSaveFileInfo;
        }

        String orginName = unSaveFileInfo.getName();
        Assert.notNull(orginName, "文件名称不能为空");

        //解析后缀名
        String suffix = getContentType(orginName);
        boolean needValidteFileType = allowUploadType != FileType.NO_LIMIT;

        if (suffix == null && needValidteFileType) {
            unSaveFileInfo.setUploadMessage("文件后缀名称无法解析");
            //如果解析不了文件后缀名称，直接返回
            return unSaveFileInfo;
        }
        //检查后缀名称
        if (needValidteFileType) {
            String allowSufix = FileType.extType.get(allowUploadType);
            if (!allowSufix.contains(suffix)) {
                unSaveFileInfo.setUploadMessage("请上传 " + allowSufix + "类型文件");
                //如果解析不了文件后缀名称，直接返回
            }
        }

        //重置文件状态
        unSaveFileInfo.setFileState(FileState.UN_UPLOAD);
        if (suffix != null) {
            //给文件设置后缀名称
            unSaveFileInfo.setSuffix(suffix);
        }

        //给未保存的文件设置名称
        unSaveFileInfo.setName(orginName);
        if (useRandomName) {
            unSaveFileInfo.setName(get13UUID() + "." + unSaveFileInfo.getSuffix());
        }
        return unSaveFileInfo;
    }

    /**
    *  @Description    ：获取文件的内容类型
    *  @Method_Name    ：getContentType
    *  @param fileName
    *  @return java.lang.String
    *  @Creation Date  ：2018/4/13
    *  @Author         ：zhongpingtang@hongkun.com.cn
    */
    public String getContentType(String fileName) {

        Matcher suffixMatcher = fileSuffix.matcher(fileName);
        while (suffixMatcher.find()) {
            return suffixMatcher.group(2).toLowerCase();
        }

        return null;
    }


   /**
   *  @Description    ：返回13位长度UUID
   *  @Method_Name    ：get13UUID
   *
   *  @return java.lang.String
   *  @Creation Date  ：2018/4/13
   *  @Author         ：zhongpingtang@hongkun.com.cn
   */
    public static String get13UUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }
}
