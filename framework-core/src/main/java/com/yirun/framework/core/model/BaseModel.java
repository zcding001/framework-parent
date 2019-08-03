
package com.yirun.framework.core.model;

import java.io.Serializable;

/**
 * @Description : Model 基础类
 * @Project : framework-core
 * @Program Name : com.yirun.framework.core.model.BaseModel.java
 * @Author : imzhousong@gmail.com 周松
 */
public class BaseModel implements Serializable {

  private static final long serialVersionUID = 7350537051632310117L;

  /**
   * 排序字段
   */
  private String sortColumns;
  /**
   * 平台名称
   */
  public String systemName;

  /**
   * 指定的查询列的id
   */
  public String queryColumnId;

  public String getSortColumns() {
    return sortColumns;

  }

  public void setSortColumns(String sortColumns) {
    this.sortColumns = sortColumns;
  }

  public String getSystemName() {
    return systemName;
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }

  public String getQueryColumnId() {
    return queryColumnId;
  }

  public void setQueryColumnId(String queryColumnId) {
    this.queryColumnId = queryColumnId;
  }
}
