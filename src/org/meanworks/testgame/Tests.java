package org.meanworks.testgame;

import org.meanworks.engine.math.Transform;
import org.meanworks.engine.model.MWMLoader;
import org.meanworks.render.geometry.AnimatedModel;
import org.meanworks.render.geometry.animation.AnimationChannel;
import org.meanworks.render.geometry.animation.LoopMode;

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
public class Tests {

	public static void doTests(TestGame tg) {
		doModelTest(tg);
	}

	private static void doModelTest(TestGame tg) {
		AnimatedModel model = MWMLoader
				.loadAnimatedModel("./data/models/Sinbad/Sinbad_mesh.mwm");

		/*
		 * Play some animations
		 */
		final AnimationChannel channel1 = model.createChannel();
		final AnimationChannel channel2 = model.createChannel();

		channel1.playAnimation(model.getAnimation("RunBase"), LoopMode.LM_LOOP);
		channel2.playAnimation(model.getAnimation("RunTop"), LoopMode.LM_LOOP);

		/*
		 * Set the position of the model
		 */
		final Transform modelTransform = model.getTransform();
		modelTransform.setScale(0.2f, 0.2f, 0.2f);
		modelTransform.setPosition(4998f, 138f, 5024f);

		/*
		 * Set that we want to debug the skeleton
		 */
		model.setRenderSkeleton(false);

		/*
		 * Add the model to the scene
		 */
		tg.getScene().getRootNode().addChild(model);

		/*
		 * Copy this model and play a different animation
		 */
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				AnimatedModel model2 = model.shallowCopy();

				final Transform model2Transform = model2.getTransform();
				model2Transform.setScale(0.2f, 0.2f, 0.2f);
				model2Transform.setPosition(4992f + (x * 3), 138f,
						5024f + (y * 3));

				final AnimationChannel channel3 = model2.createChannel();
				channel3.playAnimation(model2.getAnimation("Dance"),
						LoopMode.LM_LOOP);


				tg.getScene().getRootNode().addChild(model2);
			}
		}
	}

}
