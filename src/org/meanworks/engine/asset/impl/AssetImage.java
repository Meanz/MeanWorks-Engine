package org.meanworks.engine.asset.impl;

import org.meanworks.engine.asset.Asset;
import org.meanworks.engine.render.texture.Texture;

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
public class AssetImage extends Asset {

	/*
	 * The texture this asset is holding
	 */
	private Texture texture;

	/**
	 * Construct a new AssetImage
	 * 
	 * @param assetName
	 */
	public AssetImage(String assetName) {
		super(assetName);
	}

	/**
	 * Get the texture this asset is holding
	 * 
	 * @return
	 */
	public Texture getTexture() {
		return texture;
	}

}
