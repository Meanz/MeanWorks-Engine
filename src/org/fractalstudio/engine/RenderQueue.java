package org.fractalstudio.engine;

import java.util.LinkedList;

import org.lwjgl.util.Renderable;

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
public class RenderQueue {

	/*
	 * The render queue
	 */
	private LinkedList<Renderable> queue = new LinkedList<>();

	/**
	 * Construct a new Render Queue
	 */
	public RenderQueue() {

	}

	/**
	 * Add the given renderable to the render queue
	 * 
	 * @param renderable
	 */
	public void addToQueue(Renderable renderable) {
		queue.add(renderable);
	}

	/**
	 * Simple yet efficient
	 */
	public void doRender() {
		for (Renderable renderable : queue) {
			renderable.render();
		}
	}
}
