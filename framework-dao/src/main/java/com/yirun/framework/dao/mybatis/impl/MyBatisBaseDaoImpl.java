package com.yirun.framework.dao.mybatis.impl;

import com.yirun.framework.core.exception.BusinessException;
import com.yirun.framework.core.support.jta.JTAContext;
import com.yirun.framework.core.support.jta.JTAInterceptor;
import com.yirun.framework.core.utils.pager.Pager;
import com.yirun.framework.dao.mybatis.MyBatisBaseDao;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

/**
 * @Description   : mybatis DAO 通用工具类
 * @Project       : framework-dao
 * @Program Name  : com.yirun.framework.dao.mybatis.impl.MyBatisBaseDaoImpl.java
 * @Author        : imzhousong@gmail.com 周松
 */
@Component(value="myBatisBaseDao")
public class MyBatisBaseDaoImpl<T, PK extends Serializable> /**extends SqlSessionDaoSupport**/  implements  MyBatisBaseDao<T, PK> {

	private static final Logger logger = Logger.getLogger(MyBatisBaseDaoImpl.class);

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	/**
	 * 插入
	 */
	public String INSERT = ".insert";

	/**
	 * 批量插入
	 */
	public String INSERT_BATCH = ".insertBatch";

	/**
	 * 更新
	 */
	public String UPDATE = ".update";

	/**
	 * 根据ID 删除
	 */
	public String DELETE = ".delete";

	/**
	 * 根据ID 查询
	 */
	public String GETBYID = ".getById";

	/**
	 * 根据条件 分页查询
	 */
	//TODO:zhongping,这里名字是否要修改,改成统一格式sqlName+Count
	public String COUNT = ".findPage_count";

	/**
	 * 根据条件 分页查询
	 */
	public String PAGESELECT = ".findPage";

	public int save(T object) {
		if (object == null) {
			throw new BusinessException(" object can't null!");
		}

		if (JTAContext.TRANSACTION_LOCAL.get() != null) {
			SqlSession sqlSession = sqlSessionFactory.openSession(false);
			setAutoCommit(sqlSession.getConnection(), false);
			JTAContext.CONNECTION_HOLDER.get().add(sqlSession);
			return sqlSession.insert(object.getClass().getName() + INSERT, object);
		} else {
			return sqlSessionTemplate.insert(object.getClass().getName() + INSERT, object);
		}
	}

	public int insertBatch(Class<T> cls, List<T> domainList) {
		return sqlSessionTemplate.insert(cls.getName() + INSERT_BATCH, domainList);
	}

	public int insertBatch(Class<T> cls, List<T> domainList, Integer count) {
		SqlSession sqlSession = null;
		try {
			if (domainList == null) {
				return 0;
			}

			sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
			if (JTAContext.TRANSACTION_LOCAL.get() != null) {
				setAutoCommit(sqlSession.getConnection(), false);
				JTAContext.CONNECTION_HOLDER.get().add(sqlSession);
			}

			int num = 0;
			for (T t : domainList) {
				sqlSession.insert(cls.getName() + INSERT, t);
				num++;
				if (count == num) {
					sqlSession.commit();
					num = 0;
				}
			}

			if (JTAContext.TRANSACTION_LOCAL.get() == null) {
				sqlSession.commit();
			}
		} catch (Exception e) {
			if (JTAContext.TRANSACTION_LOCAL.get() == null) {
				if (sqlSession != null) {
					sqlSession.rollback(true);
				}
			}
			logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			if (JTAContext.TRANSACTION_LOCAL.get() == null) {
				if (sqlSession != null) {
					sqlSession.close();
				}
			}

		}
		return count;
	}


	@SuppressWarnings("unchecked")
	public List<T> findByCondition(T obj) {
		if (obj == null) {
			throw new BusinessException(" condition can't null!");
		}
		return (List<T>) sqlSessionTemplate.selectList(obj.getClass().getName() + PAGESELECT, obj);
	}

	@SuppressWarnings("unchecked")
	public List<T> findByCondition(T obj, int offset, int limit) {
		return findByCondition(obj,offset,limit,null/*默认本类*/,null/*默认为null*/);
	}

	@Override
	public List<T> findByCondition(Object obj, int offset, int limit, Class clazz/*增加calzzname,如果缺省则为本类*/, String sqlName/*加了一个默认sqlName*/) {
		if (obj == null) {
			throw new BusinessException(" condition can't null!");
		}
		if (sqlName==null) {
			//如果没有提供sql，使用缺省的sql
			sqlName=PAGESELECT;
		}

		//如果没有提供clazz，默认值是
        if (clazz==null) {
			clazz = obj.getClass();
		}

		RowBounds rb = new RowBounds(offset, limit);
		return (List<T>) sqlSessionTemplate.selectList(clazz.getName() + sqlName, obj, rb);
	}


    public Pager findByCondition(T obj, Pager pager) {
        return this.findByCondition(obj, pager, null);
    }

	@Override
	public Pager findByCondition(T obj, Pager pager, String sqlName/*添加一个自定义的sql的名字*/) {
	    //直接调用通用方法
        return this.findByCondition(obj, pager, null, sqlName);
    }


    @Override
    public Pager findByCondition(Object obj, Pager pager, Class clazz, String sqlName) {

        if (obj == null) {
            throw new BusinessException(" condition can't null!");
        }
        if (pager==null) {
            //说明是第一页,默认每页显示10
            pager = new Pager(this.getTotalCount(obj,clazz,sqlName),10);
        }else{
			if (pager.isInfiniteMode()) {
				//判断是否是无尽模式
				pager.setStartRow(pager.getPageSize()*(pager.getCurrentPage()-1));
			}else{
				//每次都要重置总记录数，因为有可能更新了。
				pager.setTotalRows(this.getTotalCount(obj,clazz,sqlName));
			}

        }
        //查询数据
        pager.setData(this.findByCondition(obj, pager.getStartRow(), pager.getPageSize(), clazz, sqlName));
        return pager;
    }

    @SuppressWarnings("unchecked")
	public T findByPK(PK pk, Class<T> cls) {
		if (pk == null) {
			throw new BusinessException(" pk can't null!");
		}
		return (T) sqlSessionTemplate.selectOne(cls.getName() + GETBYID, pk);

	}

	public int update(T object) {
		if (object == null) {
			throw new BusinessException(" object can't null!");
		}
		if (JTAContext.TRANSACTION_LOCAL.get() != null) {
			SqlSession session = sqlSessionFactory.openSession(false);
			setAutoCommit(session.getConnection(), false);
			JTAContext.CONNECTION_HOLDER.get().add(session);
			return session.update(object.getClass().getName() + UPDATE, object);
		} else {
			return sqlSessionTemplate.update(object.getClass().getName() + UPDATE, object);
		}
	}

	public int delete(PK pk, Class<T> cls) {
		if (pk == null) {
			throw new BusinessException(" pk can't null!");
		}
		if (JTAContext.TRANSACTION_LOCAL.get() != null) {
			SqlSession session = sqlSessionFactory.openSession(false);
			setAutoCommit(session.getConnection(), false);
			JTAContext.CONNECTION_HOLDER.get().add(session);
			return session.delete(cls.getName() + DELETE, pk);
		} else {
			return sqlSessionTemplate.delete(cls.getName() + DELETE, pk);
		}
	}

    @Override
	public Integer getTotalCount(T object) {
		return getTotalCount(object,null);
	}

	@Override
	public Integer getTotalCount(T object, String sqlName) {
        return this.getTotalCount(object, null, sqlName);
    }

    @Override
    public Integer getTotalCount(Object object, Class clazz, String sqlName) {
        if (object == null) {
            throw new BusinessException(" condition can't null!");
        }
        if (sqlName==null) {
            sqlName = COUNT;
        }else {
            sqlName=sqlName+"Count";
        }
        if (clazz==null) {
			clazz = object.getClass();
		}

        Object obj = sqlSessionTemplate.selectOne(clazz.getName() + sqlName, object);
        if (obj != null) {
            return Integer.parseInt(obj.toString());
        }
        return 0;
    }

    public int updateBatch(Class<T> cls, List<T> domainList, Integer count) {
		SqlSession sqlSession = null;
		try {
			if (domainList == null) {
				return 0;
			}
			sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
			if (JTAContext.TRANSACTION_LOCAL.get() != null) {
				setAutoCommit(sqlSession.getConnection(), false);
				JTAContext.CONNECTION_HOLDER.get().add(sqlSession);
			}

			int num = 0;
			for (T t : domainList) {
				sqlSession.update(cls.getName() + UPDATE, t);
				num++;
				if (count == num) {
					sqlSession.commit();
					num = 0;
				}
			}
			if (JTAContext.TRANSACTION_LOCAL.get() == null) {
				sqlSession.commit();
			}
		} catch (Exception e) {
			if (JTAContext.TRANSACTION_LOCAL.get() == null) {
				if (sqlSession != null) {
					sqlSession.rollback(true);
				}
			}
			logger.error(e.getMessage(), e);
		} finally {
			if (JTAContext.TRANSACTION_LOCAL.get() == null) {
				if (sqlSession != null) {
					sqlSession.close();
				}
			}
		}
		return count;
	}

	public SqlSessionTemplate getCurSqlSessionTemplate() {
		return sqlSessionTemplate;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	public SqlSessionFactory getCurSqlSessionFactory() {
		return this.sqlSessionFactory;
	}

	private void setAutoCommit(Connection con, boolean autoCommit) {
		try {
			if (con != null) {
				con.setAutoCommit(autoCommit);
			}
		} catch (Exception e) {

		}
	}

	public int updateExp(T object) {
		if (object == null) {
			throw new BusinessException(" object can't null!");
		}
		if (JTAContext.TRANSACTION_LOCAL.get() != null) {
			SqlSession session = sqlSessionFactory.openSession(false);
			setAutoCommit(session.getConnection(), false);
			JTAContext.CONNECTION_HOLDER.get().add(session);
			return session.update(object.getClass().getName() + UPDATE, object);
		} else {
			return sqlSessionTemplate.update(object.getClass().getName() + UPDATE, object);
		}
	}

	public int deleteExp(PK pk, Class<T> cls) {
		if (pk == null) {
			throw new BusinessException(" pk can't null!");
		}
		if (JTAContext.TRANSACTION_LOCAL.get() != null) {
			SqlSession session = sqlSessionFactory.openSession(false);
			setAutoCommit(session.getConnection(), false);
			JTAContext.CONNECTION_HOLDER.get().add(session);
			return session.delete(cls.getName() + DELETE, pk);
		} else {
			return sqlSessionTemplate.delete(cls.getName() + DELETE, pk);
		}
	}
}
