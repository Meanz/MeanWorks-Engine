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
	 * The position of this transform
	 */
	private Vec3 position;

	/*
	 * The rotation of this transform
	 */
	private Vec3 rotation;

	/*
	 * The scale of this transform
	 */
	private Vec3 scale;

	/*
	 * Whether this transform needs an update or not
	 */
	private boolean needsUpdate = false;

	/*
	 * Whether this transform has changed or not
	 */
	private boolean isChanged = true;

	/**
	 * Construct a new Transform
	 */
	public Transform() {
		transformMatrix = new Matrix4f();
		position = new Vec3();
		rotation = new Vec3();
		scale = new Vec3();
		scale.x = 1.0f;
		scale.y = 1.0f;
		scale.z = 1.0f;
	}

	/**
	 * Unflags the is changed flag
	 */
	public void unflagChange() {
		isChanged = false;
	}

	/**
	 * Check whether or not this transform has changed or not
	 * 
	 * @return
	 */
	public boolean isChanged() {
		return isChanged;
	}

	/**
	 * Get the yaw of this transform in degrees
	 * 
	 * @return
	 */
	public float getYaw() {
		return rotation.y;
	}

	/**
	 * Get the pitch of this transform in degrees
	 * 
	 * @return
	 */
	public float getPitch() {
		return rotation.x;
	}

	/**
	 * Get the position of this transform
	 * 
	 * @return
	 */
	public Vec3 getPosition() {
		return position;
	}

	/**
	 * Get the scale of this transform
	 * 
	 * @return
	 */
	public Vec3 getScale() {
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
		isChanged = true;
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
		isChanged = true;
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
		isChanged = true;
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
		isChanged = true;
	}

	/**
	 * Set the rotation of this transform
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRotation(float x, float y, float z) {
		rotation.x = x;
		rotation.y = y;
		rotation.z = z;
		needsUpdate = true;
		isChanged = true;
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
		isChanged = true;
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
	 * Calculates the transform matrix
	 */
	public void calculateTransformMatrix() {
		transformMatrix.setIdentity();
		transformMatrix.translate(new Vector3f(position.x, position.y,
				position.z));
		// Perform expensive rotation operations
		transformMatrix.rotate((float) Math.toRadians(rotation.x),
				new Vector3f(1.0f, 0.0f, 0.0f));
		transformMatrix.rotate((float) Math.toRadians(rotation.y),
				new Vector3f(0.0f, 1.0f, 0.0f));
		transformMatrix.rotate((float) Math.toRadians(rotation.z),
				new Vector3f(0.0f, 0.0f, 1.0f));
		// Scale
		transformMatrix.scale(new Vector3f(scale.x, scale.y, scale.z));
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
