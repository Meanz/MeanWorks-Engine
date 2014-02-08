package org.meanworks.engine;

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
public class EngineConfig {

	/*
	 * The engine version
	 */
	public final static double MW_VERSION = 0.401d;

	/*
	 * The max number of texture units available
	 */
	public static int MAX_TEXTURE_UNITS = 8;
	/*
	 * Whether to display
	 */
	public static boolean debug = true;
	/*
	 * Hehe
	 */
	public static boolean usingModernOpenGL = true;

	/*
	 * Some debug flags
	 */
	
	// Whether we want to render the scene in wireframe or not
	public static boolean wireframeScene = false;
}
