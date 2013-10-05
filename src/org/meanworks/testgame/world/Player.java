package org.meanworks.testgame.world;

import org.lwjgl.util.vector.Vector2f;
import org.meanworks.engine.Application;
import org.meanworks.engine.model.MWMLoader;
import org.meanworks.engine.scene.Node;
import org.meanworks.render.geometry.AnimatedModel;
import org.meanworks.render.geometry.animation.AnimationChannel;
import org.meanworks.render.geometry.animation.LoopMode;
import org.meanworks.testgame.TestGame;

public class Player extends Node {

	/*
	 * The player model
	 */
	private AnimatedModel playerModel;

	/*
	 * Our move to target
	 */
	private Vector2f moveTarget;

	/**
	 * 
	 */
	public Player() {
		// Let's try to load a model
		playerModel = MWMLoader
				.loadAnimatedModel("./data/models/Sinbad/sinbad_mesh.mwm");

		AnimationChannel ac = playerModel.createChannel(playerModel
				.getAnimation("IdleBase"));
		ac.setLoopMode(LoopMode.LM_LOOP);

		ac = playerModel.createChannel(playerModel.getAnimation("IdleTop"));
		ac.setLoopMode(LoopMode.LM_LOOP);

		// geometry.createChannel(0);
		playerModel.getTransform().setPosition(0.5f, 0.9f, 0.5f);
		playerModel.getTransform().setScale(0.2f, 0.2f, 0.2f);

		addChild(playerModel);
	}

	/**
	 * Move towards the given position
	 * 
	 * @param x
	 * @param y
	 */
	public void moveTowards(float x, float y) {
		playerModel.clearChannels();
		AnimationChannel ac = playerModel.createChannel(playerModel
				.getAnimation("RunBase"));
		ac.setLoopMode(LoopMode.LM_LOOP);

		moveTarget = new Vector2f(x, y);
	}

	float temp = 0.0f;
	
	/**
	 * Update the player
	 */
	public void update() {

		temp += 0.01f;
		
		playerModel.getTransform().identity();
		playerModel.getTransform().rotate(0.0f, (float)Math.sin(temp), 0.0f);
		
		if (moveTarget != null) {

			Vector2f direction = Vector2f.sub(moveTarget, new Vector2f(
					getTransform().getPosition().x, getTransform()
							.getPosition().y), null);

			direction.normalise();
			
			System.err.println("Direction: " + direction.toString());

			float moveSpeed = 0.5f;

			float x = (float)Math.asin(direction.x) * moveSpeed;
			float y = (float)Math.acos(direction.y) * moveSpeed;
			
			getTransform().translate(x, 0.0f, y);

		}

		getTransform().setPosition(
				getTransform().getPosition().x,
				TestGame.getWorld().getInterpolatedHeight(
						getTransform().getPosition().x,
						getTransform().getPosition().z),
				getTransform().getPosition().z);
		playerModel.getTransform().setPosition(
				getTransform().getPosition().x + 0.5f,
				getTransform().getPosition().y + 0.7f,
				getTransform().getPosition().z + 0.5f);
	}

	/**
	 * Render the player
	 */
	public void render() {
		// Nothing to render :D
	}

}
