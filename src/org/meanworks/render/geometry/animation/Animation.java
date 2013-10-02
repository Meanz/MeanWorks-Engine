package org.meanworks.render.geometry.animation;

import java.util.LinkedList;

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
public class Animation {

	/*
	 * The name of this animation
	 */
	private String name;

	/*
	 * The duration of this animation
	 */
	private double duration;

	/*
	 * How many ticks per second this animation needs
	 */
	private double ticksPerSecond;

	/*
	 * The list of animation nodes
	 */
	private AnimationNode[] nodes;

	/**
	 * Construct a new animation
	 * 
	 * @param name
	 */
	public Animation(String name, int numAnimationNodes, double duration,
			double ticksPerSecond) {
		this.name = name;
		this.nodes = new AnimationNode[numAnimationNodes];
		this.duration = duration;
		this.ticksPerSecond = ticksPerSecond;
	}

	/**
	 * Get the name of this animation
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the number of this per second for this animation
	 * 
	 * @return
	 */
	public double getTicksPerSecond() {
		return ticksPerSecond;
	}

	/**
	 * Get the duration of this animation
	 * 
	 * @return
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * Get the nodes of this animation
	 * 
	 * @return
	 */
	public AnimationNode[] getNodes() {
		return nodes;
	}

	/**
	 * Add an animation node to this animation
	 * 
	 * @param node
	 */
	public void setAnimationNode(int index, AnimationNode node) {
		nodes[index] = node;
	}
}
