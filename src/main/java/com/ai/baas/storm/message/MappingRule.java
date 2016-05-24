package com.ai.baas.storm.message;

import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.baas.storm.message.RecordFmt.RecordFmtKey;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报文消息与配置表映射
 *
 * @author majun
 */
public class MappingRule {
    private static Logger logger = LoggerFactory.getLogger(MappingRule.class);
    public static final int FORMAT_TYPE_INPUT = 1;
    public static final int FORMAT_TYPE_OUTPUT = 2;
    private Map<RecordFmtKey, Map<String, Integer>> recordFmtMap = new HashMap<RecordFmtKey, Map<String, Integer>>();

    /**
     * 获取当前类型的映射规则
     *
     * @param formatType
     * @return
     */
    public static MappingRule getMappingRule(int formatType, String dbName) {
        logger.debug("load mapping " + formatType);
        MappingRule mappingRule = new MappingRule();
        StringBuilder recordFmtSql = new StringBuilder();
        recordFmtSql.append("select r.tenant_id tenantId,r.service_type serviceType,r.source source,");
        recordFmtSql.append("       r.format_type formatType,r.field_serial fieldSerial,r.field_name fieldName,");
        recordFmtSql.append("       r.field_code fieldCode ");
        recordFmtSql.append("from bmc_record_fmt r ");
        recordFmtSql.append("where r.format_type<=").append(formatType);
        recordFmtSql.append(" order by r.field_serial ");
        List<RecordFmt> recordFmtList = JdbcTemplate.query(recordFmtSql.toString(), dbName, new BeanListHandler<RecordFmt>(RecordFmt.class));
        mappingRule.init(recordFmtList);
        return mappingRule;
    }

    /**
     * 初始化
     *
     * @param recordFmtList
     */
    private void init(List<RecordFmt> recordFmtList) {
        if (recordFmtList == null) {
            return;
        }

        for (RecordFmt recordFmt : recordFmtList) {
            Map<String, Integer> indexMap = recordFmtMap.get(recordFmt.getRecordFmtKey());
            if (indexMap == null) {
                indexMap = new HashMap<String, Integer>();
                recordFmtMap.put(recordFmt.getRecordFmtKey(), indexMap);
            }
            indexMap.put(recordFmt.getFieldCode(), recordFmt.getFieldSerial());
        }
        logger.debug("there is " + recordFmtMap.size() + " mapping loaded!");
    }

    public Map<String, Integer> getIndexes(RecordFmtKey recordFmtKey) {
        return recordFmtMap.get(recordFmtKey);
    }

}
