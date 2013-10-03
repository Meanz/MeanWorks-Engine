package org.meanworks.testgame.world;

import org.meanworks.engine.model.MWMLoader;
import org.meanworks.engine.scene.Node;
import org.meanworks.render.geometry.AnimatedModel;
import org.meanworks.render.geometry.animation.AnimationChannel;
import org.meanworks.render.geometry.animation.LoopMode;

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

	}

	/**
	 * Render the player
	 */
	public void render() {
		// Nothing to render :D
	}

}
