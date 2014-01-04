package org.meanworks.engine.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;

import java.util.LinkedList;
import java.util.List;

import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.render.FontRenderer;

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
public class Button extends Component {

	/*
	 * Whether or not this button is being clicked
	 */
	private boolean isClicked = false;

	/*
	 * The text of this button
	 */
	private String text = null;

	/*
	 * The tooltop of this button
	 */
	private String tooltip = null;

	/*
	 * The dimensions of this button
	 */
	private int buttonWidth, buttonHeight;

	/*
	 * Internal variable for the text width
	 */
	private int textWidth;

	/*
	 * Internal variable for the tooltip text width
	 */
	private int tooltipWidth;

	/*
	 * Whether the text in this button will be automatically centered or not
	 */
	private boolean centered = true;

	/*
	 * The x offset of the text relative to the background position
	 */
	private int textXOffset = 0;

	/*
	 * The y offset of the text relative to the background position
	 */
	private int textYOffset = 0;

	/*
	 * The background color of the button
	 */
	private Vec3 backgroundColor = new Vec3(0.6f, 0.6f, 0.6f);

	/*
	 * The color of the button when it's being hovered
	 */
	private Vec3 hoverColor = new Vec3(0.8f, 0.8f, 0.8f);

	/*
	 * The color of the button when you are clicking it
	 */
	private Vec3 clickColor = new Vec3(0.99f, 0.99f, 0.99f);

	/*
	 * The opacity on the button's background
	 */
	private float opacity = 0.2f;

	/*
	 * Whether or nto we are hovering this button
	 */
	private boolean isHovered = false;

	/**
	 * 
	 * @param text
	 * @param x
	 * @param y
	 */
	public Button(String text, int x, int y) {
		super("Button_" + Component.getNextId(), x, y,
				FontRenderer.arial14_white.getStringWidth(text) + 20, 35);
		this.text = text;
		buttonWidth = 0;
		buttonHeight = 0;
		textXOffset = 10;
		textYOffset = 10;
		textWidth = FontRenderer.arial14_white.getStringWidth(text);
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
		textWidth = FontRenderer.arial14_white.getStringWidth(text);
	}

	/**
	 * 
	 * @param text
	 * @param x
	 * @param y
	 */
	public Button(String text, int x, int y, int width, int height,
			boolean centered) {
		super("Button_" + Component.getNextId(), x, y, width, height);
		this.text = text;
		buttonWidth = width;
		buttonHeight = height;
		this.centered = centered;
		textWidth = FontRenderer.arial14_white.getStringWidth(text);
	}

	/**
	 * Set the opacity of the button
	 * 
	 * @param opacity
	 */
	public void setOpacity(float opacity) {
		if (opacity < 0.0f) {
			opacity = 0.0f;
		}
		if (opacity > 1.0f) {
			opacity = 1.0f;
		}
		this.opacity = opacity;
	}

	/**
	 * Set the background color of the button
	 * 
	 * @param color
	 */
	public void setBackgroundColor(Vec3 color) {
		if (color == null) {
			return;
		}
		this.backgroundColor = color;
	}

	/**
	 * Set the color to be displayed when clicking the button
	 * 
	 * @param color
	 */
	public void setClickColor(Vec3 color) {
		if (color == null) {
			return;
		}
		this.clickColor = color;
	}

	/**
	 * Set the color to be displayed when hovering the button
	 * 
	 * @param color
	 */
	public void setHoverColor(Vec3 color) {
		if (color == null) {
			return;
		}
		this.hoverColor = color;
	}

	/**
	 * Set the text x offset, does not work with centered text
	 * 
	 * @param xOffset
	 */
	public void setTextXOffset(int xOffset) {
		this.textXOffset = xOffset;
	}

	/**
	 * Set the text y offset, does not work with centered text
	 * 
	 * @param yOffset
	 */
	public void setTextYOffset(int yOffset) {
		this.textYOffset = yOffset;
	}

	/**
	 * Set the tooltip of this button
	 * 
	 * @param tooltip
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
		if (tooltip != null) {
			// Update width
			tooltipWidth = FontRenderer.arial14_white.getStringWidth(tooltip);
		}
	}

	/**
	 * Get the text of the button
	 * 
	 * @return
	 */
	public String getText() {
		return text;
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
			setSize(FontRenderer.arial14_white.getStringWidth(text) + 20,
					getHeight());
		}
		textWidth = FontRenderer.arial14_white.getStringWidth(text);
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meanworks.engine.gui.Component#onFocusLost()
	 */
	@Override
	public void onFocusLost() {
		if (isClicked || isHovered) {
			isClicked = false;
			isHovered = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#onMouseDown(int, int, int)
	 */
	@Override
	public final boolean onMouseDown(int button, int mouseX, int mouseY) {
		if (isInside(mouseX, mouseY)) {
			isClicked = true;
			requestFocusNotification();
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#onMouseUp(int, int, int)
	 */
	@Override
	public final boolean onMouseUp(int button, int mouseX, int mouseY) {
		if (isClicked) {
			if (isInside(mouseX, mouseY)) {
				onButtonClick();
			}
			isClicked = false;
			return true;
		} else {
			return false;
		}
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
		if (isInside(mouseX, mouseY)) {
			requestFocusNotification();
			isHovered = true;
			return true;
		} else {
			isHovered = false;
			return false;
		}
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
		if (isClicked) {
			paintRectangle(getX(), getY(), getWidth(), getHeight(), clickColor,
					opacity);
		} else if (isHovered) {
			paintRectangle(getX(), getY(), getWidth(), getHeight(), hoverColor,
					opacity);
		} else {
			paintRectangle(getX(), getY(), getWidth(), getHeight(),
					backgroundColor, opacity);
		}
		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		// Draw the font
		if (buttonWidth == 0 && buttonHeight == 0) {
			FontRenderer.arial14_white.drawString(text, getX() + textXOffset,
					getY() + textYOffset);
		} else if (centered) {
			// Center the text
			FontRenderer.arial14_white.drawString(text, getX()
					- (textWidth / 2) + (getWidth() / 2), getY() + 10);
		} else {
			FontRenderer.arial14_white.drawString(text, getX() + textXOffset,
					getY() + textYOffset);
		}

		/*
		 * Render tooltip if hovered
		 */
		if (isHovered && tooltip != null) {
			paintTooltip(tooltip, getMouseX(), getMouseY());
		}

	}
}
