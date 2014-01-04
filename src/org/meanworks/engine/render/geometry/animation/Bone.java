package org.meanworks.engine.render.geometry.animation;

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
	 * The local transform of this bone This transform tells us how this bone is
	 * rotated in relation to it's parent
	 */
	public Matrix4f localTransform = new Matrix4f();

	/*
	 * This transform tells us the final transformation for the bone
	 */
	public Matrix4f globalTransform = new Matrix4f();

	/*
	 * The parent bone of this bone
	 */
	private Bone parent;

	/*
	 * The children of this bone
	 */
	private Bone[] children;

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
		this.boneName = new String(other.boneName);
		this.offsetMatrix = new Matrix4f().load(other.offsetMatrix);
		this.localTransform = new Matrix4f().load(other.localTransform);
		this.globalTransform = new Matrix4f().load(other.globalTransform);
		// The skeleton has to set this bone's parent
		this.parent = null;
	}

	public Matrix4f calculateGlobalTransform_new() {
		
		final Bone node = this;
		
		node.globalTransform.load(node.localTransform);
		
		if(parent != null) {
			Matrix4f.mul(parent.globalTransform, node.localTransform,
					node.globalTransform);
		}
		
		return node.globalTransform;
	}
	
	/**
	 * Calculate the global transform of this bone
	 */
	public Matrix4f calculateGlobalTransform() {
		final Bone node = this;
		// Set the global transform to this localtransform
		node.globalTransform.load(node.localTransform);
		// Ref to the curr it node
		Bone pNode = node.parent;
		while (pNode != null) {
			Matrix4f.mul(pNode.localTransform, node.globalTransform,
					node.globalTransform);
			pNode = pNode.parent;
		}
		return node.globalTransform;
	}

	/**
	 * Get the global transform of this bone
	 * 
	 * @return
	 */
	public Matrix4f getGlobalTransform() {
		return this.globalTransform;
	}

	/**
	 * Set the global transform of this bone
	 * 
	 * @param globalTransform
	 */
	public void setGlobalTransform(Matrix4f globalTransform) {
		this.globalTransform = globalTransform;
	}

	/**
	 * Get the local transform of this bone
	 * 
	 * @return
	 */
	public Matrix4f getLocalTransform() {
		return this.localTransform;
	}

	/**
	 * Set the local transform of this bone
	 * 
	 * @param localTransform
	 */
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
	 * Set the id of this bone
	 * 
	 * @param boneId
	 */
	public void setBoneId(int boneId) {
		this.boneId = boneId;
	}

	/**
	 * Get the children of this bone
	 * 
	 * @return
	 */
	public Bone[] getChildren() {
		return children;
	}

	/**
	 * Set the children of this bone
	 * 
	 * @param children
	 */
	public void setChildren(Bone[] children) {
		this.children = children;
	}
}
