package org.meanworks.engine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.meanworks.engine.render.opengl.shader.ShaderProgram;
import org.meanworks.engine.render.texture.Texture;

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
public class RenderState {

	/*
	 * The currently bound textures
	 */
	private static Texture[] boundTextures = new Texture[EngineConfig.MAX_TEXTURE_UNITS];

	/*
	 * The currently bound shader program
	 */
	private static ShaderProgram boundShaderProgram;

	/*
	 * Holds information about how many vertices is being rendered
	 */
	private static int renderedVertices = 0;

	/*
	 * Holds information about how many objects is being rendered
	 */
	private static int renderedObjects = 0;

	/*
	 * The matrices that we are going to use for the next rendering call
	 */
	private static Matrix4f projectionViewMatrix;
	private static Matrix4f transformMatrix;

	/**
	 * Get the projection matrix that will be used the next draw call
	 * 
	 * @return
	 */
	public static Matrix4f getProjectionViewMatrix() {
		return projectionViewMatrix;
	}

	/**
	 * Get the transformation matrix that will be used the next draw call
	 * 
	 * @return
	 */
	public static Matrix4f getTransformMatrix() {
		return transformMatrix;
	}


	/**
	 * Set the projection matrix that will be used the next draw call
	 * 
	 * @param projectionViewMatrix
	 */
	public static void setProjectionViewMatrix(Matrix4f projectionViewMatrix) {
		RenderState.projectionViewMatrix = projectionViewMatrix;
	}

	/**
	 * Set the transform matrix that will be used the next draw call
	 * 
	 * @param transformMatrix
	 */
	public static void setTransformMatrix(Matrix4f transformMatrix) {
		RenderState.transformMatrix = transformMatrix;
	}

	/**
	 * Increment the number of rendered objects
	 */
	public static void addRenderedObject() {
		renderedObjects += 1;
	}

	/**
	 * Get the number of rendered objects
	 * 
	 * @return
	 */
	public static int getRenderedObjects() {
		return renderedObjects;
	}

	/**
	 * Get the number of rendered vertices
	 * 
	 * @return
	 */
	public static int getRenderedVertices() {
		return renderedVertices;
	}

	/**
	 * Clears the number of rendered vertices
	 */
	public static void clearRenderedVertices() {
		renderedVertices = 0;
		renderedObjects = 0;
	}

	/**
	 * Add the given number of vertices to the total amount of rendered vertices
	 * this frame
	 * 
	 * @param amt
	 */
	public static void addRenderedVertices(int amt) {
		renderedVertices += amt;
	}

	/**
	 * Sets the given texture unit as active
	 * 
	 * @param texture
	 */
	public static void activeTexture(int textureUnit) {
		// Ignore for now
	}

	/**
	 * Bind the given texture to the given texture unit
	 * 
	 * @param textureUnit
	 * @param texture
	 */
	public static void bindTexture(int textureUnit, Texture texture) {
		boundTextures[textureUnit] = texture;
	}

	/**
	 * Get's the currently bound shader program
	 * 
	 * @return
	 */
	public static ShaderProgram getBoundShader() {
		return boundShaderProgram;
	}

	/**
	 * Bind a shader program
	 * 
	 * @param program
	 */
	public static void setBoundShader(ShaderProgram program) {
		boundShaderProgram = program;
	}

	/**
	 * Clear the drawing state
	 */
	public static void clearState() {
		if (getBoundShader() != null) {
			ShaderProgram.bindNone();
			setBoundShader(null);
		}
		// Clear all texture units
		for (int i = 0; i < boundTextures.length; i++) {
			boundTextures[i] = null;
		}
		// Update parameters
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		
	}
}
