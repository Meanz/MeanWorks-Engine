package org.meanworks.engine;

import java.util.logging.Logger;

public class EngineLogger {

	/*
	 * 
	 */
	private static Logger logger = Logger.getLogger("FractalEngine");

	/**
	 * 
	 * @param message
	 */
	public static void info(Object message) {
		if (EngineConfig.debug)
			logger.info("" + message);
	}

	/**
	 * 
	 * @param message
	 */
	public static void warning(Object message) {
		if (EngineConfig.debug)
			logger.warning("" + message);
	}

	/**
	 * 
	 * @param message
	 */
	public static void error(Object message) {
		logger.severe("" + message);
	}

}
