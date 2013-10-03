package org.meanworks.engine.math;

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
public class Mat4 {

	private float[][] values;

	public Mat4() {
		values = new float[4][4];
		identity();
	}

	/**
	 * Set this matrix to an identity matrix
	 */
	public void identity() {
		values[0][0] = 1.0f;
		values[1][1] = 1.0f;
		values[2][2] = 1.0f;
		values[3][3] = 1.0f;
	}

	/**
	 * Multiply this matrix by the other matrix given Where this matrix is on
	 * the left hand side
	 */
	public void mul(Mat4 other) {

	}

}
