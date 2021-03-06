package org.meanworks.engine.bounding;

import org.lwjgl.util.vector.Vector3f;
import org.meanworks.engine.math.Ray;
import org.meanworks.engine.math.Vec3;

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
public class AABoundingBox {

	/*
	 * The minimum values of the bounds
	 */
	private Vec3 min;

	/*
	 * The maximum values of the bounds
	 */
	private Vec3 max;

	/**
	 * Create a new bounding box with the given values
	 * 
	 * @param min
	 * @param max
	 */
	public AABoundingBox(Vec3 min, Vec3 max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Create a new empty bounding box
	 */
	public AABoundingBox() {
		this(new Vec3(), new Vec3());
	}

	/**
	 * Get the minimum point of the bb
	 * 
	 * @return
	 */
	public Vec3 getMin() {
		return min;
	}

	/**
	 * Get the maximum point of the bb
	 * 
	 * @return
	 */
	public Vec3 getMax() {
		return max;
	}

	/**
	 * Get a translated version of this bounding box
	 * 
	 * @return
	 */
	public AABoundingBox getTranslated(Vec3 translation) {
		return new AABoundingBox(new Vec3(min.x + translation.x, min.y
				+ translation.y, min.z + translation.z), new Vec3(max.x
				+ translation.x, max.y + translation.y, max.z + translation.z));
	}

	/**
	 * Update this bounding box
	 * 
	 * @param min
	 * @param max
	 */
	public void update(Vec3 min, Vec3 max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Update the min bounds of this bounding box
	 * 
	 * @param min
	 */
	public void updateMin(Vec3 min) {
		update(min, max);
	}

	/**
	 * Update the max bounds of this bounding box
	 * 
	 * @param max
	 */
	public void updateMax(Vec3 max) {
		update(min, max);
	}

	/**
	 * Scale this bounding box
	 * 
	 * @param scale
	 */
	public void scale(float scale) {
		min.scale(scale);
		max.scale(scale);
	}

	/**
	 * 
	 * @param r
	 * @return
	 */
	public static boolean intersects(AABoundingBox box, Ray r) {
		float tmin = (box.min.x - r.origin.x) / r.direction.x;
		float tmax = (box.max.x - r.origin.x) / r.direction.x;
		if (tmin > tmax) {
			float temp = tmin;
			tmin = tmax;
			tmax = temp;
		}
		float tymin = (box.min.y - r.origin.y) / r.direction.y;
		float tymax = (box.max.y - r.origin.y) / r.direction.y;
		if (tymin > tymax) {
			float temp = tymin;
			tymin = tymax;
			tymax = temp;
		}
		if ((tmin > tymax) || (tymin > tmax)) {
			return false;
		}
		if (tymin > tmin) {
			tmin = tymin;
		}
		if (tymax < tmax) {
			tmax = tymax;
		}
		float tzmin = (box.min.z - r.origin.z) / r.direction.z;
		float tzmax = (box.max.z - r.origin.z) / r.direction.z;
		if (tzmin > tzmax) {
			float temp = tzmin;
			tzmin = tzmax;
			tzmax = temp;
		}
		if ((tmin > tzmax) || (tzmin > tmax)) {
			return false;
		}
		if (tzmin > tmin) {
			tmin = tzmin;
		}
		if (tzmax < tmax) {
			tmax = tzmax;
		}
		return true;
	}

	public static void printAABB(AABoundingBox box) {

		System.err.println(" -- Axis Aligned Bounding Box --");
		System.err.println("Min: " + box.min.toString());
		System.err.println("Max: " + box.max.toString());
	}

	/**
	 * Meep
	 * @param box
	 * @param ray
	 * @param result
	 * @return
	 */
	public static boolean intersects(AABoundingBox box, Ray ray, Vec3 result) {
		// r.dir is unit direction vector of ray
		Vec3 dirfrac = new Vec3();
		dirfrac.x = 1.0f / ray.direction.x; // Normalize it?
		dirfrac.y = 1.0f / ray.direction.y; // Normalize it?
		dirfrac.z = 1.0f / ray.direction.z; // Normalize it?
		// lb is the corner of AABB with minimal coordinates - left bottom, rt
		// is maximal corner
		// r.org is origin of ray
		float t1 = (box.min.x - ray.origin.x) * dirfrac.x;
		float t2 = (box.max.x - ray.origin.x) * dirfrac.x;
		float t3 = (box.min.y - ray.origin.y) * dirfrac.y;
		float t4 = (box.max.y - ray.origin.y) * dirfrac.y;
		float t5 = (box.min.z - ray.origin.z) * dirfrac.z;
		float t6 = (box.max.z - ray.origin.z) * dirfrac.z;

		float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)),
				Math.min(t5, t6));
		float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)),
				Math.max(t5, t6));

		float t = 0f;

		// if tmax < 0, ray (line) is intersecting AABB, but whole AABB is
		// behing us
		if (tmax < 0) {
			t = tmax;
			return false;
		}

		// if tmin > tmax, ray doesn't intersect AABB
		if (tmin > tmax) {
			t = tmax;
			return false;
		}

		t = tmin;
		result.x = ray.origin.x + (ray.direction.x * t);
		result.y = ray.origin.y + (ray.direction.y * t);
		result.z = ray.origin.z + (ray.direction.z * t);
		return true;
	}

	/**
	 * Tests if the given boxes intersects each other
	 * 
	 * @param box1
	 * @param box2
	 * @return
	 */
	public static boolean intersects(AABoundingBox box1, AABoundingBox box2) {
		return (box1.max.x > box2.min.x && box1.min.x < box2.max.x
				&& box1.max.y > box2.min.y && box1.min.y < box2.max.y
				&& box1.max.z > box2.min.z && box1.min.z < box2.max.z);
	}

	/**
	 * Check if a point is inside the given box
	 * 
	 * @param box
	 * @param point
	 * @return
	 */
	public static boolean isPointInside(AABoundingBox box, Vec3 point) {
		// Check if the point is less than max and greater than min
		if (point.x > box.min.x && point.x < box.max.x && point.y > box.min.y
				&& point.y < box.max.y && point.z > box.min.z
				&& point.z < box.max.z) {
			return true;
		}
		return false;
	}
}
