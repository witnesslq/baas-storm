package com.ai.baas.storm.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.storm.message.RecordFmt.RecordFmtKey;
import com.ai.baas.storm.util.BaseConstants;

/**
 * 单条数据解析类
 * 将字符串数据解析成Map格式
 * 
 * @author majun
 * @since 2016.3.21
 *
 */
public class MessageParser {
	private static Logger logger = LoggerFactory.getLogger(MessageParser.class);
	private Map<String, String> data = new HashMap<String, String>();
	private MappingRule[] mappingRules;//0:inputMappingRule  1:outMappingRule
	private String[] outputKeys;
	private String[] inputDatas;
	private RecordFmtKey recordFmtKey;
	private static String[] headerKeys;
	
	static{
		headerKeys = new String[] { BaseConstants.TENANT_ID,
				BaseConstants.SERVICE_TYPE, BaseConstants.SOURCE,
				BaseConstants.BATCH_SERIAL_NUMBER, BaseConstants.SERIAL_NUMBER,
				BaseConstants.ARRIVAL_TIME, BaseConstants.ACCOUNT_PERIOD };
	}
	
	private MessageParser(String original, MappingRule[] mappingRules, String[] outputKeys) throws Exception{
		String[] inputParams = StringUtils.splitPreserveAllTokens(original,BaseConstants.FIELD_SPLIT);
		if (inputParams.length < 7) {
			throw new Exception("不正确的数据格式!"); 
		}
		for(int i=0;i<headerKeys.length;i++){
			data.put(headerKeys[i], inputParams[i]);
		}
		int dataBeginPosi = headerKeys.length;
		int len = inputParams.length - dataBeginPosi;
		inputDatas = new String[len];
		System.arraycopy(inputParams, dataBeginPosi, inputDatas, 0, len);
		this.mappingRules = mappingRules;
		this.outputKeys = outputKeys;
	}
	
	private void init() throws Exception{
		recordFmtKey = new RecordFmtKey(data.get(BaseConstants.TENANT_ID),data.get(BaseConstants.SERVICE_TYPE),data.get(BaseConstants.SOURCE));
		Map<String, Integer> inputMappingRule = mappingRules[0].getIndexes(recordFmtKey);
		if (inputMappingRule == null) {
			throw new Exception("bmc_record_fmt表中没有配置报文格式!");
		}
		for (Entry<String, Integer> entry : inputMappingRule.entrySet()) {
			data.put(entry.getKey(), inputDatas[entry.getValue()]);
		}
	}
	
	public Map<String, String> getData() {
		return data;
	}
	
	public List<Object> toTupleData(){
		//String[] outputKeys = boltInOutKeys.get(1);
		List<Object> rtnValue = new ArrayList<Object>();
		for (String outputKey:outputKeys){
			if(!outputKey.equals(BaseConstants.RECORD_DATA)){
				rtnValue.add(data.get(outputKey));
			}
		}
		rtnValue.add(toDataString(mappingRules[1].getIndexes(recordFmtKey)));
		return rtnValue;
	}
	
	private String toDataString(Map<String, Integer> outputMappingRule){
		String[] tmpArr = new String[outputMappingRule.size()];
		for(Entry<String,Integer> entry:outputMappingRule.entrySet()){
			tmpArr[entry.getValue()]=StringUtils.defaultString(data.get(entry.getKey()));
		}
		StringBuilder record = new StringBuilder();
		for(String headerKey:headerKeys){
			record.append(data.get(headerKey)).append(BaseConstants.FIELD_SPLIT);
		}
		for(String e:tmpArr){
			record.append(e).append(BaseConstants.FIELD_SPLIT);
		}
		record.delete(record.length()-1, record.length());
		return record.toString();
	}

	
	public static MessageParser parseObject(String original, MappingRule[] mappingRules, String[] outputKeys) throws Exception{
		if(StringUtils.isBlank(original)){
			throw new Exception("input String is null!");
		}
		MessageParser messageParser = new MessageParser(original,mappingRules,outputKeys);
		messageParser.init();
		return messageParser;
	}


}
