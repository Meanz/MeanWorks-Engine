package org.fractalstudio.engine.gui;

import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import static org.lwjgl.opengl.GL11.*;

public class FontRenderer {

	/**
	 * 2D Text Rendering
	 */
	private UnicodeFont defaultFont;

	/**
	 * The instance
	 */
	private static FontRenderer fontRenderer;

	public FontRenderer() {
		fontRenderer = this;
		/**
		 * Load our font
		 */
		try {
			defaultFont = new UnicodeFont("./data/fonts/arial.ttf", 14, false,
					false);
			defaultFont.addAsciiGlyphs(); // Add Glyphs
			defaultFont.addGlyphs(400, 600); // Add Glyphs
			defaultFont.getEffects().add(new ColorEffect(java.awt.Color.white)); // Add
			// Effects
			defaultFont.loadGlyphs();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Load Glyphs
	}

	public void ortho2d(int width, int height) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, height, 0, 0.0f, 100);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public void gluPersp3D(int width, int height) {
		/**
		 * Basic opengl settings
		 */
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		// glFrustum(-1.0, 1.0, -1.0, 1.0, 1.0f, 100);
		GLU.gluPerspective(60.0f, width / height, 0.1f, 1000.0f);
		glMatrixMode(GL_MODELVIEW);
	}

	public void persp3d() {
		/**
		 * Setup a perspective projection
		 */
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		// glFrustum(0, getFrame().getWidth(), getFrame().getHeight(), 0, 0.1f,
		// 1000.0f);
		// l, r, b, t, znear, zfar
		glFrustum(-1.0, 1.0, -1.0, 1.0, 1.0f, 1000.0f);
		glMatrixMode(GL_MODELVIEW);
	}

	public static void drawString(String s, int x, int y) {
		if (s == null)
			return;
		fontRenderer.defaultFont.drawString((float) x, (float) y, s);
	}
}
