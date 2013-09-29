package org.fractalstudio.render.geometry.animation;

import org.lwjgl.util.vector.Matrix4f;

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
public class Bone {

	/*
	 * The name of this bone
	 */
	private String boneName;

	/*
	 * Reference to the index of this bone
	 */
	private int boneId;

	/*
	 * The bone matrix, the same as offsetMatrix?
	 */
	public Matrix4f jointMatrix;

	/*
	 * The offset matrix of this bone
	 */
	public Matrix4f offsetMatrix;

	/*
	 * The current transform of this bone This is the transform we use when
	 * rendering
	 */
	public Matrix4f transform;

	/**
	 * Construct a new bone
	 */
	public Bone(String boneName) {

	}

	/**
	 * Get the index of this bone
	 * 
	 * @return
	 */
	public int getBoneId() {
		return boneId;
	}

	/**
	 * Get the joint matrix of this bone
	 * 
	 * @return
	 */
	public Matrix4f getJointMatrix() {
		return jointMatrix;
	}

	/**
	 * Get the current transform of this bone
	 * 
	 * @return
	 */
	public Matrix4f getTransform() {
		return transform;
	}

	/**
	 * Set the transform for this bone
	 * 
	 * @param transform
	 */
	public void setTransform(Matrix4f transform) {
		this.transform = transform;
	}
}
