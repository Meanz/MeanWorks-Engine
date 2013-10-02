package org.meanworks.engine.gui.impl;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.meanworks.engine.gui.Component;
import org.meanworks.engine.gui.FontRenderer;

public class PerformanceGraph extends Component {

	public static Object syncObject = new Object();

	private static float divisor = 1f;
	private static LinkedList<Integer> heightValues = new LinkedList<>();
	private static int highestVal1 = 0;

	private static float divisor2 = 1f;
	private static LinkedList<Integer> heightValues2 = new LinkedList<>();
	private static int highestVal2 = 0;

	public static void feedTick(int hv) {
		if (heightValues.size() == 50) {
			heightValues.pop();
			heightValues.add(hv);
		} else {
			heightValues.add(hv);
		}

		float range = 100;
		int maxVal = 0;
		for (Integer _hv : heightValues) {
			if (_hv > maxVal) {
				maxVal = _hv;
			}
		}
		highestVal1 = maxVal;
		divisor = ((float) maxVal) / range;
	}

	public static void feedTick2(int hv) {
		hv = hv / 1000;
		if (heightValues2.size() == 50) {
			heightValues2.pop();
			heightValues2.add(hv);
		} else {
			heightValues2.add(hv);
		}

		float range = 100;
		int maxVal = 0;
		for (Integer _hv : heightValues2) {
			if (_hv > maxVal) {
				maxVal = _hv;
			}
		}
		highestVal2 = maxVal;
		divisor2 = ((float) maxVal) / range;
	}

	public PerformanceGraph() {
		super("PerformanceGraph", 10, 400, 200, 240);
	}

	public void render() {

		if (heightValues.size() > 50) {
			heightValues.clear();
		}

		FontRenderer.arial14.drawString("Highestval 1: " + highestVal1, 10, 410);
		FontRenderer.arial14.drawString("Highestval 2: " + highestVal2, 10, 425);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		{
			GL11.glColor3f(0.0f, 1.0f, 0.0f);
			int x = 10;
			int lastY = 0;
			int idx = 0;
			for (Integer hv : heightValues) {
				float pointValue = (float) hv / divisor;
				line(x, 640 - lastY, x + 10, 640 - (int) pointValue);
				x += 10;
				lastY = (int) pointValue;
				idx++;
				if (idx > 50) {
					break;
				}
			}

			GL11.glColor3f(1.0f, 0.0f, 0.0f);
			x = 10;
			lastY = 0;
			idx = 0;
			for (Integer hv : heightValues2) {
				float pointValue = (float) hv / divisor2;
				line(x, 640 - lastY, x + 10, 640 - (int) pointValue);
				x += 10;
				lastY = (int) pointValue;
				idx++;
				if (idx > 50) {
					break;
				}
			}
		}
		GL11.glEnd();
	}

	public void line(int x1, int y1, int x2, int y2) {
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y2);
	}

}
