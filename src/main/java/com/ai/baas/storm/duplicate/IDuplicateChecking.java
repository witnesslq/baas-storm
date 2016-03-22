package com.ai.baas.storm.duplicate;

import java.util.Map;

public interface IDuplicateChecking {

	/**
	 * 查重数据
	 * @param aData
	 * @return 
	 *   false:查重失败有重复数据
	 */
	boolean checkData(Map<String,String> data) throws Exception;
	
}
