package com.ai.baas.storm.util;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HBaseProxy {
	private static Logger logger = LoggerFactory.getLogger(HBaseProxy.class);
	private static Connection connection;
	private static HBaseProxy instance;
	
	public static void loadResource(Map<String,String> config){
		if(instance == null){
			synchronized(HBaseProxy.class){
				if(instance == null){
					instance = new HBaseProxy();
					instance.loading(config);
					System.out.println("hbase客户端连接成功...");
				}
			}
		}
	}
	
	public void loading(Map<String,String> config){
		Configuration configuration = HBaseConfiguration.create();
		String hbaseSite = config.get(BaseConstants.HBASE_PARAM);
		try {
			if(StringUtils.isBlank(hbaseSite)){
				throw new Exception("输入参数中没有配置hbase.site属性信息!");
			}
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = (JsonObject)jsonParser.parse(hbaseSite);
			for(Entry<String, JsonElement> entry:jsonObject.entrySet()){
				configuration.set(entry.getKey(), entry.getValue().getAsString());
			}
			connection = ConnectionFactory.createConnection(configuration);
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	public static Connection getConnection(){
		return connection;
	}
	
	
	
}
