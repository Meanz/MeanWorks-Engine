package org.meanworks.testgame.entity;

import org.meanworks.engine.render.geometry.Model;

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
public class NonPlayerCharacter extends Character {

	/**
	 * Construct a new NPC
	 */
	public NonPlayerCharacter() {

	}

	/**
	 * Set the model of this NPC
	 * 
	 * @param geometry
	 */
	public void setModel(Model geometry) {
		this.setGeometry(geometry);
	}

}
