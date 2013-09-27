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
	
	private int boneId;
	
	private String boneName;

	public Matrix4f jointMatrix;
	
	public Matrix4f transform;
	
	public Matrix4f offsetMatrix;
	
	public Bone() {
		
	}
	
	public int getBoneId() {
		return boneId;
	}
	
	public Matrix4f getJointMatrix() {
		return jointMatrix;
	}
	
	public Matrix4f getTransform() {
		return transform;
	}
}
