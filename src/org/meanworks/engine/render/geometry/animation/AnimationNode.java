package org.meanworks.engine.render.geometry.animation;

import java.util.LinkedList;

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
public class AnimationNode {
	/**
	 * Class container for a vector3f keyframe
	 * 
	 * @author meanz
	 * 
	 */
	public static class Vec3Key {

		float x;
		float y;
		float z;
		double time;

		public Vec3Key(float x, float y, float z, double time) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.time = time;
		}

	}

	/**
	 * Class container for a quaternion keyframe
	 * 
	 * @author meanz
	 * 
	 */
	public static class QuatKey {

		// Need a quat class here
		float x;
		float y;
		float z;
		float w;
		double time;

		public QuatKey(float x, float y, float z, float w, double time) {
			this.time = time;
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}

	}

	/*
	 * The name of the animation node
	 */
	private String name;

	/*
	 * The position keys in this animation node
	 */
	private Vec3Key[] positionKeys;
	/*
	 * The rotation keys in this animation node
	 */
	private QuatKey[] rotationKeys;
	/*
	 * The scaling keys in this animation node
	 */
	private Vec3Key[] scalingKeys;

	/**
	 * Construct a new animation
	 * 
	 * @param animationName
	 */
	public AnimationNode(String animationName, int numPositionKeys,
			int numRotationKeys, int numScalingKeys) {
		this.name = animationName;
		positionKeys = new Vec3Key[numPositionKeys];
		rotationKeys = new QuatKey[numRotationKeys];
		scalingKeys = new Vec3Key[numScalingKeys];
	}

	/**
	 * Get the name of this animation node
	 * 
	 * @return
	 */
	public String getNodeName() {
		return name;
	}

	/**
	 * Get the number of position keys in this node
	 * 
	 * @return
	 */
	public int getNumPositionKeys() {
		return positionKeys.length;
	}

	/**
	 * Get the number of rotation keys in this node
	 * 
	 * @return
	 */
	public int getNumRotationKeys() {
		return rotationKeys.length;
	}

	/**
	 * Get the number of scaling keys in this node
	 * 
	 * @return
	 */
	public int getNumScalingKeys() {
		return scalingKeys.length;
	}

	/**
	 * Get the position key at the given index
	 * 
	 * @param index
	 * @return
	 */
	public Vec3Key getPositionKey(int index) {
		return positionKeys[index];
	}

	/**
	 * Get the rotation key at the given index
	 * 
	 * @param index
	 * @return
	 */
	public QuatKey getRotationKey(int index) {
		return rotationKeys[index];
	}

	/**
	 * Get the scaling key at the given index
	 * 
	 * @param index
	 * @return
	 */
	public Vec3Key getScalingKey(int index) {
		return scalingKeys[index];
	}

	/**
	 * Add a position keyframe to this animation
	 * 
	 * @param key
	 */
	public void setPositionKey(int index, Vec3Key key) {
		positionKeys[index] = key;
	}

	/**
	 * Add a rotation keyframe to this animation
	 * 
	 * @param key
	 */
	public void setRotationKey(int index, QuatKey key) {
		rotationKeys[index] = key;
	}

	/**
	 * Add a scaling keyframe to this animation
	 * 
	 * @param key
	 */
	public void setScalingKey(int index, Vec3Key key) {
		scalingKeys[index] = key;
	}
}
