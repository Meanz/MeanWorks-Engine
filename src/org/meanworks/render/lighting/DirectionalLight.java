package org.meanworks.render.lighting;

import org.lwjgl.util.vector.Vector3f;

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
public class DirectionalLight {

	/*
	 * The direction of this light
	 */
	private Vector3f direction;

	/*
	 * The color of this light
	 */
	private Vector3f lightColor;

	/**
	 * Construct a new directional light
	 * 
	 * @param direction
	 */
	public DirectionalLight(Vector3f direction, Vector3f lightColor) {
		this.direction = direction;
		this.lightColor = lightColor;
	}

	/**
	 * Construct a new directional light
	 */
	public DirectionalLight() {
		this(new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(0.5f, 0.5f, 0.5f));
	}

}
