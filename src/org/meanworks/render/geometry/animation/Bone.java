package org.meanworks.render.geometry.animation;

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
	 * The offset matrix of this bone
	 */
	public Matrix4f offsetMatrix;

	/*
	 * The current transform of this bone This is the transform we use when
	 * rendering
	 */
	public Matrix4f transform;

	/*
	 * 
	 */
	public Matrix4f localTransform = new Matrix4f();

	/*
	 * 
	 */
	public Matrix4f globalTransform = new Matrix4f();

	/*
	 * The parent bone of this bone
	 */
	private Bone parent;

	/**
	 * Construct a new bone
	 */
	public Bone(String boneName) {
		this.boneName = boneName;
		parent = null;
	}

	/**
	 * Special copy constructor Does not really work because it won't copy
	 * parents.
	 */
	public Bone(Bone other) {
		this.boneId = other.boneId;
		this.boneName = other.boneName;
		this.offsetMatrix = other.offsetMatrix;
		this.transform = new Matrix4f();
	}

	/**
	 * Calculate the global transform of this bone
	 */
	public Matrix4f calculateGlobalTransform() {
		Bone node = this;
		node.globalTransform = node.localTransform;
		Bone pNode = node.parent;

		while (pNode != null) {
			node.globalTransform = Matrix4f.mul(pNode.localTransform,
					node.globalTransform, null);
			pNode = pNode.parent;
		}

		return node.globalTransform;
	}
	
	
	
	public Matrix4f getGlobalTransform() {
		return this.globalTransform;
	}
	
	public void setGlobalTransform(Matrix4f globalTransform) {
		this.globalTransform = globalTransform;
	}
	
	public Matrix4f getLocalTransform() {
		return this.localTransform;
	}
	
	public void setLocalTransform(Matrix4f localTransform) {
		this.localTransform = localTransform;
	}

	/**
	 * Get the parent bone of this bone
	 * 
	 * @return
	 */
	public Bone getParent() {
		return parent;
	}

	/**
	 * Set the parent bone of this bone
	 * 
	 * @param parent
	 */
	public void setParent(Bone parent) {
		this.parent = parent;
	}

	/**
	 * Get the name of this bone
	 * 
	 * @return
	 */
	public String getBoneName() {
		return boneName;
	}

	/**
	 * Get the offset matrix of this bone
	 * 
	 * @return
	 */
	public Matrix4f getOffsetMatrix() {
		return offsetMatrix;
	}

	/**
	 * Set the offset matrix of this bone
	 * 
	 * @param offsetMatrix
	 */
	public void setOffsetMatrix(Matrix4f offsetMatrix) {
		this.offsetMatrix = offsetMatrix;
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
