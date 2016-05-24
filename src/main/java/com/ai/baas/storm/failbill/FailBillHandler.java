package com.ai.baas.storm.failbill;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.storm.util.BaseConstants;
import com.ai.baas.storm.util.LoopThread;
import com.google.gson.Gson;

/**
 * 错单处理器
 * 每个JVM启动一个，通过队列接收错单数据
 * 然后写入到hbase中
 * 
 * @author majun
 * 
 */
public class FailBillHandler extends LoopThread {
	private static Logger logger = LoggerFactory.getLogger(FailBillHandler.class);
	private static BlockingQueue<FailureBill> msgQueue = new LinkedBlockingQueue<FailureBill>();
    private FailureBillDao failureBillDao = new FailureBillDao();
    private static FailBillHandler failBillHandler;
	
	/**
	 * 启动错单处理器
	 */
	public static void startup(){
		if(failBillHandler == null){
			synchronized(FailBillHandler.class){
				if(failBillHandler == null){
					failBillHandler = new FailBillHandler();
					failBillHandler.start();
					System.out.println("错单处理器启动中...");
					logger.debug("错单处理器启动中...");
				}
			}
		}
	}
    
    
	@Override
	public boolean init() {
		return true;
	}

	@Override
	public boolean unInit() {
		return true;
	}

	@Override
	public void work() {
		FailureBill element = null;
		try{
			element = msgQueue.take();
		}catch(InterruptedException e){
			logger.error("context", e);
			exitFlag = true;
		}
		//insertFailBillData(element);
		failureBillDao.insertFailBillData(element);
		
	}
	
	/**
	 * 向错单队列中增加一条错单信息
	 * @param failureBill
	 */
	public static void addFailBillMsg(FailureBill failureBill){
		msgQueue.add(failureBill);
	}
	
	/**
	 * 插入错单数据到队列
	 * @param data
	 * @param failStep
	 * @param failCode
	 * @param failReason
	 */
	public static void addFailBillMsg(Map<String, String> data,String failStep,String failCode,String failReason){
		if(data == null){
			return;
		}
		FailureBill failureBill = new FailureBill();
		failureBill.setTenantId(data.get(BaseConstants.TENANT_ID));
		failureBill.setServiceType(data.get(BaseConstants.SERVICE_TYPE));
		failureBill.setSource(data.get(BaseConstants.SOURCE));
		failureBill.setBsn(data.get(BaseConstants.BATCH_SERIAL_NUMBER));
		failureBill.setSn(data.get(BaseConstants.SERIAL_NUMBER));
		failureBill.setAccountPeriod(data.get(BaseConstants.ACCOUNT_PERIOD));
		failureBill.setArrivalTime(data.get(BaseConstants.ARRIVAL_TIME));
		failureBill.setFailStep(StringUtils.defaultString(failStep));
		failureBill.setFailCode(StringUtils.defaultString(failCode));
		failureBill.setFailDate(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
		failureBill.setFailReason(failReason);
		Gson gson = new Gson();
		failureBill.setFailPakcet(gson.toJson(data));
		msgQueue.add(failureBill);
	}
	
	/**
	 * 插入错单数据到队列
	 * @param original
	 * @param failStep
	 * @param failCode
	 * @param failReason
	 */
	public static void addFailBillMsg(String original,String failStep,String failCode,String failReason){
		if(StringUtils.isBlank(original)){
			return;
		}
		String[] inputs = StringUtils.splitPreserveAllTokens(original,BaseConstants.FIELD_SPLIT);
		FailureBill failureBill = new FailureBill();
		if (inputs.length >= 7) {
			failureBill.setTenantId(inputs[0]);
			failureBill.setServiceType(inputs[1]);
			failureBill.setSource(inputs[2]);
			failureBill.setBsn(inputs[3]);
			failureBill.setSn(inputs[4]);
			failureBill.setAccountPeriod(inputs[5]);
			failureBill.setArrivalTime(inputs[6]);
		}
		failureBill.setFailStep(StringUtils.defaultString(failStep));
		failureBill.setFailCode(StringUtils.defaultString(failCode));
		failureBill.setFailDate(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
		failureBill.setFailReason(failReason);
		failureBill.setFailPakcet(original);
		
		msgQueue.add(failureBill);
	}
	
	
	
}
