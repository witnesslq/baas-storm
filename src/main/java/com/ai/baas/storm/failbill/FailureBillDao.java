package com.ai.baas.storm.failbill;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.storm.util.BaseConstants;
import com.ai.baas.storm.util.HBaseProxy;

/**
 * 错单Dao类,将错单数据持久化
 * @author majun
 *
 */
public class FailureBillDao {
	private static Logger logger = LoggerFactory.getLogger(FailureBillDao.class);
	//private Connection conn = HBaseProxy.getConnection();
	private byte[] column_family = Bytes.toBytes(BaseConstants.FAILURE_BILL_FAMILY_NAME);
	private TableName tableName = TableName.valueOf(BaseConstants.FAILURE_BILL_TABLE_NAME);
	
	/**
	 * 将错单数据保存到HBase中
	 * @param failureBill
	 */
	public void insertFailBillData(FailureBill failureBill){
		String rowKey = assembleRowKey(failureBill);
		Table table=null;
		try {
			table = HBaseProxy.getConnection().getTable(tableName);
			Put put = new Put(rowKey.getBytes());
			putData(put, failureBill);
			table.put(put);			
		} catch (IOException e) {
			logger.error("error", e);
		} finally{
			if(table != null){
				try {
					table.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
		}
	}
	
	/**
	 * 拼装rowKey
	 * @param failureBill
	 * @return
	 */
	private String assembleRowKey(FailureBill failureBill){
		StringBuilder rowKey = new StringBuilder();
		rowKey.append(failureBill.getTenantId()).append(BaseConstants.FIELD_SPLIT);
		rowKey.append(failureBill.getServiceType()).append(BaseConstants.FIELD_SPLIT);
		rowKey.append(failureBill.getSource()).append(BaseConstants.FIELD_SPLIT);
		rowKey.append(failureBill.getBsn()).append(BaseConstants.FIELD_SPLIT);
		rowKey.append(failureBill.getSn()).append(BaseConstants.FIELD_SPLIT);
		rowKey.append(failureBill.getFailStep()).append(BaseConstants.FIELD_SPLIT);
		rowKey.append(failureBill.getFailCode()).append(BaseConstants.FIELD_SPLIT);
		rowKey.append(failureBill.getFailDate());
		return rowKey.toString();
	}
	
	/**
	 * 拼装hbase put数据
	 * @param put
	 * @param failureBill
	 */
	private void putData(Put put,FailureBill failureBill){
		put.addColumn(column_family, Bytes.toBytes("tenant_id"), toBytes(failureBill.getTenantId()));
		put.addColumn(column_family, Bytes.toBytes("service_type"), toBytes(failureBill.getServiceType()));
		put.addColumn(column_family, Bytes.toBytes("source"), toBytes(failureBill.getSource()));
		put.addColumn(column_family, Bytes.toBytes("bsn"), toBytes(failureBill.getBsn()));
		put.addColumn(column_family, Bytes.toBytes("sn"), toBytes(failureBill.getSn()));
		put.addColumn(column_family, Bytes.toBytes("account_period"), toBytes(failureBill.getAccountPeriod()));
		put.addColumn(column_family, Bytes.toBytes("arrival_time"), toBytes(failureBill.getArrivalTime()));
		put.addColumn(column_family, Bytes.toBytes("fail_step"), toBytes(failureBill.getFailStep()));
		put.addColumn(column_family, Bytes.toBytes("fail_code"), toBytes(failureBill.getFailCode()));
		put.addColumn(column_family, Bytes.toBytes("fail_reason"), toBytes(failureBill.getFailReason()));
		put.addColumn(column_family, Bytes.toBytes("fail_packet"), toBytes(failureBill.getFailPakcet()));
		put.addColumn(column_family, Bytes.toBytes("fail_date"), toBytes(failureBill.getFailDate()));
	}
	
	private byte[] toBytes(String data){
		String strTemp = StringUtils.isNotBlank(data)?data:"";
		return strTemp.getBytes();
	}
	
}
