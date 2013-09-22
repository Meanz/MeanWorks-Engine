package org.fractalstudio.render.opengl;

import org.lwjgl.util.vector.Matrix4f;

public class Renderer {

	/*
	 * MVP
	 */
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f modelMatrix = new Matrix4f();
	
	/**
	 * 
	 */
	public Renderer() {
		
	}
	
}
