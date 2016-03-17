package com.ai.baas.storm.flow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

import com.ai.baas.storm.spout.KafkaSpoutAgent;
import com.ai.baas.storm.util.BaseConstants;
import com.ai.baas.storm.util.LoadConfig;

public abstract class BaseFlow {

	private static Logger logger = LoggerFactory.getLogger(BaseFlow.class);
	protected Map<String, String> conf = new HashMap<String, String>();
	protected String topologyName;
	protected TopologyBuilder builder;
	
	private void loadConf(String path){
		if (path.endsWith("yaml")){
			conf = LoadConfig.loadYaml(path);
			topologyName = conf.get(BaseConstants.TOPOLOGY_NAME);
		}else{
			exit("Load Yaml config failure");
		}
	}
	
	
	/**
	 * 定义拓扑的主流程
	 */
	public abstract void define();
	
	/**
	 * 本地执行拓扑
	 */
	public void setLocalTopology(){
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(topologyName, conf, builder.createTopology());
		try {
			TimeUnit.SECONDS.sleep(300);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		cluster.killTopology(topologyName);
		cluster.shutdown();
	}
	
	/**
	 * 远程执行拓扑
	 */
	public void setRemoteTopology(){
		try {
			StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
		} catch (AlreadyAliveException e) {
			logger.error(e.getMessage());
		} catch (InvalidTopologyException e) {
			logger.error(e.getMessage());
		}
	}
	
	
	/**
	 * 程序执行方法
	 * @param args
	 */
	public void run(String[] args){
		if (args.length == 0) {
			exit("Please input configuration file");
		}
		loadConf(args[0]);
		System.out.println("conf="+conf.toString());
		builder = new TopologyBuilder();
		define();
		if (local_mode()) {
			setLocalTopology();
		} else {
			setRemoteTopology();
		}
	}
	
	/**
	 * 强行退出
	 * @param errorMessage
	 */
	private void exit(String errorMessage){
		System.err.println(errorMessage);
		System.exit(-1);
	}
	
	private boolean local_mode(){
		String mode = (String) conf.get(Config.STORM_CLUSTER_MODE);
        if (mode != null) {
            if ("local".equals(mode)) {
                return true;
            }
            if ("distributed".equals(mode)) {
                return false;
            }
        }
        throw new IllegalArgumentException("Illegal cluster mode in conf [storm.cluster.mode]:" + mode);
	}
	
	/**
	 * 得到并行数
	 * @param param
	 * @param defaultNum
	 * @return
	 */
	public int getParallelNum(String param, int defaultNum){
		String str = conf.get(BaseConstants.TOPOLOGY_SPOUT_PARALLEL);
		if (StringUtils.isEmpty(str)) {
			logger.warn(param + " is not set , use default " + defaultNum);
			return defaultNum;
		} else {
			return Integer.parseInt(str);
		}
	}
	
	/**
	 * kafkaSpout
	 */
	public void setKafkaSpout(){
		KafkaSpoutAgent agent = new KafkaSpoutAgent(conf);
		builder.setSpout(BaseConstants.KAFKA_SPOUT_NAME, agent.getKafkaSpout(), getParallelNum(BaseConstants.KAFKA_SPOUT_PARALLEL_NUM, 1));
	}
	
	
}
