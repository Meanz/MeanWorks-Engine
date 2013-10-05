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
	 * Get the position of this transform
	 * 
	 * @return
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * Get the scale of this transform
	 * 
	 * @return
	 */
	public Vector3f getScale() {
		return scale;
	}

	/**
	 * Set the position to 0
	 */
	public void identity() {
		position.x = 0;
		position.y = 0;
		position.z = 0;
		needsUpdate = true;
	}

	/**
	 * Translate this transform
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void translate(float x, float y, float z) {
		position.x += x;
		position.y += y;
		position.z += z;
		needsUpdate = true;
	}

	/**
	 * Set the position of this transform
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
		needsUpdate = true;
	}

	/**
	 * Set the scale of this transform
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setScale(float x, float y, float z) {
		scale.x = x;
		scale.y = y;
		scale.z = z;
		needsUpdate = true;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public void rotate(float x, float y, float z) {
		rotation.x += x;
		rotation.y += y;
		rotation.z += z;
		needsUpdate = true;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public void rotate(float x, float y, float z, float w) {
		// Err
	}

	/**
	 * 
	 */
	public void calculateTransformMatrix() {
		transformMatrix.setIdentity();
		transformMatrix.translate(position);
		//Perform expensive rotation operations
		transformMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1.0f, 0.0f, 0.0f));
		transformMatrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0.0f, 1.0f, 0.0f));
		transformMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0.0f, 0.0f, 1.0f));
		//Scale
		transformMatrix.scale(scale);
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
