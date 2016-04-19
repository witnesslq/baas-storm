package com.ai.baas.storm.sequence.datasource;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.storm.datasource.HiDataSource;
import com.zaxxer.hikari.HikariDataSource;

public class SeqDataSourceLoader {
    private static final Logger LOG = LoggerFactory.getLogger(SeqDataSourceLoader.class);

    private static HikariDataSource ds;

    public static void initDefault(Map<String, String> config) {
        if (ds == null) {
            ds = HiDataSource.getInstance(config);
        }
//        SeqDataSourceLoaderFactory.init(ds);
    }

    public static HikariDataSource getDs() {
        return ds;
    }

    public void setDs(HikariDataSource ds) {
        this.ds = ds;
    }

}
