package org.meanworks.engine.math;

import org.lwjgl.util.vector.Vector3f;

/**
 * Simple structure for defining a ray
 * 
 * @author meanz
 * 
 */
public class Ray {

	/*
	 * The ray origin
	 */
	public Vec3 origin;
	/*
	 * The ray direction
	 */
	public Vec3 direction;
	/*
	 * The minimum and maximum length of the ray
	 */
	private float minLength = 0.0f;
	private float maxLength = 1000.0f;

	/**
	 * Construct a new ray
	 * 
	 * @param origin
	 * @param direction
	 */
	public Ray(Vec3 origin, Vec3 direction) {
		this.origin = origin;
		this.direction = direction;
	}

	/**
	 * Get the minimum length of the ray
	 * 
	 * @return
	 */
	public float getMinLength() {
		return minLength;
	}

	/**
	 * Get the max length of the ray
	 * 
	 * @return
	 */
	public float getMaxLength() {
		return maxLength;
	}

	/**
	 * Get the origin point of this ray
	 * 
	 * @return The origin point of this ray
	 */
	public Vec3 getOrigin() {
		return origin;
	}

	/**
	 * Get the direction of this ray
	 * 
	 * @return The direction of this ray
	 */
	public Vec3 getDirection() {
		return direction;
	}

}
