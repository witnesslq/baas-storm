package com.ai.baas.storm.failbill;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.storm.util.LoopThread;

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
	
}
