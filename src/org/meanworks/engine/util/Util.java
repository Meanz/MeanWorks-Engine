package org.meanworks.engine.util;

import java.io.File;

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
public class Util {

	public static void loadLWJGL() {
		try {
			String osName = System.getProperty("os.name").toLowerCase();
			boolean isMacOs = osName.startsWith("mac os x");
			if (isMacOs) {
				System.setProperty("java.library.path",
						System.getProperty("java.library.path") + ";"
								+ new File("native/macosx").getAbsolutePath());
				System.setProperty("org.lwjgl.librarypath", new File(
						"native/macosx").getAbsolutePath());
			} else {
				System.setProperty("java.library.path",
						System.getProperty("java.library.path")
								+ ";"
								+ new File("native/windows/").getAbsolutePath()
										.replaceAll("\\/", "\\"));
				System.setProperty("org.lwjgl.librarypath",
						new File("native/windows/").getAbsolutePath()
								.replaceAll("\\/", "\\"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
