package com.ai.baas.storm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoopThread extends Thread {
	private static Logger logger = LoggerFactory.getLogger(LoopThread.class);
	public boolean exitFlag = false;

	public abstract boolean init();

	public abstract boolean unInit();

	public abstract void work();

	public void run() {
		if (init()) {
			for (;;) {
				work();
				if (exitFlag)
					break;
			}
		}
		unInit();
		logger.debug("Thread [" + getName() + "] is terminated!");
	}
	
	
}
