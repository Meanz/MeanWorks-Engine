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
public class RayResult {

	/*
	 * Whether or not the ray hit
	 */
	public boolean hit;

	/*
	 * The point where the ray hit
	 */
	public Vec3 hitPoint;
	
	/*
	 * The hit triangle
	 */
	public Triangle hitTriangle;

	/**
	 * Construct a new RayResult
	 * 
	 * @param hit
	 * @param hitPoint
	 */
	public RayResult(boolean hit, Vec3 hitPoint) {
		this.hit = hit;
		this.hitPoint = hitPoint;
		this.hitTriangle = hitTriangle;
	}

	/**
	 * Whether this ray result was successful or not
	 * 
	 * @return
	 */
	public boolean didHit() {
		return hit;
	}

	/**
	 * Get the point in space the ray collided with
	 * 
	 * @return
	 */
	public Vec3 getHitPoint() {
		return hitPoint;
	}

}
