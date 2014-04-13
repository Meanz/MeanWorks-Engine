package org.meanworks.engine.render;

/**
 * Copyright (C) 2014 Steffen Evensen
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
public class VertexFormat {

	/**
	 * Different kinds of input slots
	 */
	public final static int POSITION = 0;
	public final static int NORMAL = 1;
	public final static int UV = 2;
	public final static int TANGENT = 3;
	public final static int CUSTOM0 = 4;
	public final static int CUSTOM1 = 5;
	public final static int CUSTOM2 = 6;

	/**
	 * 
	 */
	public int[] slots;

	/**
	 * 
	 * @param slots
	 */
	public VertexFormat(int... types) {
		this.slots = slots;
	}

	/**
	 * 
	 * @return
	 */
	public int[] getSlots() {
		return slots;
	}

	/**
	 * A vertex buffer uses this function to communicate with the shader
	 */
	public void apply() {

		
		
	}

}
