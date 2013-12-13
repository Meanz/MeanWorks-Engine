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
public class Vec2 {
	/*
	 * The values of this vector
	 */
	public float x;
	public float y;

	/**
	 * Create a vector with the given values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new empty vector
	 */
	public Vec2() {
		this(0.0f, 0.0f);
	}

	/**
	 * Copy the given vector
	 * 
	 * @param other
	 */
	public Vec2(Vec2 other) {
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * Add the given values to this vector
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Add the given vector to this vector
	 * 
	 * @param other
	 */
	public Vec2 add(Vec2 other) {
		return add(other.x, other.y);
	}

	/**
	 * Add the given vectors
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Vec2 add(Vec2 lhs, Vec2 rhs) {
		return new Vec2(lhs.x + rhs.x, lhs.y + rhs.y);
	}

	/**
	 * Translate this vector by the given values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec2 translate(float x, float y) {
		return add(x, y);
	}

	/**
	 * Translate this vector by the given vector
	 * 
	 * @param other
	 */
	public Vec2 translate(Vec2 other) {
		return add(other.x, other.y);
	}

	/**
	 * Subtract this vector by the given values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec2 sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	/**
	 * Subtract this vector by another vector
	 * 
	 * @param other
	 */
	public Vec2 sub(Vec2 other) {
		return sub(other.x, other.y);
	}

	/**
	 * Substract the given vectors
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Vec2 sub(Vec2 lhs, Vec2 rhs) {
		return new Vec2(lhs.x - rhs.x, lhs.y - rhs.y);
	}

	/**
	 * Multiply this vector by the given values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec2 mul(float x, float y) {
		this.x *= x;
		this.y *= y;
		return this;
	}

	/**
	 * Multiply this vector by the given vector
	 * 
	 * @param other
	 */
	public Vec2 mul(Vec2 other) {
		return mul(other.x, other.y);
	}

	/**
	 * Multiply the given vectors
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Vec2 mul(Vec2 lhs, Vec2 rhs) {
		return new Vec2(lhs.x * rhs.x, lhs.y * rhs.y);
	}

	/**
	 * Scale this vector by the given scale value
	 * 
	 * @param scale
	 */
	public Vec2 scale(float scale) {
		return mul(scale, scale);
	}

	/**
	 * Scale the given vector by the given scale value
	 * 
	 * @param in
	 * @param scale
	 * @return
	 */
	public static Vec2 scale(Vec2 in, float scale) {
		return new Vec2(in.x * scale, in.y * scale);
	}

	/**
	 * Get the length of this vector
	 * 
	 * @return
	 */
	public float getLength() {
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * Negate this vector
	 */
	public Vec2 negate() {
		this.x = -x;
		this.y = -y;
		return this;
	}

	/**
	 * Negate the given vector
	 * 
	 * @param vec
	 * @return
	 */
	public static Vec2 negate(Vec2 vec) {
		return new Vec2(-vec.x, -vec.y);
	}

	/**
	 * Normalize this vector
	 */
	public Vec2 normalize() {
		float l = getLength();
		x /= l;
		y /= l;
		return this;
	}

	/**
	 * Normalize the given vector
	 * 
	 * @param vec
	 * @return
	 */
	public static Vec2 normalize(Vec2 vec) {
		float l = vec.getLength();
		return new Vec2(vec.x / l, vec.y / l);
	}

}
