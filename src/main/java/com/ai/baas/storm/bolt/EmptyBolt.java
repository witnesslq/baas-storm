package com.ai.baas.storm.bolt;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class EmptyBolt extends BaseRichBolt {

	private static final long serialVersionUID = -5523968230121889429L;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		
	}

	@Override
	public void execute(Tuple tuple) {
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

}
