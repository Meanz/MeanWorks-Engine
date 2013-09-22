package org.fractalstudio.testgame.world;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Player {

	/**
	 * The position of the player
	 */
	private Vector3f position;

	/**
	 * 
	 */
	public Player() {
		//position = new Vector3f(4794.0f, 1.8f, 4956.0f);
		position = new Vector3f();
	}

	/**
	 * Get the position of the player
	 * 
	 * @return
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * Set the position of the player
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	/**
	 * Translate the player position
	 * @param x
	 * @param y
	 * @param z
	 */
	public void translate(float x, float y, float z) {
		position.x += x;
		position.y += y;
		position.z += z;
	}

	/**
	 * Update the player
	 */
	public void update() {

	}

	/**
	 * Render the player
	 */
	public void render() {
		// Nothing to render :D
	}

}
