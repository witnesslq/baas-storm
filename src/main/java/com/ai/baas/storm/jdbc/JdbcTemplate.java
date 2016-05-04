package com.ai.baas.storm.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.storm.util.BaseConstants;

/**
 * Jdbc模板类,封装jdbc增删改查操作
 * @author majun
 *
 */
public class JdbcTemplate {
	private static Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);
	
	/**
	 * 查询(带参数)
	 * @param sql
	 * @param conn
	 * @param rsh
	 * @param params
	 * @return
	 */
	public static <T> List<T> query(String sql, Connection conn, ResultSetHandler<List<T>> rsh, Object... params) {
		List<T> list = new ArrayList<>();
		try {
			QueryRunner runner = new QueryRunner();
			list = runner.query(conn, sql, rsh, params);
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error("error",e1);
			}
			logger.error("error",e);
		}
		return list;
	}

	/**
	 * 查询
	 * @param sql
	 * @param conn
	 * @param rsh
	 * @return
	 */
	public static <T> List<T> query(String sql, Connection conn, ResultSetHandler<List<T>> rsh) {
		List<T> list = new ArrayList<>();
		try {
			QueryRunner runner = new QueryRunner();
			list = runner.query(conn, sql, rsh);
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error("error",e1);
			}
			logger.error("error",e);
		}
		return list;
	}

	/**
	 * 查询(根据DB名称)
	 * @param sql
	 * @param dbName
	 * @param rsh
	 * @return
	 */
	public static <T> List<T> query(String sql, String dbName, ResultSetHandler<List<T>> rsh) {
		List<T> result = null;
		Connection conn = null;
		try {
			conn = JdbcProxy.getConnection(dbName);
			if (conn != null){
				result = query(sql, conn, rsh);
			}
		} catch (Exception e) {
			logger.error("error",e);
		}
		return result;
	}
	
	/**
	 * 使用默认的jdbc查询
	 * @param sql
	 * @param rsh
	 * @return
	 */
	public static <T> List<T> defaultQuery(String sql, ResultSetHandler<List<T>> rsh) {
		return query(sql,BaseConstants.JDBC_DEFAULT,rsh);
	}
	
	
	/**
	 * 更新（包括UPDATE、INSERT、DELETE，返回受影响的行数）
	 * 
	 * @param DataSourceName
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int update(String sql, Connection conn, Object... params) {
		int result = 0;
		QueryRunner runner = new QueryRunner();
		try {
			result = runner.update(conn, sql, params);
			conn.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
		}
		return result;
	}

	public static int update(String sql, Connection conn) {
		int result = 0;
		QueryRunner runner = new QueryRunner();
		try {
			result = runner.update(conn, sql);
			conn.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
		}
		return result;
	}
	
	/**
	 * 手动update,需要自行commit
	 * @param sql
	 * @param conn
	 * @param params
	 * @return
	 */
	public static int manualUpdate(String sql, Connection conn, Object... params){
		int result = 0;
		QueryRunner runner = new QueryRunner();
		try {
			result = runner.update(conn, sql, params);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 批量操作
	 * @param sql
	 * @param conn
	 * @param inputParams
	 * @return
	 */
	public static int[] batchUpdate(String sql, Connection conn, List<Object[]> inputParams) {
		int[] results = null;
		QueryRunner runner = new QueryRunner();
		int len = inputParams.size();
		Object[][] params = new Object[len][];
		try {
			for (int i = 0; i < len; i++) {
				params[i] = inputParams.get(i);
			}
			results = runner.batch(conn, sql, params);
			conn.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
		}
		return results;
	}
	
	
	
}
