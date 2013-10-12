package org.meanworks.engine.camera;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.meanworks.engine.core.Application;

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
public class FirstPersonCamera extends Camera {

	public FirstPersonCamera(int windowWidth, int windowHeight, float fovY,
			float aspect) {
		super(windowWidth, windowHeight, fovY, aspect);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Update this camera
	 */
	public void update() {
		float moveSpeed = 0.2f;
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Application.getApplication().stop();
		}

		if (getFollowTarget() != null) {

			if (Mouse.isButtonDown(1)) { // RMB
				float mouseRatio = 0.2f;

				float yincr = Application.getApplication().getInputHandler()
						.getDX()
						* mouseRatio;
				float pincr = Application.getApplication().getInputHandler()
						.getDY()
						* mouseRatio;
				yaw(yincr);
				pitch(pincr);
			}

			// Set our position to the players position
			this.setPosition(getFollowTarget().getTransform().getPosition());

			this.translate(0.0f, 2.5f, 0.0f);

		} else if (isFlying()) {
			if (Mouse.isButtonDown(1)) { // RMB
				float mouseRatio = 0.2f;

				float yincr = Application.getApplication().getInputHandler()
						.getDX()
						* mouseRatio;
				float pincr = -Application.getApplication().getInputHandler()
						.getDY()
						* mouseRatio;
				yaw(yincr);
				pitch(pincr);
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				moveSpeed = 1.0f;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				translate(
						moveSpeed * (float) Math.sin(Math.toRadians(getYaw())),
						0.0f,
						-moveSpeed * (float) Math.cos(Math.toRadians(getYaw())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				translate(
						moveSpeed
								* (float) Math.sin(Math
										.toRadians(getYaw() + 90)),
						0.0f,
						-moveSpeed
								* (float) Math.cos(Math
										.toRadians(getYaw() + 90)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				translate(
						moveSpeed
								* (float) Math.sin(Math
										.toRadians(getYaw() - 90)),
						0.0f,
						-moveSpeed
								* (float) Math.cos(Math
										.toRadians(getYaw() - 90)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				translate(
						-moveSpeed * (float) Math.sin(Math.toRadians(getYaw())),
						0.0f,
						moveSpeed * (float) Math.cos(Math.toRadians(getYaw())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				translate(0.0f, -0.5f * moveSpeed, 0.0f);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				translate(0.0f, 0.5f * moveSpeed, 0.0f);
			}
		} else {
		}
	}
}
