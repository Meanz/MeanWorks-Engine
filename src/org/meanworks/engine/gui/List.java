package org.meanworks.engine.gui;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;

import java.util.LinkedList;

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
public class List extends Component {

	/*
	 * The elements in this list
	 */
	private LinkedList<Object> elements = new LinkedList<Object>();

	/*
	 * The selected index in this list
	 */
	private int selectedIndex = -1;

	/*
	 * The hover index in this list
	 */
	private int hoverIndex = -1;

	/*
	 * The scroll offset in this list
	 */
	private int scrollOffset = 0;

	/**
	 * Construct a new list at the given position
	 * 
	 * @param x
	 * @param y
	 */
	public List(int x, int y, int width, int height) {
		super("List" + getNextId(), x, y, width, height);
	}

	/**
	 * Add an element to this list
	 * 
	 * @param element
	 */
	public void addElement(Object element) {
		if (element == null) {
			return;
		}
		if (selectedIndex == -1) {
			selectedIndex = 0;
		}
		elements.add(element);
	}

	/**
	 * Remove an element from this listF
	 * 
	 * @param element
	 */
	public void removeElement(Object element) {
		if (element == null) {
			return;
		}
		if (elements.size() - 1 <= selectedIndex) {
			if (elements.size() - 1 < 1) {
				selectedIndex = -1;
			} else {
				selectedIndex--;
			}
		}
		elements.remove(element);
	}

	/**
	 * Get the currently selected element
	 * 
	 * @return
	 */
	public Object getSelectedElement() {
		return selectedIndex != -1 ? elements.get(selectedIndex) : null;
	}

	/**
	 * Get the selected index of this list
	 * 
	 * @return
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * Internal function for getting the element at the given coordinates
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getElementAt(int x, int y) {
		if (!isInside(x, y)) {
			return -1;
		}
		return (y - getY() + scrollOffset) / 20;
	}

	/**
	 * Called when an element is selected or deselected
	 */
	public void onSelection() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meanworks.engine.gui.Component#onMouseDown(int, int, int)
	 */
	@Override
	public boolean onMouseDown(int button, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		if (isInside(mouseX, mouseY)) {

			// Determine selection
			selectedIndex = getElementAt(mouseX, mouseY);
			if (selectedIndex >= elements.size()) {
				selectedIndex = elements.size() - 1;
			}
			onSelection();
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meanworks.engine.gui.Component#onMouseMove(int, int, int, int)
	 */
	@Override
	public boolean onMouseMove(int mouseX, int mouseY, int mouseDeltaX,
			int mouseDeltaY) {

		if (isInside(mouseX, mouseY)) {
			hoverIndex = getElementAt(mouseX, mouseY);
			if (hoverIndex >= elements.size()) {
				hoverIndex = -1;
			}
		} else {
			hoverIndex = -1;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meanworks.engine.gui.Component#render()
	 */
	@Override
	public void render() {

		/*
		 * Draw background
		 */
		glDisable(GL_TEXTURE_2D);

		paintRectangle(getX(), getY(), getWidth(), getHeight(), new Vec3(0.8f,
				0.8f, 0.8f), 0.0f);

		paintRectangle(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2,
				new Vec3(0.55f, 0.55f, 0.55f), 0.0f);

		if (selectedIndex != -1) {

			paintRectangle(getX() + 1, getY() + 1 + (selectedIndex * 20),
					getWidth() - 2, 20, new Vec3(0.7f, 0.7f, 0.7f), 0.0f);

		}

		if (hoverIndex != -1 && hoverIndex != selectedIndex) {
			paintRectangle(getX() + 1, getY() + 1 + (hoverIndex * 20),
					getWidth() - 2, 20, new Vec3(0.6f, 0.6f, 0.6f), 0.0f);
		}

		int yOff = 5;
		for (Object obj : elements) {
			if (getHeight() < yOff) {
				break;
			}
			FontRenderer.arial14_white.drawString(
					obj == null ? "null" : obj.toString(), getX() + 5, getY()
							- 3 + yOff);
			yOff += 20;
		}

	}
}
