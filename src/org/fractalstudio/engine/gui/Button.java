package org.fractalstudio.engine.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;

import org.fractalstudio.render.opengl.ImmediateRenderer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Button extends Component {

	/*
	 * 
	 */
	private boolean gray = false;

	/*
	 * 
	 */
	private String text = null;

	/*
	 * 
	 */
	private int buttonWidth, buttonHeight;
	
	/*
	 * 
	 */
	private int textWidth;

	/**
	 * 
	 * @param text
	 * @param x
	 * @param y
	 */
	public Button(String text, int x, int y) {
		super("Button_" + Component.getNextId(), x, y,
				FontRenderer.arial14.getStringWidth(text) + 20, 35);
		this.text = text;
		buttonWidth = 0;
		buttonHeight = 0;
		textWidth = FontRenderer.arial14.getStringWidth(text);
	}

	/**
	 * 
	 * @param text
	 * @param x
	 * @param y
	 */
	public Button(String text, int x, int y, int width, int height) {
		super("Button_" + Component.getNextId(), x, y, width, height);
		this.text = text;
		buttonWidth = width;
		buttonHeight = height;
		textWidth = FontRenderer.arial14.getStringWidth(text);
	}

	/**
	 * Set the text of the button
	 * 
	 * @param text
	 */
	public void setText(String text) {
		if (text == null) {
			text = "";
		}
		// Update width
		// only if we are using auto width buttons
		if (buttonWidth == 0 && buttonHeight == 0) {
			setSize(FontRenderer.arial14.getStringWidth(text) + 20, getHeight());
		}
		textWidth = FontRenderer.arial14.getStringWidth(text);
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#onMouseDown(int, int, int)
	 */
	@Override
	public final boolean onMouseDown(int button, int mouseX, int mouseY) {
		activateInputLock();
		gray = true;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#onMouseUp(int, int, int)
	 */
	@Override
	public final boolean onMouseUp(int button, int mouseX, int mouseY) {
		onButtonClick();
		deactivateInputLock();
		gray = false;
		return true;
	}

	/*
	 * Hover (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#onMouseMove(int, int, int,
	 * int)
	 */
	@Override
	public final boolean onMouseMove(int mouseX, int mouseY, int mouseDeltaX,
			int mouseDeltaY) {
		return false;
	}

	// Called whenever the button is clicked
	public void onButtonClick() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#render()
	 */
	@Override
	public void render() {
		glDisable(GL_TEXTURE_2D);

		// Draw background part of button
		glBegin(GL_QUADS);
		{
			// Make border
			if (gray || isHovered()) {
				glColor4f(0.9f, 0.9f, 0.9f, 0.8f);
			} else {
				glColor4f(0.6f, 0.6f, 0.6f, 0.8f);
			}
			drawQuad(getX(), getY(), getWidth(), getHeight());
		}
		glEnd();
		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		// Draw the font
		glColor3f(0.0f, 0.0f, 0.0f);

		
		if (buttonWidth == 0 && buttonHeight == 0) {
			FontRenderer.arial14.drawString(text, getX() + 10, getY() + 10);
		} else {
			// Center the text
			FontRenderer.arial14.drawString(text, getX() - (textWidth / 2) + (getWidth() / 2), getY() + 10);
		}
	}
}
