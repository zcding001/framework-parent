package com.yirun.framework.core.enums;

/**
*  时间周期枚举
*  @date                    ：2018/8/10
*  @author                  ：zc.ding@foxmail.com
*/
public enum PeriodEnum {
    /**
    * 昨天（最近1天）
    */
    LATEST_1_DAYS(1),
    /**
     * 最近3天
     */
    LATEST_3_DAYS(2),
    /**
     * 最近7天
     */
    LATEST_7_DAYS(3),
    /**
     * 最近30天
     */
    LATEST_30_DAYS(4),
    /**
    * 当前周
    */
    CURR_WEEK(11),
    /**
    * 上一周
    */
    PRE_WEEK(12),
    /**
    * 当天月
    */
    CURR_MONTH(21),
    /**
    * 上一月
    */
    PRE_MONTH(22),
    /**
    * 当前季度
    */
    CURR_QUARTER(31),
    /**
    * 上一季度
    */
    PRE_QUARTER(32),
    /**
    * 本年
    */
    CURR_YEAR(100);
    
    private int type;

    PeriodEnum(int type){
        this.type = type;
    }
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    /**
    *  获取时间周期类型
    *  @Method_Name             ：getPeriodEnum
    *  @param period
    *  @return com.yirun.framework.core.enums.PeriodEnum
    *  @Creation Date           ：2018/9/17
    *  @Author                  ：zc.ding@foxmail.com
    */
    public static PeriodEnum getPeriodEnum(Integer period){
        for(PeriodEnum  pe : PeriodEnum.values()){
            if(period.equals(pe.getType())){
                return pe;
            }
        }
        return PeriodEnum.LATEST_1_DAYS;
    }
}
