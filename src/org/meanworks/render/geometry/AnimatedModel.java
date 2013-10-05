package org.meanworks.render.geometry;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.Application;
import org.meanworks.engine.EngineLogger;
import org.meanworks.render.geometry.animation.Animation;
import org.meanworks.render.geometry.animation.AnimationChannel;
import org.meanworks.render.geometry.animation.Skeleton;
import org.meanworks.render.material.Material;

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
public class AnimatedModel extends Model {

	/*
	 * The animation channels active on this model
	 */
	private LinkedList<AnimationChannel> channels = new LinkedList<>();

	/*
	 * The list of available animations for this skinned mesh
	 */
	private LinkedList<Animation> availableAnimations = new LinkedList<Animation>();

	/*
	 * The bind shape matrix for this mesh
	 */
	private Matrix4f bindShapeMatrix;

	/*
	 * The bind shape matrices of this animated model
	 */
	private Matrix4f[] bindShapeMatrices;

	/*
	 * 
	 */
	private Matrix4f[] transformMatrices;

	/*
	 * The skinning matrices of this animated model
	 */
	private Matrix4f[] skinningMatrices;

	/*
	 * The skeleton for this model
	 */
	private Skeleton skeleton;

	/*
	 * Whether to render the skeleton or not
	 */
	private boolean renderSkeleton;

	/*
	 * 
	 */
	Matrix4f identityMatrix = new Matrix4f();

	/**
	 * Construct a new AnimatedModel
	 */
	public AnimatedModel() {
		renderSkeleton = false;

		setMaterial(new Material("animatedModelMaterial", Application
				.getApplication().getAssetManager()
				.loadShader("./data/shaders/simple_skinning")));

	}

	/**
	 * Stops all animations
	 */
	public void clearChannels() {
		channels.clear();
	}

	/**
	 * Get the active animation channels
	 * 
	 * @return
	 */
	public List<AnimationChannel> getActiveChannels() {
		return channels;
	}

	/**
	 * Get the animation names
	 * 
	 * @return
	 */
	public String[] getAnimationNames() {
		String[] names = new String[availableAnimations.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = availableAnimations.get(i).getName();
		}
		return names;
	}

	/**
	 * Get the animation of with this name
	 * 
	 * @param animationName
	 * @return
	 */
	public Animation getAnimation(String animationName) {
		for (Animation animation : availableAnimations) {
			if (animation.getName().equals(animationName)) {
				return animation;
			}
		}
		EngineLogger.error("Could not find animation with name "
				+ animationName);
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public AnimationChannel createChannel(int animationIndex) {
		AnimationChannel channel = new AnimationChannel(skeleton,
				availableAnimations.get(animationIndex));
		channels.add(channel);
		return channel;
	}

	/**
	 * 
	 * @return
	 */
	public AnimationChannel createChannel(Animation animation) {
		AnimationChannel channel = new AnimationChannel(skeleton, animation);
		channels.add(channel);
		return channel;
	}

	/**
	 * Calculate the skinning matrices
	 */
	public void calculateSkinningMatrices() {

		if (transformMatrices == null || skinningMatrices == null) {
			return;
		}

		for (int i = 0; i < skeleton.getBones().length; i++) {
			transformMatrices[i] = skeleton.getBones()[i]
					.calculateGlobalTransform();
		}

		Matrix4f globalInvTransform = Matrix4f.invert(transformMatrices[0],
				null);

		//
		for (int i = 0; i < skinningMatrices.length; i++) {
			Matrix4f temp = Matrix4f.mul(globalInvTransform,
					transformMatrices[i], null);
			Matrix4f.mul(temp, skeleton.getBones()[i].getOffsetMatrix(),
					skinningMatrices[i]);
		}

	}

	/**
	 * Set the skeleton of this animated model
	 * 
	 * @return
	 */
	public Skeleton getSkeleton() {
		return skeleton;
	}

	/**
	 * Set the skeleton of this animated model
	 * 
	 * @param skeleton
	 */
	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;

		skinningMatrices = new Matrix4f[skeleton.getBones().length];
		transformMatrices = new Matrix4f[skeleton.getBones().length];

		for (int i = 0; i < skinningMatrices.length; i++) {
			skinningMatrices[i] = new Matrix4f();
		}
	}

	/**
	 * Get the available animations for this animated model
	 * 
	 * @return
	 */
	public LinkedList<Animation> getAvailableAnimations() {
		return availableAnimations;
	}

	/**
	 * Set the available animations for this animated model
	 * 
	 * @param availableAnimations
	 */
	public void setAvailableAnimations(LinkedList<Animation> availableAnimations) {
		this.availableAnimations = availableAnimations;
	}

	/**
	 * Set the bind shape matrices for this animated model
	 * 
	 * @param bindShapeMatrices
	 */
	public void setBindShapeMatrices(Matrix4f[] bindShapeMatrices) {
		this.bindShapeMatrices = bindShapeMatrices;
	}

	/**
	 * Render this skinned model
	 */
	@Override
	public void render() {

		if (skeleton == null) {

			// Don't render

			return;
		}

		/*
		 * Update animation
		 */
		for (AnimationChannel channel : channels) {
			if (channel.isFinished()) {
				// Remove this channel
				continue;
			}
			channel.setSpeed(1.0d);
			channel.addTime(Application.getApplication().getFrameTime());
			channel.update();
		}

		/*
		 * Update skinning matrices
		 */
		calculateSkinningMatrices();

		/*
		 * Update the material
		 */
		getMaterial().setProperty("vAmbientColor",
				new Vector4f(0.5f, 0.5f, 0.5f, 1.0f));
		getMaterial().setProperty("vDiffuseColor",
				new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		getMaterial().setProperty("fSpecularIntensity", 30.0f);
		getMaterial().setProperty("tColorMap", 0);
		getMaterial().setProperty("mBones", skinningMatrices);

		/*
		 * Send render requests to the meshes
		 */
		super.render();

		/*
		 * Render skeleton for debuging purposes
		 */
		if (renderSkeleton) {
			GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			glBegin(GL_LINES);
			{

				for (int i = 0; i < skeleton.getBones().length; i++) {
					Matrix4f boneMatrix = skeleton.getBones()[i]
							.getGlobalTransform();
					Matrix4f parentMatrix = skeleton.getBones()[i].getParent() != null ? skeleton
							.getBones()[i].getParent().getGlobalTransform()
							: new Matrix4f();

					Vector3f lastPosition = new Vector3f(parentMatrix.m30,
							parentMatrix.m31, parentMatrix.m32);
					Vector3f newPosition = new Vector3f(boneMatrix.m30,
							boneMatrix.m31, boneMatrix.m32);

					glVertex3f(lastPosition.x, lastPosition.y, lastPosition.z);
					glVertex3f(newPosition.x, newPosition.y, newPosition.z);

				}

			}
			glEnd();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

}
