package com.yirun.framework.dao.mybatis;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

import com.yirun.framework.core.utils.pager.Pager;

/**
 * @Description : MyBatis DAO通用操作类
 * @Project : framework-dao
 * @Program Name  : com.yirun.framework.dao.mybatis.MyBatisBaseDao.java
 * @Author : imzhousong@gmail.com 周松
 */
public interface MyBatisBaseDao<T, PK extends Serializable> {

    /**
     * 增加实体
     *
     * @param object
     */
    public int save(T object);

    /**
     * 按条件查询实体
     *
     * @param obj
     * @return
     */
    public List<T> findByCondition(T obj);


    /**
     * @param obj   查询对象
     * @param pager 分页对象
     * @return 查询结果
     * @throws
     * @description 分页查询
     */
    public Pager findByCondition(T obj, Pager pager);

    /**
     * 自定义sql查询count
     *
     * @param obj
     * @param pager
     * @param sqlName
     * @return
     */
    public Pager findByCondition(T obj, Pager pager, String sqlName/*添加一个自定义的sql的名字*/);

    /**
     * 为了扩展VO分页所添加的方法(泛型方法)
     * @param obj
     * @param clazz
     * @param sqlName
     * @return
     */
    Pager findByCondition(Object obj/*代表vo类*/, Pager pager, Class clazz/*增加calzzname,如果缺省则为本类*/,String sqlName/*加了一个默认sqlName*/);

    /**
     * 按条件查询实体并分页
     *
     * @param obj
     * @param start
     * @param limit
     * @return
     */
    public List<T> findByCondition(T obj, int start, int limit);

    /**
     * 自定义的sql查询分页数据
     *
     * @param obj
     * @param offset
     * @param limit
     * @param sqlName
     * @return
     */
    List<T> findByCondition(Object obj, int offset, int limit, Class clazz/*增加calzzname,如果缺省则为本类*/, String sqlName/*加了一个默认sqlName*/);

    /**
     * 按主键查询
     *
     * @param pk
     * @return
     */
    public T findByPK(PK pk, Class<T> cls);

    /**
     * 更新实体
     *
     * @param object
     */
    public int update(T object);

    public int updateExp(T object);

    /**
     * 按主键删除实体
     *
     * @param pk
     */
    public int delete(PK pk, Class<T> cls);

    public int deleteExp(PK pk, Class<T> cls);

    /**
     * 按条件查询总记录数
     *
     * @param object
     * @return
     */
    public Integer getTotalCount(T object);

    /**
     * 查询统计
     *
     * @param object
     * @param sqlName
     * @return
     */
    public Integer getTotalCount(T object, String sqlName);


    /**
     * 查询统计，未扩展vo类的查询所扩展的方法
     *
     * @param object
     * @param sqlName
     * @return
     */
    public Integer getTotalCount(Object object, Class clazz/*代表VO类*/,String sqlName);



    /**
     * 批量插入
     *
     * @param statementname 更新SQL的ID（sqlMap中）
     * @param domainList    需要更新的集合
     * @param count         表示多少笔数据提交一次
     */
    public int insertBatch(Class<T> cls, List<T> domainList, Integer count);

    /**
     * 批量插入
     *
     * @param cls
     * @param domainList
     * @throws
     * @description 基本原生SQL的数据库插入
     */
    public int insertBatch(Class<T> cls, List<T> domainList);


    /**
     * 批量更新
     *
     * @param statementname 更新SQL的ID（sqlMap中）
     * @param domainList    需要更新的集合
     * @param count         表示多少笔数据提交一次
     */
    public int updateBatch(Class<T> cls, List<T> domainList, Integer count);

    public SqlSessionTemplate getCurSqlSessionTemplate();

    public SqlSessionFactory getCurSqlSessionFactory();

}

