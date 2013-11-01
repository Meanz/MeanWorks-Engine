package org.meanworks.render.geometry.animation;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.render.geometry.animation.AnimationNode.QuatKey;
import org.meanworks.render.geometry.animation.AnimationNode.Vec3Key;

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
public class AnimationChannel {

	/*
	 * The current time in this channel
	 */
	private double currentTime;

	/*
	 * The speed modifier
	 */
	private double speedModifier;

	/*
	 * The animation we are using
	 */
	private Animation animation;

	/*
	 * The reference to the skeleton we are manipulating
	 */
	private Skeleton skeleton;

	/*
	 * The node positions
	 */
	private Vector3f[] nodePositions;

	/*
	 * 
	 */
	private LoopMode loopMode;

	/*
	 * Whether this animation has finished or not
	 */
	private boolean finished;

	/**
	 * Construct a new animation channel
	 * 
	 * @param animation
	 */
	public AnimationChannel(Skeleton skeleton, Animation animation) {
		this.skeleton = skeleton;
		playAnimation(animation, LoopMode.LM_DONT_LOOP);
	}

	/**
	 * Construct a new animation channel
	 * 
	 * @param animation
	 */
	public AnimationChannel(Skeleton skeleton) {
		this.skeleton = skeleton;
		this.finished = true;
	}

	/**
	 * Get the active animation
	 * 
	 * @return
	 */
	public Animation getAnimation() {
		return animation;
	}

	/**
	 * Plays the given animation
	 * 
	 * @param animation
	 */
	public void playAnimation(Animation animation, LoopMode loopMode) {
		this.animation = animation;
		this.loopMode = loopMode;
		this.currentTime = 0.0d;
		this.speedModifier = 1.0d;
		this.finished = false;

		nodePositions = new Vector3f[animation.getNodes().length];
		for (int i = 0; i < nodePositions.length; i++) {
			nodePositions[i] = new Vector3f(0.0f, 0.0f, 0.0f);
		}
	}

	/**
	 * Plays the given animation with the default loop mode
	 * 
	 * @param animation
	 */
	public void playAnimation(Animation animation) {
		playAnimation(animation, LoopMode.LM_DONT_LOOP);
	}

	/**
	 * Check whether this animation is finished or not
	 * 
	 * @return
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Get the loop mode of this animation channel
	 * 
	 * @return
	 */
	public LoopMode getLoopMode() {
		return loopMode;
	}

	/**
	 * Set the loop mode of this animation channel
	 * 
	 * @param loopMode
	 */
	public void setLoopMode(LoopMode loopMode) {
		if (loopMode == null) {
			return;
		}
		this.loopMode = loopMode;
	}

	/**
	 * Set the speed of this animation
	 * 
	 * @param speed
	 */
	public void setSpeed(double speed) {
		if (speed < 0) {
			return;
		}
		this.speedModifier = speed;
	}

	/**
	 * Get the current speed this animation is running at
	 * 
	 * @return
	 */
	public double getSpeed() {
		return speedModifier;
	}

	/**
	 * Add time to this animation channel
	 * 
	 * @param time
	 */
	public void addTime(double time) {
		if (animation == null || isFinished()) {
			return;
		}
		if (animation.getDuration() > 0.0) {
			// Calculate the current frame time
			currentTime += (time * animation.getTicksPerSecond())
					* speedModifier;

			if (currentTime > animation.getDuration()) {

				if (loopMode == LoopMode.LM_DONT_LOOP) {
					finished = true;
					return;
				} else if (loopMode == LoopMode.LM_LOOP) {
					currentTime = currentTime % animation.getDuration();
				} else if (loopMode == LoopMode.LM_CYCLE) {
					// NOT YET IMPLEMENTED
					return;
				} else {
					// Shouldn't be possible, so this else clause is not needed
					// at all.
				}
			}
		}
	}

	public Vector4f interpolate(QuatKey key1, QuatKey key2, float pFactor) {

		QuatKey pStart = key1;
		QuatKey pEnd = key2;

		float cosom = pStart.x * pEnd.x + pStart.y * pEnd.y + pStart.z * pEnd.z
				+ pStart.w * pEnd.w;

		QuatKey end = pEnd;
		if (cosom < 0.0f) {
			cosom = -cosom;
			end.x = -end.x; // Reverse all signs
			end.y = -end.y;
			end.z = -end.z;
			end.w = -end.w;
		}

		// Calculate coefficients
		float sclp, sclq;
		if ((1.0f - cosom) > (0.0001f)) // 0.0001 -> some epsillon
		{
			// Standard case (slerp)
			float omega, sinom;
			omega = (float) Math.acos(cosom); // extract theta from dot
												// product's cos theta
			sinom = (float) Math.sin(omega);
			sclp = (float) Math.sin(((1.0f) - pFactor) * omega) / sinom;
			sclq = (float) Math.sin(pFactor * omega) / sinom;
		} else {
			// Very close, do linear interp (because it's faster)
			sclp = (1.0f) - pFactor;
			sclq = pFactor;
		}
		Vector4f pOut = new Vector4f();
		pOut.x = sclp * pStart.x + sclq * end.x;
		pOut.y = sclp * pStart.y + sclq * end.y;
		pOut.z = sclp * pStart.z + sclq * end.z;
		pOut.w = sclp * pStart.w + sclq * end.w;
		return pOut;
	}

	/**
	 * Update this channel
	 */
	public void update() {

		if (animation == null || isFinished()) {
			return;
		}

		for (int i = 0; i < animation.getNodes().length; i++) {

			AnimationNode node = animation.getNodes()[i];

			Vector3f position = new Vector3f();
			Vector4f rotation = new Vector4f();
			Vector3f scale = new Vector3f();

			// Position Keys
			if (node.getNumPositionKeys() > 0) {

				int currFrame = 0;
				int nextFrame = 0;

				while (currFrame < node.getNumPositionKeys() - 1) {
					if (node.getPositionKey(currFrame).time > currentTime) {
						break;
					}
					currFrame++;
				}

				nextFrame = currFrame + 1;

				// If next frame is over the end of the animation reset
				if (nextFrame == node.getNumPositionKeys()) {
					nextFrame = 0;
				}

				// Interpolate
				Vec3Key key1 = node.getPositionKey(currFrame);
				Vec3Key key2 = node.getPositionKey(nextFrame);

				double timeDifference = key2.time - key1.time;

				if (timeDifference < 0) {
					timeDifference += animation.getDuration();
				}
				if (timeDifference > 0) {
					float interpolationFactor = (float) ((currentTime - key1.time) / timeDifference);
					position.x = key1.x + (key2.x - key1.x)
							* interpolationFactor;
					position.y = key1.y + (key2.y - key1.y)
							* interpolationFactor;
					position.z = key1.z + (key2.z - key1.z)
							* interpolationFactor;
				} else {
					position.x = key1.x;
					position.y = key1.y;
					position.z = key1.z;
				}

				nodePositions[i].x = (float) currFrame;
			}

			// Rotation Keys
			if (node.getNumRotationKeys() > 0) {

				int currFrame = 0;
				int nextFrame = 0;

				while (currFrame < node.getNumRotationKeys() - 1) {
					if (node.getRotationKey(currFrame).time > currentTime) {
						break;
					}
					currFrame++;
				}

				nextFrame = (currFrame + 1) % node.getNumRotationKeys();

				QuatKey key1 = node.getRotationKey(currFrame);
				QuatKey key2 = node.getRotationKey(nextFrame);

				double timeDifference = key2.time - key1.time;

				if (timeDifference < 0) {
					timeDifference = animation.getDuration();
				}
				if (timeDifference > 0) {
					float interpolationFactor = (float) ((currentTime - key1.time) / timeDifference);
					rotation = interpolate(key1, key2, interpolationFactor);
				} else {
					rotation.x = key1.x;
					rotation.y = key1.y;
					rotation.z = key1.z;
					rotation.w = key1.w;
				}

				nodePositions[i].y = (float) currFrame;
			}

			// System.err.println("Frame: " + nodePositions[i].x);
			// Ignore scaling

			// Update the bones position
			Bone bone = skeleton.findBone(node.getNodeName());
			if (bone != null) {

				// Rotation
				quatToRotationMatrix(-rotation.x, -rotation.y, -rotation.z,
						rotation.w, bone.localTransform);

				// Translation
				bone.localTransform.m30 = position.x;
				bone.localTransform.m31 = position.y;
				bone.localTransform.m32 = position.z;

			} else {
				System.err.println("Bone " + node.getNodeName()
						+ " could not be found!");
			}
		}

	}

	public void quatToRotationMatrix(float x, float y, float z, float w,
			Matrix4f inMatrix) {
		// compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
		// will be used 2-4 times each.
		float s = 2.0f;
		float xs = x * s;
		float ys = y * s;
		float zs = z * s;
		float xx = x * xs;
		float xy = x * ys;
		float xz = x * zs;
		float xw = w * xs;
		float yy = y * ys;
		float yz = y * zs;
		float yw = w * ys;
		float zz = z * zs;
		float zw = w * zs;

		// using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
		inMatrix.m00 = 1 - (yy + zz);
		inMatrix.m01 = (xy - zw);
		inMatrix.m02 = (xz + yw);
		inMatrix.m10 = (xy + zw);
		inMatrix.m11 = 1 - (xx + zz);
		inMatrix.m12 = (yz - xw);
		inMatrix.m20 = (xz - yw);
		inMatrix.m21 = (yz + xw);
		inMatrix.m22 = 1 - (xx + yy);
	}

}
