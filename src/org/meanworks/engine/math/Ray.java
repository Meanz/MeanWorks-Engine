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
	public Vector3f origin;
	/*
	 * The ray direction
	 */
	public Vector3f direction;

	/**
	 * Construct a new ray
	 * 
	 * @param origin
	 * @param direction
	 */
	public Ray(Vector3f origin, Vector3f direction) {
		this.origin = origin;
		this.direction = direction;
	}

	/**
	 * Get the origin point of this ray
	 * 
	 * @return The origin point of this ray
	 */
	public Vector3f getOrigin() {
		return origin;
	}

	/**
	 * Get the direction of this ray
	 * 
	 * @return The direction of this ray
	 */
	public Vector3f getDirection() {
		return direction;
	}

}
