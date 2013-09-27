package org.fractalstudio.engine.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class FontRenderer {

	/**
	 * 
	 */
	public static FontRenderer arial14;

	/**
	 * 2D Text Rendering
	 */
	private UnicodeFont unicodeFont;

	public FontRenderer(String fontFile, int size) {

		/**
		 * Load default font
		 */
		if (arial14 == null) {
			try {
				unicodeFont = new UnicodeFont("./data/fonts/arial.ttf", 14,
						false, false);
				unicodeFont.addAsciiGlyphs(); // Add Glyphs
				unicodeFont.addGlyphs(400, 600); // Add Glyphs
				unicodeFont.getEffects().add(new ColorEffect(Color.white)); // Add
				// Effects
				unicodeFont.loadGlyphs();

				arial14 = this;
			} catch (SlickException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Load Glyphs
		}
	}

	/**
	 * Set the color of this font
	 * 
	 * @param color
	 */
	public void setColor(Color color) {
		unicodeFont.getEffects().add(new ColorEffect(color));
	}

	/**
	 * Get the width of this string
	 * 
	 * @param text
	 * @return
	 */
	public int getStringWidth(String text) {
		return unicodeFont.getWidth(text);
	}

	/**
	 * Draw the given string
	 * 
	 * @param s
	 * @param x
	 * @param y
	 */
	public void drawString(String s, int x, int y) {
		if (s == null)
			return;
		unicodeFont.drawString((float) x, (float) y, s);
	}
}
