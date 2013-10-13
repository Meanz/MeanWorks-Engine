package org.meanworks.engine;

import org.meanworks.render.opengl.shader.ShaderProgram;
import org.meanworks.render.texture.Texture;

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
public class Renderer {

	/*
	 * The currently bound textures
	 */
	private static Texture[] boundTextures = new Texture[EngineConfig.MAX_TEXTURE_UNITS];

	/*
	 * The currently bound shader program
	 */
	private static ShaderProgram boundShaderProgram;

	/**
	 * Bind the given texture to the given texture unit
	 * 
	 * @param textureUnit
	 * @param texture
	 */
	public static void bindTexture(int textureUnit, Texture texture) {
		boundTextures[textureUnit] = texture;
	}

	/**
	 * Bind a shader program
	 * 
	 * @param program
	 */
	public static void bindShaderProgram(ShaderProgram program) {
		boundShaderProgram = program;
	}

	/**
	 * Clear the drawing state
	 */
	public static void clearState() {
		boundShaderProgram = null;
		// Clear all texture units
		for (int i = 0; i < boundTextures.length; i++) {
			boundTextures[i] = null;
		}
	}
}
