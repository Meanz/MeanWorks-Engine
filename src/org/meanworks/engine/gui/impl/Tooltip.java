package org.meanworks.engine.gui.impl;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glColor4f;

import java.awt.Font;

import org.lwjgl.opengl.GL11;
import org.meanworks.engine.Application;
import org.meanworks.engine.gui.Component;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Tooltip extends Component {

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static int getStringWidth(String text) {
		if (text == null) {
			return 15;
		}
		return text.length() * 8 + 5;
	}

	/*
	 * 
	 */
	private static String tooltip;

	/**
	 * 
	 * @param tooltip
	 */
	public static void setTooltip(String tooltip) {
		Tooltip.tooltip = tooltip;
	}

	/*
	 * 
	 */
	private static UnicodeFont tooltipFont;

	/**
	 * 
	 */
	public Tooltip() {
		super("Tooltip", 0, 0, 0, 0);
		if (tooltipFont == null) {
			try {
				tooltipFont = new UnicodeFont("./data/fonts/arial.ttf", 12,
						false, false);
				tooltipFont.addAsciiGlyphs(); // Add Glyphs
				tooltipFont.addGlyphs(400, 600); // Add Glyphs
				tooltipFont.getEffects().add(
						new ColorEffect(java.awt.Color.black)); // Add
				// Effects
				tooltipFont.loadGlyphs();
			} catch (SlickException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Load Glyphs
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#render()
	 */
	public void render() {
		if (tooltip == null) {
			return;
		}
		// We need to get the mouse position
		int tx = Application.getApplication().getInputHandler().getMouseX() + 8;
		int ty = Application.getApplication().getInputHandler().getMouseY() + 12;
		int h = 25;
		int strw = getStringWidth(tooltip);

		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		{
			// Make inner button
			glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
			glVertex2f(tx + 2, ty + h - 2);
			glVertex2f(tx + strw - 2, ty + h - 2);
			glVertex2f(tx + strw - 2, ty + 2);
			glVertex2f(tx + 2, ty + 2);
		}
		glEnd();
		
		
		
		// Draw the font
		glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		tooltipFont.drawString((float) tx + 5, (float) ty + 5, tooltip);

	}

}
