package com.ai.baas.storm.duplicate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;

import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.baas.storm.util.BaseConstants;


/**
 * 查重配置参数类
 * @author majun
 * 
 */
public class DuplicateCheckingConfig {

	public static final String DUP_PREFIX = "dup";
	private static MultiValueMap dupKeyMap = new MultiValueMap();
	private static Map<String, String> suffixKeyMap = new HashMap<String, String>();
	private static DuplicateCheckingConfig instance = null;
	
	public static DuplicateCheckingConfig getInstance() {
		if (instance == null) {
			synchronized (DuplicateCheckingConfig.class) {
				if (instance == null) {
					instance = new DuplicateCheckingConfig();
					loadData();
				}
			}
		}
		return instance;
		
	}
	
	private static void loadData() {
		StringBuilder sql = new StringBuilder();
		//603版本中去掉source
		//sql.append("select t.tenant_id tenantId,t.service_type serviceType,t.source source,");
		sql.append("select t.tenant_id tenantId,t.service_type serviceType,");
		sql.append("t.dup_key dupKey,t.tb_suffix_key tbSuffixKey ");
		sql.append("from bmc_dataquality t");
		List<DuplicateChecking> duplicateCheckings = JdbcTemplate.query(sql.toString(), BaseConstants.JDBC_DEFAULT, new BeanListHandler<DuplicateChecking>(DuplicateChecking.class));
		String[] dupKeys = null;
		for(DuplicateChecking duplicateChecking:duplicateCheckings){
			StringBuilder key = new StringBuilder();
			key.append(duplicateChecking.getTenantId()).append(BaseConstants.COMMON_JOINER);
			key.append(duplicateChecking.getServiceType()).append(BaseConstants.COMMON_JOINER);
			key.append(DUP_PREFIX);
			dupKeys = StringUtils.splitPreserveAllTokens(duplicateChecking.getDupKey(), BaseConstants.COMMON_SPLIT);
			for (String dupKey : dupKeys) {
				dupKeyMap.put(key.toString(), dupKey);
			}
			suffixKeyMap.put(key.toString(), duplicateChecking.getTbSuffixKey());
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getDupKeyNames(String key) {
		return (List<String>) dupKeyMap.get(key);
	}

	public String getTbSuffixKey(String key) {
		return suffixKeyMap.get(key);
	}
	
	
}
