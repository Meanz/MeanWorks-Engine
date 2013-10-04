package org.meanworks.testgame.world;

import org.meanworks.engine.model.MWMLoader;
import org.meanworks.engine.scene.Node;
import org.meanworks.render.geometry.AnimatedModel;
import org.meanworks.render.geometry.animation.AnimationChannel;
import org.meanworks.render.geometry.animation.LoopMode;
import org.meanworks.testgame.TestGame;

public class Player extends Node {

	/**
	 * 
	 */
	private AnimatedModel playerModel;

	/**
	 * 
	 */
	public Player() {
		// Let's try to load a model
		playerModel = MWMLoader
				.loadAnimatedModel("./data/models/Sinbad/sinbad_mesh.mwm");

		playerModel.createChannel(playerModel.getAnimation("SliceVertical"));
		AnimationChannel ac = playerModel.createChannel(playerModel
				.getAnimation("RunBase"));
		ac.setLoopMode(LoopMode.LM_LOOP);
		// geometry.createChannel(0);
		playerModel.getTransform().setPosition(0.5f, 0.9f, 0.5f);
		playerModel.getTransform().setScale(0.2f, 0.2f, 0.2f);

		addChild(playerModel);
	}

	/**
	 * Update the player
	 */
	public void update() {		
		
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
