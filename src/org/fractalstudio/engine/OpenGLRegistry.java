package org.fractalstudio.engine;

import java.util.LinkedList;

import org.fractalstudio.render.opengl.VertexBuffer;
import org.fractalstudio.render.texture.Texture;

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
public class OpenGLRegistry {

	/*
	 * The registry singleton
	 */
	private static OpenGLRegistry singleton;

	/**
	 * Get the registry singleton
	 * 
	 * @return
	 */
	private static OpenGLRegistry getRegistry() {
		if (singleton == null) {
			singleton = new OpenGLRegistry();
		}
		return singleton;
	}

	/*
	 * The list of vertex buffers created
	 */
	private LinkedList<VertexBuffer> vertexBuffers = new LinkedList<VertexBuffer>();

	/*
	 * The list of textures created
	 */
	private LinkedList<Texture> textures = new LinkedList<Texture>();

	/**
	 * Register a vertex buffers
	 * 
	 * @param vb
	 */
	public void registerVertexBuffer(VertexBuffer vb) {
		vertexBuffers.add(vb);
	}

	/**
	 * Register a texture
	 * 
	 * @param texture
	 */
	public void registerTexture(Texture texture) {
		textures.add(texture);
	}

}
