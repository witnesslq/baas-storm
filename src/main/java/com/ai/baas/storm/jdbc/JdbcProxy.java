package com.ai.baas.storm.jdbc;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.storm.util.BaseConstants;


/**
 * Jdbc代理类,在一个JVM中,
 * 一个DB只维持一个连接(自动重连)
 *
 * @author majun
 * @since 2016.3.17
 */
public class JdbcProxy {
    private static Logger logger = LoggerFactory.getLogger(JdbcProxy.class);
    private static Map<String, Connector> connectors = new HashMap<String, Connector>();
    private static Lock lock = new ReentrantLock();

    public static void loadResources(List<String> dbNames, Map<String, String> config) {
        for (String dbName : dbNames) {
            String jsonJdbcParam = config.get(dbName);
            if (StringUtils.isBlank(jsonJdbcParam)) {
                logger.debug("{0}没有配置Jdbc参数", dbName);
                continue;
            }
            setConnector(dbName,jsonJdbcParam);
        }
    }

    public static void loadDefaultResource(Map<String, String> config) {
        String jsonJdbcParam = config.get(BaseConstants.JDBC_DEFAULT);
        if (StringUtils.isBlank(jsonJdbcParam)) {
            logger.error("{0}没有配置Jdbc参数", BaseConstants.JDBC_DEFAULT);
            throw new RuntimeException(BaseConstants.JDBC_DEFAULT + "{0}没有配置Jdbc参数");
        }
        setConnector(BaseConstants.JDBC_DEFAULT,jsonJdbcParam);       
    }
    
    private static void setConnector(String dbName,String jdbcParam){
    	lock.lock();
    	try {
    		Connector connector = connectors.get(dbName);
    		if(connector == null){
    			connector = new Connector(jdbcParam);
    			connectors.put(dbName, connector);
    		}
        } catch (Exception e) {
            logger.debug("error", e);
        }
    	lock.unlock();
    }

    public static Connection getConnection(String dbName) throws Exception {
        Connection conn = null;
        Connector connector = connectors.get(dbName);
        if (connector != null) {
            conn = connector.getConnection();
        }
        return conn;
    }


}
