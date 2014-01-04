package org.meanworks.engine;

import java.util.logging.Logger;

import org.meanworks.engine.core.Application;

/**
 * Copyright (C) 2013 Steffen Evensen
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Meanz
 */
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
		Application.getApplication().getConsole().print("[DEBUG]: " + message);
		if (EngineConfig.debug)
			logger.info("" + message);
	}

	/**
	 * 
	 * @param message
	 */
	public static void warning(Object message) {
		Application.getApplication().getConsole()
				.print("[WARNING]: " + message);
		if (EngineConfig.debug)
			logger.warning("" + message);
	}

	/**
	 * 
	 * @param message
	 */
	public static void error(Object message) {
		Application.getApplication().getConsole().print("[ERROR]: " + message);
		logger.severe("" + message);
	}

}
