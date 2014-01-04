package org.meanworks.engine.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.render.FontRenderer;
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
public class Surface {

	public void paintImage(Texture image, int x, int y, int width, int height) {
		if (image == null)
			return;
		image.bind();
		glBegin(GL_QUADS);
		{
			drawTexturedQuad(x, y, width, height);
		}
		glEnd();
	}

	/**
	 * Paint a tooltip at the given location
	 * 
	 * @param tooltip
	 * @param x
	 * @param y
	 */
	public void paintTooltip(String tooltip, int x, int y) {

		/*
		 * calculate lines required
		 */
		int height = 35;
		int tooltipWidth = FontRenderer.arial14_white.getStringWidth(tooltip);
		int width = tooltipWidth;
		List<String> lines = new LinkedList<String>();
		int maxWidth = 200;
		if (tooltipWidth > maxWidth) {

			// Work on the lines
			// Count the words and calculate lengths
			// Split text into words
			String[] words = tooltip.split(" ");
			if (words.length < 2) {
				// Everything is one big word, split it up
				// Let's say that each line get's 25 characters
				int maxLen = 0;
				int left = tooltip.length();
				int offset = 0;
				while (left > 0) {
					lines.add(tooltip.substring(offset, offset
							+ (left > 40 ? 40 : left)));
					int lineLen = FontRenderer.arial14_white
							.getStringWidth(lines.get(lines.size() - 1));
					if (lineLen > maxLen) {
						maxLen = lineLen;
					}
					offset += 40;
					left -= 40;
				}

				height = 35 + (lines.size() - 1) * 15;
				width = maxLen;
			} else {
				int maxLen = 0;
				int left = tooltip.length();
				int offset = 0;
				int wordOffset = 0;
				while (wordOffset < words.length) {

					// Add words so that they match up to max 100 +- 20 len
					String line = "";
					int ww = 0;
					while ((ww = FontRenderer.arial14_white
							.getStringWidth(line)) < maxWidth) {

						// Nothing more to add to the line
						if (wordOffset == words.length) {
							break;
						}

						String tempOldLine = line;
						line += words[wordOffset++] + " ";

						int oww = ww;
						ww = FontRenderer.arial14_white.getStringWidth(line);

						if (ww > maxWidth - 20) {
							// Check offsets
							if (ww > maxWidth + 20) {
								line = tempOldLine;
								ww = oww;
								wordOffset--;
								break; // Break this while loop
							}
							break;
						}

					}

					if (ww > maxLen) {
						maxLen = ww;
					}

					height += 15;
					lines.add(line);
				}

				width = maxLen;
				height -= 15;
			}

		} else {
			lines.add(tooltip);
		}

		glColor4f(0.6f, 0.6f, 0.6f, 0.8f);

		// Draw background part of tooltip
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		{
			glColor4f(0.6f, 0.6f, 0.6f, 0.8f);
			drawQuad(x + 10, y, width + 20, height);
		}
		glEnd();

		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		// Center the text
		for (int i = 0; i < lines.size(); i++) {
			FontRenderer.arial14_white.drawString(lines.get(i), x + 20, y + 10
					+ (i * 15));
		}
	}

	/**
	 * Paint a rectangle at the given position
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void paintRectangle(int x1, int y1, int x2, int y2) {
		paintRectangle(x1, y1, x2, y2, null, 1.0f);
	}

	/**
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param opacity
	 */
	public void paintRectangle(int x1, int y1, int x2, int y2, Vec3 color) {
		paintRectangle(x1, y1, x2, y2, color, 1.0f);
	}

	/**
	 * Paint a rectangle with the given opacity at the given position
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param opacity
	 */
	public void paintRectangle(int x1, int y1, int x2, int y2, float opacity) {
		paintRectangle(x1, y1, x2, y2, null, opacity);
	}

	/**
	 * Paint a rectangle with the given color and opacity at the given position
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 * @param opacity
	 */
	public void paintRectangle(int x1, int y1, int x2, int y2, Vec3 color,
			float opacity) {
		if (opacity < 0.0f) {
			opacity = 0.0f;
		} else if (opacity > 1.0f) {
			opacity = 1.0f;
		}
		if (color != null) {
			glColor4f(color.x, color.y, color.z, 1.0f - opacity);
		} else {
			glColor4f(1.0f, 1.0f, 1.0f, 1.0f - opacity);
		}
		glBegin(GL_QUADS);
		{
			drawQuad(x1, y1, x2, y2);
		}
		glEnd();
	}

	/**
	 * Draw a rect
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawRect(int x1, int y1, int x2, int y2) {
		drawLine(x1, y1, x1, y2);
		drawLine(x1, y2, x2, y2);
		drawLine(x1, y1, x2, y1);
		drawLine(x2, y1, x2, y2);
	}

	/**
	 * Draw a line
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {
		GL11.glBegin(GL11.GL_LINES);
		{
			glVertex2f(x1, y1);
			glVertex2f(x2, y2);
		}
		GL11.glEnd();
	}

	/**
	 * Draw a quad
	 * 
	 * @param _x
	 * @param _y
	 * @param _width
	 * @param _height
	 */
	public static void drawQuad(float _x, float _y, float _width, float _height) {
		glVertex2f(_x, _y + _height);
		glVertex2f(_x + _width, _y + _height);
		glVertex2f(_x + _width, _y);
		glVertex2f(_x, _y);
	}

	/**
	 * Draw a textured quad
	 * 
	 * @param _x
	 * @param _y
	 * @param _width
	 * @param _height
	 */
	public static void drawTexturedQuad(float _x, float _y, float _width,
			float _height) {
		glTexCoord2f(0.0f, 1.0f);
		glVertex2f(_x, _y + _height);
	
		glTexCoord2f(1.0f, 1.0f);
		glVertex2f(_x + _width, _y + _height);

		glTexCoord2f(1.0f, 0.0f);
		glVertex2f(_x + _width, _y);
	
		glTexCoord2f(0.0f, 0.0f);
		glVertex2f(_x, _y);

	}

}
