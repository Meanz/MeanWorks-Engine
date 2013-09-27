package org.fractalstudio.engine.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.fractalstudio.engine.gui.event.ActionEvent;
import org.fractalstudio.engine.gui.event.EventType;
import org.fractalstudio.engine.gui.event.MouseEvent;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Button extends Component {

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static int getStringWidth(String text) {
		return FontRenderer.arial14.getStringWidth(text);
	}

	/*
	 * 
	 */
	private static UnicodeFont buttonFont;

	/*
	 * 
	 */
	private boolean gray = false;

	/*
	 * 
	 */
	private String text = null;

	/**
	 * 
	 * @param text
	 * @param x
	 * @param y
	 */
	public Button(String text, int x, int y) {
		super("Button_" + Component.getNextId(), x, y,
				getStringWidth(text) + 20, 35);
		this.text = text;

		if (buttonFont == null) {
			try {
				buttonFont = new UnicodeFont("./data/fonts/arial.ttf", 14,
						false, false);
				buttonFont.addAsciiGlyphs(); // Add Glyphs
				buttonFont.addGlyphs(400, 600); // Add Glyphs
				buttonFont.getEffects().add(
						new ColorEffect(java.awt.Color.black)); // Add
				// Effects
				buttonFont.loadGlyphs();
			} catch (SlickException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Load Glyphs
		}
	}

	/**
	 * Set the text of the button
	 * @param text
	 */
	public void setText(String text) {
		if (text == null) {
			text = "";
		}
		// Update width
		setSize(getStringWidth(text) + 20, getHeight());
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fractalstudio.engine.gui.Component#actionPerformed(org.fractalstudio
	 * .engine.gui.GuiHandler, org.fractalstudio.engine.gui.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(GuiHandler guiHandler, ActionEvent actionEvent) {

		if (actionEvent.getEventType() == EventType.MOUSE_PRESSED) {
			MouseEvent mouseEvent = actionEvent.asMouseEvent();
			if (mouseEvent.getKey() == MouseEvent.LMB) {
				guiHandler.mouseLock(this);
				gray = true;
			}

		} else if (actionEvent.getEventType() == EventType.MOUSE_RELEASED) {
			MouseEvent mouseEvent = actionEvent.asMouseEvent();
			if (mouseEvent.getKey() == MouseEvent.LMB) {
				onButtonClick();
				guiHandler.mouseRelease(this);
				gray = false;
			}
		}

	}

	// Called whenever the button is clicked
	public void onButtonClick() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#render()
	 */
	@Override
	public void render() {
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		{
			// Make border
			glColor3f(0.7f, 0.7f, 0.7f);
			drawQuad(getX(), getY(), getWidth(), getHeight());


			// Make inner button
			if (gray) {
				glColor3f(0.9f, 0.9f, 0.9f);
			} else {
				glColor3f(1.0f, 1.0f, 1.0f);
			}
			drawQuad(getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4);
		}
		glEnd();

		// Draw the font
		glColor3f(0.0f, 0.0f, 0.0f);
		buttonFont.drawString((float) getX() + 10, (float) getY() + 10, text);
	}
}
