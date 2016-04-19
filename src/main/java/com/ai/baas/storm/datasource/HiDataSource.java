package com.ai.baas.storm.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * HikariCP使用
 * 
 * @author mayt
 * 
 */
public class HiDataSource {

    private static HikariDataSource ds;

    private static Map<String, String> configMap;

    public static HikariDataSource getInstance(Map<String, String> config) {
        if (ds == null) {
            configMap = config;
            init();
        }
        return ds;
    }

    /**
     * 初始化连接池
     * 
     */
    static void init() {
        // 连接池配置
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(configMap.get("jdbc.driver"));
        config.setJdbcUrl(configMap.get("jdbc.url"));
        config.setUsername(configMap.get("jdbc.username"));
        config.setPassword(configMap.get("jdbc.password"));
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 500);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.setConnectionTestQuery("SELECT 1");
        config.setAutoCommit(true);
        // 池中最小空闲链接数量
        config.setMinimumIdle(1);
        // 池中最大链接数量
        config.setMaximumPoolSize(1);

        ds = new HikariDataSource(config);

    }

    /**
     * 销毁连接池
     */
    public void shutdown() {
        ds.close();
    }

    /**
     * 从连接池中获取链接
     * 
     * @return
     */
    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            ds.resumePool();
            return null;
        }
    }

}
