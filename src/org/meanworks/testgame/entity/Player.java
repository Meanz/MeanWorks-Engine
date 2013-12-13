package org.meanworks.testgame.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.model.MWMLoader;
import org.meanworks.engine.scene.Node;
import org.meanworks.engine.scene.SceneGraph;
import org.meanworks.render.geometry.AnimatedModel;
import org.meanworks.render.geometry.animation.AnimationChannel;
import org.meanworks.render.geometry.animation.LoopMode;
import org.meanworks.testgame.TestGame;

public class Player extends Node {

	/*
	 * Our move to target
	 */
	private Vector2f moveTarget;

	/**
	 * 
	 */
	public Player() {
		super("playerNode_" + SceneGraph.getNextNodeId());
	}

	/**
	 * Move the player
	 * 
	 * @param yawAngleDegrees
	 * @param units
	 */
	public void move(float yawAngleDegrees, float units) {
		getTransform().translate(
				units * (float) Math.sin(Math.toRadians(yawAngleDegrees)),
				0.0f,
				-units * (float) Math.cos(Math.toRadians(yawAngleDegrees)));
	}

	/**
	 * Update the player
	 */
	public void update() {
		this.getTransform().setRotation(0.0f,
				Application.getApplication().getCamera().getYaw(), 0.0f);
		/*
		 * if (moveTarget != null) { float moveSpeed = 0.05f; float deltaX =
		 * moveTarget.x - getTransform().getPosition().x; float deltaZ =
		 * moveTarget.y - getTransform().getPosition().z; float angle = (float)
		 * Math.atan2(deltaZ, deltaX);
		 * 
		 * float x = (float) Math.cos(angle) * moveSpeed; float y = (float)
		 * Math.sin(angle) * moveSpeed;
		 * 
		 * float distance = (float) Math.sqrt((deltaX * deltaX) + (deltaZ *
		 * deltaZ)); if (distance < 0.1f) { moveTarget = null; playIdle(); } //
		 * Move towards the moving target getTransform().translate(x, 0.0f, y);
		 * 
		 * // Rotate towards the moving target
		 * playerModel.getTransform().setRotation(0.0f, 90f + (float)
		 * Math.toDegrees(-angle), 0.0f);
		 * 
		 * }
		 */

		float moveSpeed = 0.1f;
		boolean didMove = false;

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			move(getTransform().getYaw() + 180.0f, moveSpeed);
			didMove = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			move(getTransform().getYaw() - 90.0f, didMove ? moveSpeed / 2
					: moveSpeed);
			didMove = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			move(getTransform().getYaw() + 90.0f, didMove ? moveSpeed / 2
					: moveSpeed);
			didMove = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			move(getTransform().getYaw(), moveSpeed);
			didMove = true;
		}
		getTransform().getPosition().y = TestGame.getWorld()
				.getInterpolatedHeight(getTransform().getPosition().x,
						getTransform().getPosition().z);
	}

	/**
	 * Render the player
	 */
	public void render() {
		// Nothing to render :D
	}

}
