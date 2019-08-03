

/**
 * 
 */
package com.yirun.framework.core.utils;

import org.apache.commons.lang.StringUtils;

import com.yirun.framework.core.enums.SystemTypeEnums;

/**
 * @Description : caoxb 类描述：平台转换工具类
 * @Project : framework-core
 * @Program Name : com.yirun.framework.core.utils.PlatformSwitchUtil.java
 * @Author : caoxinbang@hongkun.com.cn 曹新帮
 */

public class PlatformSwitchUtil {

    /**
     * 
     * @param <T>
     * @Description : caoxb 方法描述 根据系统类型 生成新的对应系统 mapper文件namespace名称
     * @Method_Name : getName
     * @param tableName 实体类全包名称 比如 com.yirun.finance.payment.model.FinAccount
     * @return
     * @return : String
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @Creation Date : 2017年6月13日 下午2:04:41
     * @Author : caoxinbang@hongkun.com.cn 曹新帮
     */
    public static <T> String getName(T t) {
        String systemName;
        String tableName;
        try {
            if (t == null) {
                throw new Exception();
            }
            systemName =
                    (String) t.getClass().getSuperclass().getDeclaredField("systemName").get(t);
            tableName = t.getClass().getName();
            if (StringUtils.isBlank(tableName)) {
                return null;
            }
            if (SystemTypeEnums.QKD.getType().equals(systemName)
                    || SystemTypeEnums.HKJF.getType().equals(systemName)) {
                return tableName;
            } else {
                return tableName.substring(0, tableName.lastIndexOf(".") + 1)
                        + StringUtilsExtend.captureName(systemName)
                        + tableName.substring(tableName.lastIndexOf(".") + 1, tableName.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

