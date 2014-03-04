package org.meanworks.engine.math;

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
public class Vec2i {

	public int x;
	public int y;

	/**
	 * Construct a new 2d integer vector with default coordinates
	 */
	public Vec2i() {
		x = 0;
		y = 0;
	}

	/**
	 * Construct a new 2d integer vector with the given coordinates
	 * 
	 * @param x
	 * @param y
	 */
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the x coordinate of this vector
	 * 
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the y coordinate of this vector
	 * 
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Add another integer vector to this vector
	 * 
	 * @param other
	 */
	public Vec2i add(Vec2i other) {
		this.x += other.x;
		this.y += other.y;
		return this;
	}

	/**
	 * Add two vectors together
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static Vec2i add(Vec2i v1, Vec2i v2) {
		return new Vec2i(v1.x + v2.x, v1.y + v2.y);
	}

	/**
	 * Subtract another integer vector from this vector
	 * 
	 * @param other
	 */
	public Vec2i sub(Vec2i other) {
		this.x -= other.x;
		this.y -= other.y;
		return this;
	}

	/**
	 * Subtract two vectors
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static Vec2i sub(Vec2i v1, Vec2i v2) {
		return new Vec2i(v1.x - v2.x, v1.y - v2.y);
	}
	
	/**
	 * Get the distance between this vector and the given coordinates
	 * @param x2
	 * @param y2
	 * @return
	 */
	public double dist(int x2, int y2) {
		return dist(new Vec2i(x2, y2));
	}

	/**
	 * Get the distance between this vector and the given vector
	 * @param other
	 * @return
	 */
	public double dist(Vec2i other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
	}
	
	/**
	 * Check if another object equals this
	 * 
	 * @param other
	 * @return
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other instanceof Vec2i) {
			Vec2i otherv = (Vec2i) other;
			if (x == otherv.x && y == otherv.y) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Check if the given vectors are equal
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static boolean equals(Vec2i v1, Vec2i v2) {
		return v1.x == v2.x && v1.y == v2.y;
	}

}
