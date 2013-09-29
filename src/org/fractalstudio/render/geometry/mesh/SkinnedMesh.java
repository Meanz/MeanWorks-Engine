package org.fractalstudio.render.geometry.mesh;

import java.util.LinkedList;

import org.fractalstudio.render.geometry.animation.Animation;
import org.fractalstudio.render.geometry.animation.Skeleton;
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
public class SkinnedMesh extends Mesh {

	/*
	 * The list of available animations for this skinned mesh
	 */
	private LinkedList<Animation> availableAnimations = new LinkedList<Animation>();

	/*
	 * The bind shape matrix for this mesh
	 */
	private Matrix4f bindShapeMatrix;

	/*
	 * The bind shape transformations
	 */
	private Matrix4f bindShapes[];

	/*
	 * The skeleton for this skinned mesh
	 */
	private Skeleton skeleton;

	/**
	 * Construct a new mesh
	 */
	public SkinnedMesh() {

	}

	/**
	 * Get the skeleton of this skinned mesh
	 * 
	 * @return
	 */
	public Skeleton getSkeleton() {
		return skeleton;
	}

	/**
	 * Set the skeleton of this skinned mesh
	 * 
	 * @param skeleton
	 */
	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.render.geometry.mesh.Mesh#render()
	 */
	@Override
	public void render() {

	}

}
