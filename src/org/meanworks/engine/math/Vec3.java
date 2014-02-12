package org.meanworks.engine.math;

import org.lwjgl.util.vector.Matrix4f;

/**
 * Copyother (C) 2013 Steffen Evensen
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
public class Vec3 {

	/*
	 * The values of this vector
	 */
	public float x;
	public float y;
	public float z;

	/**
	 * Create a vector with the given values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Create a new empty vector
	 */
	public Vec3() {
		this(0.0f, 0.0f, 0.0f);
	}

	/**
	 * Copy the given vector
	 * 
	 * @param other
	 */
	public Vec3(Vec3 other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}

	/**
	 * Add the given values to this vector
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	/**
	 * Add the given vector to this vector
	 * 
	 * @param other
	 */
	public Vec3 add(Vec3 other) {
		return add(other.x, other.y, other.z);
	}

	/**
	 * Add the given vectors
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Vec3 add(Vec3 lhs, Vec3 rhs) {
		return new Vec3(lhs.x + rhs.x, lhs.y + rhs.y, lhs.z + rhs.z);
	}

	/**
	 * Translate this vector by the given values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec3 translate(float x, float y, float z) {
		return add(x, y, z);
	}

	/**
	 * Translate this vector by the given vector
	 * 
	 * @param other
	 */
	public Vec3 translate(Vec3 other) {
		return add(other.x, other.y, other.z);
	}
	
	/**
	 * Translate this vector by the given matrix
	 * 
	 * @param other
	 */
	public Vec3 translate(Matrix4f other) {
		return add(other.m30, other.m31, other.m32);
	}
	
	/**
	 * Translate this vector by the given matrix
	 * 
	 * @param other
	 */
	public Vec3 translateN(Matrix4f other) {
		return add(-other.m30, -other.m31, -other.m32);
	}


	/**
	 * Subtract this vector by the given values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec3 sub(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	/**
	 * Subtract this vector by another vector
	 * 
	 * @param other
	 */
	public Vec3 sub(Vec3 other) {
		return sub(other.x, other.y, other.z);
	}

	/**
	 * Substract the given vectors
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Vec3 sub(Vec3 lhs, Vec3 rhs) {
		return new Vec3(lhs.x - rhs.x, lhs.y - rhs.y, lhs.z - rhs.z);
	}

	/**
	 * Multiply this vector by the given values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec3 mul(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}

	/**
	 * Multiply this vector by the given vector
	 * 
	 * @param other
	 */
	public Vec3 mul(Vec3 other) {
		return mul(other.x, other.y, other.z);
	}

	/**
	 * Multiply the given vectors
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Vec3 mul(Vec3 lhs, Vec3 rhs) {
		return new Vec3(lhs.x * rhs.x, lhs.y * rhs.y, lhs.z * rhs.z);
	}

	/**
	 * Scale this vector by the given scale value
	 * 
	 * @param scale
	 */
	public Vec3 scale(float scale) {
		return mul(scale, scale, scale);
	}

	/**
	 * Scale the given vector by the given scale value
	 * 
	 * @param in
	 * @param scale
	 * @return
	 */
	public static Vec3 scale(Vec3 in, float scale) {
		return new Vec3(in.x * scale, in.y * scale, in.z * scale);
	}

	/**
	 * Get the length of this vector
	 * 
	 * @return
	 */
	public float getLength() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Negate this vector
	 */
	public Vec3 negate() {
		this.x = -x;
		this.y = -y;
		this.z = -z;
		return this;
	}

	/**
	 * Negate the given vector
	 * 
	 * @param vec
	 * @return
	 */
	public static Vec3 negate(Vec3 vec) {
		return new Vec3(-vec.x, -vec.y, -vec.z);
	}

	/**
	 * Normalize this vector
	 */
	public Vec3 normalize() {
		float l = getLength();
		x /= l;
		y /= l;
		z /= l;
		return this;
	}

	/**
	 * Normalize the given vector
	 * 
	 * @param vec
	 * @return
	 */
	public static Vec3 normalize(Vec3 vec) {
		float l = vec.getLength();
		return new Vec3(vec.x / l, vec.y / l, vec.z / l);
	}

	/**
	 * Cross this vector by the given vector
	 * 
	 * @param other
	 */
	public static Vec3 cross(Vec3 lhs, Vec3 rhs) {
		return new Vec3(lhs.y * rhs.z - lhs.z * rhs.y, rhs.x * lhs.z - rhs.z
				* lhs.x, lhs.x * rhs.y - lhs.y * rhs.x);
	}

	/**
	 * The dot product of two vectors is calculated as v1.x * v2.x + v1.y * v2.y
	 * + v1.z * v2.z
	 * 
	 * @param rhs
	 *            The RHS vector
	 * @return left dot other
	 */
	public float dot(Vec3 other) {
		return x * other.x + y * other.y + z * other.z;
	}

	/**
	 * Calculate the angle between two vectors, in radians
	 * 
	 * @param b
	 *            The other vector
	 * @return the angle between the two vectors, in radians
	 */
	public float angle(Vec3 b) {
		float dls = dot(b) / (getLength() * b.getLength());
		if (dls < -1f)
			dls = -1f;
		else if (dls > 1.0f)
			dls = 1.0f;
		return (float) Math.acos(dls);
	}
	
	
	public Vec3 copy() {
		return new Vec3(x, y, z);
	}

	@Override
	public String toString() {
		return "Vec3(" + x + ", " + y + ", " + z + ")";
	}
}
