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

}
