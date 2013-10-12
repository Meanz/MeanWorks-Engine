package org.meanworks.engine.camera;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
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
public class ThirdPersonCamera extends Camera {

	public ThirdPersonCamera(int windowWidth, int windowHeight, float fovY,
			float aspect) {
		super(windowWidth, windowHeight, fovY, aspect);
		// TODO Auto-generated constructor stub
	}

	public void update() {
		//
		if (Mouse.isButtonDown(1)) { // RMB
			float mouseRatio = 0.2f;

			float yincr = Application.getApplication().getInputHandler()
					.getDX()
					* mouseRatio;
			float pincr = -Application.getApplication().getInputHandler()
					.getDY()
					* mouseRatio;
			yaw(yincr);
			// pitch(pincr);
		}

		// Set our position to the players position
		this.setPosition(getFollowTarget().getTransform().getPosition());

		// The distance from the camera
		float distance = 10;

		// Find the yaw of our camera
		float yaw = (float) Math.toRadians(getYaw());
		float pitch = (float) Math.toRadians(getPitch());

		float factor = (float) Math.cos(Math.toRadians(getPitch()));
		Vector3f forward = new Vector3f();
		forward.x = (float) Math.sin(Math.toRadians(getYaw())) * factor;
		forward.y = (float) Math.sin(Math.toRadians(-getPitch()));
		forward.z = (float) -Math.cos(Math.toRadians(getYaw())) * factor;

		translate((float) forward.x * distance, (float) forward.y * distance,
				(float) forward.z * distance);
	}

}
