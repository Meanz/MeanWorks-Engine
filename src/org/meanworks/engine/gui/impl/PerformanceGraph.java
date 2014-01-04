package org.meanworks.engine.gui.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.meanworks.engine.gui.Component;

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
public class PerformanceGraph extends Component {

	private static class PerformanceGraphEntry {

		float red = 0.0f;
		float green = 0.0f;
		float blue = 0.0f;

		float divisor = 1f;
		int highestVal = 0;
		LinkedList<Integer> values = new LinkedList<>();

		public void feed(int hv) {
			if (values.size() == 50) {
				values.pop();
				values.add(hv);
			} else {
				values.add(hv);
			}

			float range = 100;
			int maxVal = 0;
			for (Integer _hv : values) {
				if (_hv > maxVal) {
					maxVal = _hv;
				}
			}
			highestVal = maxVal;
			divisor = ((float) maxVal) / range;
		}

		public void render() {
			int x = 10;
			int lastY = 0;
			int idx = 0;
			GL11.glColor3f(red, green, blue);
			for (Integer hv : values) {
				float pointValue = (float) hv / divisor;
				line(x, 640 - lastY, x + 10, 640 - (int) pointValue);
				x += 10;
				lastY = (int) pointValue;
				idx++;
				if (idx > 50) {
					break;
				}
			}
		}
	}

	public static Object syncObject = new Object();
	private static HashMap<Integer, PerformanceGraphEntry> graphs = new HashMap<>();

	public static void tick(int idx, int hv) {
		PerformanceGraphEntry pge = graphs.get(idx);
		if (pge != null) {
			pge.feed(hv);
		} else {
			pge = new PerformanceGraphEntry();
			pge.feed(hv);
			Random random = new Random(idx + 1500);
			pge.red = random.nextFloat();
			pge.blue = random.nextFloat();
			pge.green = random.nextFloat();
			graphs.put(idx, pge);
		}
	}

	public PerformanceGraph() {
		super("PerformanceGraph", 10, 400, 200, 240);
	}

	public void render() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		{
			for (PerformanceGraphEntry pge : graphs.values()) {
				pge.render();
			}
		}
		GL11.glEnd();
	}

	public static void line(int x1, int y1, int x2, int y2) {
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y2);
	}

}
