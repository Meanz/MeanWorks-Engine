package org.meanworks.engine.util;

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
public class Timer {

	private long time;
	private long delta;
	private long milliDelta;
	private long elapsed;
	private long start;

	/**
	 * 
	 */
	public Timer() {
		start = System.nanoTime();
	}

	/**
	 * Tick the timer
	 */
	public void tick() {
		long _newTime = System.nanoTime();
		delta = time - _newTime;
		time = _newTime;
		milliDelta = delta / 1000000;
		elapsed = time - start;
	}

	/**
	 * Get the delta time in milliseconds
	 * 
	 * @return
	 */
	public long getMilliDelta() {
		return milliDelta;
	}

	/**
	 * Get the delta in nano seconds
	 * 
	 * @return
	 */
	public long getNanoDelta() {
		return delta;
	}

	/**
	 * Get the elapsed tick count
	 * 
	 * @return
	 */
	public long getTickCount() {
		return elapsed;
	}

}
