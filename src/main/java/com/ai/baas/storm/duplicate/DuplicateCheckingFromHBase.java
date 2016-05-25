package com.ai.baas.storm.duplicate;

import com.ai.baas.storm.util.BaseConstants;
import com.ai.baas.storm.util.HBaseProxy;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 查重处理类(通过hbase方式)
 *
 * @author majun
 */
public class DuplicateCheckingFromHBase implements IDuplicateChecking {
    private static Logger logger = LoggerFactory.getLogger(DuplicateCheckingFromHBase.class);
    //private Connection conn = HBaseProxy.getConnection();

    @Override
    public boolean checkData(Map<String, String> data) throws Exception {
        StringBuilder dupKey = new StringBuilder();
        dupKey.append(data.get(BaseConstants.TENANT_ID)).append(BaseConstants.COMMON_JOINER);
        dupKey.append(data.get(BaseConstants.SERVICE_TYPE)).append(BaseConstants.COMMON_JOINER);
        dupKey.append(DuplicateCheckingConfig.DUP_PREFIX);
        String dupTableName = assembleDupTable(dupKey.toString(), data);
        String dupKeyStr = assembleDuplicateKey(dupKey.toString(), data);
        return checkDuplicate(dupTableName, dupKeyStr);
    }

    /**
     * 组装查重的表名
     *
     * @param dupKey
     * @param data
     * @return
     * @throws Exception
     */
    private String assembleDupTable(String dupKey, Map<String, String> data) throws Exception {
        String suffixKey = DuplicateCheckingConfig.getInstance().getTbSuffixKey(dupKey);
        String suffixKeyValue = data.get(suffixKey);
        if (StringUtils.isBlank(suffixKeyValue)) {
            throw new Exception("查重表中后缀Key对应的Value在实际数据中不存在!");
        }
        suffixKeyValue = filterUnNumber(suffixKeyValue);
        StringBuilder dupTable = new StringBuilder();
        dupTable.append(dupKey).append("_");
        dupTable.append(suffixKeyValue.substring(0, 6));
        return dupTable.toString();
    }

    /**
     * 组装查重的关键字
     *
     * @param cacheDupKey
     * @param businessData
     * @return
     */
    private String assembleDuplicateKey(String cacheDupKey, Map<String, String> businessData) {
        List<String> keys = DuplicateCheckingConfig.getInstance().getDupKeyNames(cacheDupKey);
        StringBuilder dupKey = new StringBuilder();
        for (String key : keys) {
            dupKey.append(businessData.get(key));
        }
        return dupKey.toString();
    }

    /**
     * 通过hbase进行查重
     *
     * @param dupTableName
     * @param dupKey
     * @return
     * @throws Exception
     */
    private boolean checkDuplicate(String dupTableName, String dupKey) throws Exception {
        createHBaseTableIfNecessary(dupTableName);
        boolean isSucc = true;
        byte[] rowKey = dupKey.getBytes();
        Table table = null;
        try {
            table = HBaseProxy.getConnection().getTable(TableName.valueOf(dupTableName));
            Get get = new Get(rowKey);
            Result result = table.get(get);
            if (result.isEmpty()) {
                Put put = new Put(rowKey);
                String create_date = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
                put.addColumn("dup".getBytes(), "create_date".getBytes(), create_date.getBytes());
                table.put(put);
            } else {
                isSucc = false;
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException e) {
                    throw new Exception(e.getMessage());
                }
            }
        }
        return isSucc;
    }


    private void createHBaseTableIfNecessary(String tableName) throws IOException {
        Admin admin = HBaseProxy.getConnection().getAdmin();
        if (!admin.isTableAvailable(TableName
                .valueOf(tableName))) {
            HTableDescriptor tableDesc = new HTableDescriptor(
                    TableName.valueOf(tableName));
            tableDesc.addFamily(new HColumnDescriptor(
                    "dup"));
            admin.createTable(tableDesc);
            logger.info("Create table [{}] ok!",
                    tableName);
        }
    }


    private String filterUnNumber(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

}
