package org.meanworks.engine.gui;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;

import org.lwjgl.input.Keyboard;
import org.meanworks.engine.core.Input;
import org.meanworks.engine.math.Vec3;
import org.meanworks.engine.render.FontRenderer;

/**
 * Copyright (C) 2014 Steffen Evensen
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
public class TextField extends Component {

	/**
	 * 
	 */
	private String tooltip = null;

	/**
	 * 
	 */
	private String text = "1.0";

	/**
	 * Whether or not this textfield is focused
	 */
	private boolean isFocused = false;

	/**
	 * Whether or not this textfield is hovered
	 */
	private boolean isHovered = false;

	/**
	 * Whether this text field is clicked or not
	 */
	private boolean isClicked = false;

	/**
	 * The click color for this textfield
	 */
	public Vec3 clickColor = new Vec3(0.9f, 0.9f, 0.9f);

	/**
	 * The hover color for this textfield
	 */
	public Vec3 hoverColor = new Vec3(0.6f, 0.6f, 0.6f);

	/**
	 * The background color for this textfield
	 */
	public Vec3 backgroundColor = new Vec3(0.5f, 0.5f, 0.5f);

	/**
	 * The border color for this textfield
	 */
	public Vec3 borderColor = new Vec3(0.8f, 0.8f, 0.8f);

	/**
	 * The opacity for this textfield
	 */
	public float opacity = 0.2f;

	/**
	 * Construct a new TextField
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public TextField(int x, int y, int width, int height) {
		super("TextField_" + getNextId(), x, y, width, height);

	}
	
	/**
	 * Called when the text field is selected
	 */
	public void onSelected() {
		
	}

	/**
	 * Called when enter key is pressed on the field
	 */
	public void onSubmit() {

	}

	/**
	 * Get the text of this textfield
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set the text of this textfield
	 * 
	 * @param text
	 */
	public void setText(String text) {
		if (text == null) {
			this.text = "";
		} else {
			this.text = text;
		}
	}

	/**
	 * Set the opacity of this window, E[0-1] 1.0f = 100% opacity
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
	 * Set the tooltip of this textfield
	 * 
	 * @param tooltip
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
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
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.gui.Component#onMouseDown(int, int, int)
	 */
	@Override
	public final boolean onMouseUp(int button, int mouseX, int mouseY) {
		if (isClicked) {
			isFocused = true;
			isClicked = false;
			if (Gui.isKeyInputLocked()) {
				return false;
			} else {
				Gui.lockKeyInput(this);
			}
			onSelected();
			return true;
		} else {
			isFocused = false;
			if (Gui.getKeyInputLockComponent() == this)
				Gui.openKeyInput();
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
			isHovered = true;
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean onKeyUp(int key) {
		// TODO Auto-generated method stub
		if (isFocused) {
			if (Input.isValidKey(key)) {
				char _keychar = Input.keyToChar(key);
				text += _keychar;
			} else {
				if (key == Keyboard.KEY_RETURN) {
					onSubmit();
					isFocused = false;
					if (Gui.getKeyInputLockComponent() == this)
						Gui.openKeyInput();
				} else if (key == Keyboard.KEY_BACK) {
					if (text.length() > 0)
						text = text.substring(0, text.length() - 1);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public final void update() {
		if (!isInside(getMouseX(), getMouseY())) {
			isHovered = false;
		}
	}

	/**
	 * 
	 */
	@Override
	public void render() {
		glDisable(GL_TEXTURE_2D);

		// Draw background part of button

		// Paint the border
		paintRectangle(getX(), getY(), getWidth(), getHeight(), borderColor,
				opacity);

		if (isFocused) {
			paintRectangle(getX() + 1, getY() + 1, getWidth() - 2,
					getHeight() - 2, clickColor, opacity);
		} else if (isHovered) {
			paintRectangle(getX() + 1, getY() + 1, getWidth() - 2,
					getHeight() - 2, hoverColor, opacity);
		} else {
			paintRectangle(getX() + 1, getY() + 1, getWidth() - 2,
					getHeight() - 2, backgroundColor, opacity);

		}
		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		// Draw the text
		FontRenderer.arial14_white.drawString(text, getX() + 4, getY() + 4);

		/*
		 * Render tooltip if hovered
		 */
		if (isHovered && tooltip != null) {
			paintTooltip(tooltip, getMouseX(), getMouseY());
		}
	}

}
