package org.meanworks.engine.gui;

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
public class Label extends Component {

	/*
	 * The text of this label
	 */
	private String text = null;

	/**
	 * Construct a new label
	 * 
	 * @param text
	 * @param x
	 * @param y
	 */
	public Label(String text, int x, int y) {
		super("Label" + getNextId(), x, y,
				text != null ? FontRenderer.arial14_white.getStringWidth(text)
						: 0, 15);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if (text == null) {
			return;
		}
		setSize(FontRenderer.arial14_white.getStringWidth(text), 15);
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meanworks.engine.gui.Component#render()
	 */
	@Override
	public void render() {
		if (text != null) {
			FontRenderer.arial14_white.drawString(text, getX(), getY());
		}
	}

}
