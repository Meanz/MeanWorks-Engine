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
public class NumberFormatter {

	public static String formatNumber(int number) {

		if (number > 1000) {
			if (number > 1000000) {
				// Use mills
				double newNumber = number / 1000000d;
				return newNumber + "m";
			} else {
				// Use K's
				double newNumber = number / 1000d;
				return newNumber + "k";
			}
		} else {
			// We can do commas here
			return "" + number;
		}
	}

}
