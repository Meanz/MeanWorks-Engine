package org.meanworks.engine.math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

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
public class Transform {

	/*
	 * The transform matrix
	 */
	private Matrix4f transformMatrix;

	/*
	 * 
	 */
	private Vector3f position;

	/*
	 * 
	 */
	private Vector3f rotation;

	/*
	 * 
	 */
	private Vector3f scale;

	/*
	 * 
	 */
	private boolean needsUpdate = false;

	/**
	 * Construct a new Transform
	 */
	public Transform() {
		transformMatrix = new Matrix4f();
		position = new Vector3f();
		rotation = new Vector3f();
		scale = new Vector3f();
	}

	/**
	 * 
	 */
	public void calculateTransformMatrix() {

	}

	/**
	 * Get the transform matrix
	 * 
	 * @return
	 */
	public Matrix4f getTransformMatrix() {
		if (needsUpdate) {
			calculateTransformMatrix();
			needsUpdate = false;
		}
		return transformMatrix;
	}

}
