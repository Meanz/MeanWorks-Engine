package org.meanworks.engine.fx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.meanworks.engine.render.material.Material;

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
public class MaterialParser {

	/**
	 * Reads a file
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static LinkedList<String> readFile(InputStream is)
			throws IOException {
		LinkedList<String> lines = new LinkedList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = "";
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}

	/**
	 * Parses a material
	 * 
	 * @param materialPath
	 * @return
	 */
	public static Material parseMaterial(String materialPath) {

		return null;
	}
}
