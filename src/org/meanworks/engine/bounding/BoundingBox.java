package org.meanworks.engine.bounding;

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
public class BoundingBox {

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
	public BoundingBox(Vec3 min, Vec3 max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Create a new empty bounding box
	 */
	public BoundingBox() {
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
	 * Tests if the given boxes intersects each other
	 * 
	 * @param box1
	 * @param box2
	 * @return
	 */
	public static boolean intersects(BoundingBox box1, BoundingBox box2) {
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
	public static boolean isPointInside(BoundingBox box, Vec3 point) {
		// Check if the point is less than max and greater than min
		if (point.x > box.min.x && point.x < box.max.x && point.y > box.min.y
				&& point.y < box.max.y && point.z > box.min.z
				&& point.z < box.max.z) {
			return true;
		}
		return false;
	}
}
