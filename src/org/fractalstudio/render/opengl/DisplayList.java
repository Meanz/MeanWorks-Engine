package org.fractalstudio.render.opengl;

import org.fractalstudio.engine.EngineLogger;
import org.lwjgl.opengl.GL11;

public class DisplayList {

	/*
	 * The id of the display list
	 */
	private int listId;

	/**
	 * 
	 */
	public DisplayList() {

	}

	/**
	 * Create a display list
	 * 
	 * @return Whether the operation as successful or not
	 */
	public boolean create() {
		listId = GL11.glGenLists(1);
		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			EngineLogger.error("Could not create display list");
			return false;
		}
		return true;
	}

	/**
	 * Delete this display list
	 * 
	 * @return Whether the operation as successful or not
	 */
	public boolean delete() {
		GL11.glDeleteLists(listId, 1);
		int errorCode = GL11.glGetError();
		if (errorCode != 0) {
			EngineLogger.error("Could not delete display list(" + listId + ")");
			return false;
		}
		return true;
	}

	public void newList() {
		GL11.glNewList(listId, GL11.GL_COMPILE);
	}

	public void endList() {
		GL11.glEndList();
	}

	public void callList() {
		GL11.glCallList(listId);
	}

	/**
	 * 
	 */
	public void bindList() {

	}

	/**
	 * 
	 */
	public void compileList() {

	}

}
