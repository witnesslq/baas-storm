package com.ai.baas.storm.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * jdbc连接器,存储一个jdbc连接和参数
 * @author majun
 *
 */
public class Connector {
	private Connection conn;
	private JdbcParam jdbcParam;
	
	public Connector(String jsonJdbcParam){
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject)jsonParser.parse(jsonJdbcParam);
		Map<String,String> conf = new HashMap<String,String>();
		for(Entry<String, JsonElement> entry:jsonObject.entrySet()){
			conf.put(entry.getKey(), entry.getValue().getAsString());
		}
		jdbcParam = JdbcParam.getInstance(conf);
	}
	
	public Connection createConnection() throws ClassNotFoundException, SQLException {
		Class.forName(jdbcParam.getDriver());
		conn = DriverManager.getConnection(jdbcParam.getUrl(), jdbcParam.getUsername(), jdbcParam.getPassword());
		conn.setAutoCommit(false);
		return conn;
	}
	
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		if (conn == null) {
			return createConnection();
		}
		// 60秒没反应，需要重连
		if (conn.isValid(60)) {
			return conn;
		} else {
			return createConnection();
		}
	}
	
}
