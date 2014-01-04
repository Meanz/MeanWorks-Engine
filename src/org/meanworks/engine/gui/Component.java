package org.meanworks.engine.gui;

import java.util.LinkedList;

import org.lwjgl.input.Mouse;
import org.meanworks.engine.core.Application;

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
public abstract class Component extends Surface {

	/*
	 * The gui handler
	 */
	private static GuiHandler guiHandler;

	/*
	 * The next available id for a component
	 */
	private static int nextId = 0;

	/**
	 * Get the next available component id
	 * 
	 * @return
	 */
	public static int getNextId() {
		return (nextId++);
	}

	/*
	 * The x and y coordinates for the component
	 */
	private int x, y;
	/*
	 * The width and height of the component
	 */
	private int width, height;
	/*
	 * The name of the component
	 */
	private String name;
	/*
	 * Whether or not this component is visible
	 */
	private boolean visible;
	/*
	 * The list of components in under this component
	 */
	private LinkedList<Component> components = new LinkedList<>();
	/*
	 * A list of components that will be sent to the front on the next update
	 * tick
	 */
	private LinkedList<Component> toFront = new LinkedList<>();
	/*
	 * A list of components that will be sent to the back on the next update
	 * tick
	 */
	private LinkedList<Component> toBack = new LinkedList<>();
	/*
	 * The list of components that needs a notification when focus is lost
	 */
	private LinkedList<Component> focusNotifications = new LinkedList<>();
	/*
	 * Whether or not focus was lost under this component
	 */
	private boolean focusLost;
	/*
	 * The parent of this component
	 */
	private Component parent;

	/**
	 * Construct a new component
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Component(String name, int x, int y, int width, int height) {
		if (guiHandler == null) {
			guiHandler = Application.getApplication().getGui();
		}
		this.name = name == null ? "UntitledComponent" : name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.visible = true;
		focusLost = false;
		parent = null;
	}

	/**
	 * Request a focus notification for the given component
	 * 
	 * @param c
	 */
	public void requestFocusNotification(Component c) {
		focusNotifications.add(c);
	}

	/**
	 * Request a focus notification for this component
	 */
	public void requestFocusNotification() {
		// Ask our parent for a focus notification
		if (parent != null) {
			parent.requestFocusNotification(this);
		}
	}

	/**
	 * Send this component to the front of the list in the next update
	 */
	public void toFront() {
		if (parent == null) {
			return;
		}
		parent.toFront(this);
	}

	/**
	 * Send this component to the back of the list in the next updateF
	 */
	public void toBack() {
		if (parent == null) {
			return;
		}
		parent.toBack(this);
	}

	/**
	 * Send the given component to the front of the list in the next update
	 * 
	 * @param c
	 */
	public void toFront(Component c) {
		toFront.add(c);
	}

	/**
	 * Send the given component to the back of the list in the next update
	 * 
	 * @param c
	 */
	public void toBack(Component c) {
		toBack.add(c);
	}

	/**
	 * Get whether this component is visible or not
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Set whether this component is visible or not
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Get the components of this component
	 * 
	 * @return
	 */
	public LinkedList<Component> getComponents() {
		return components;
	}

	/**
	 * Get the mouse x coordinate
	 * 
	 * @return
	 */
	public final static int getMouseX() {
		return Mouse.getX();
	}

	/**
	 * Get the mouse y coordinate
	 * 
	 * @return
	 */
	public final static int getMouseY() {
		return getWindowHeight() - Mouse.getY();
	}

	/**
	 * Get the window width
	 * 
	 * @return
	 */
	public final static int getWindowWidth() {
		return Application.getApplication().getWindow().getWidth();
	}

	/**
	 * Get the window height
	 * 
	 * @return
	 */
	public final static int getWindowHeight() {
		return Application.getApplication().getWindow().getHeight();
	}

	/**
	 * Add a component
	 * 
	 * @param component
	 */
	public Component add(Component component) {
		component.parent = this;
		components.add(component);
		return component;
	}

	/**
	 * Remove a component
	 * 
	 * @param component
	 * @return
	 */
	public boolean remove(Component component) {
		return component.remove(component);
	}

	/**
	 * Set the position of this component
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Translate the position of this component
	 * 
	 * @param dx
	 * @param dy
	 */
	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	/**
	 * Set the size of this component
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Get the name of the component
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the x position of the component
	 * 
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the y position of the component
	 * 
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the width of the component
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the component
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/*
	 * New input system yet again
	 */

	public void onFocusLost() {

	}

	/**
	 * Called when this component is hovered
	 * 
	 * @param x
	 * @param y
	 */
	public void onMouseEnter(int x, int y) {

	}

	/**
	 * Called when this component is unhovered
	 * 
	 * @param x
	 * @param y
	 */
	public void onMouseExit(int x, int y) {

	}

	/**
	 * Called when this component get's a mouse pressed event
	 * 
	 * @param button
	 * @param mouseX
	 * @param mouseY
	 */
	public boolean onMouseDown(int button, int mouseX, int mouseY) {
		return false;
	}

	/**
	 * Called when this component get's a mouse released event
	 * 
	 * @param button
	 * @param mouseX
	 * @param mouseY
	 */
	public boolean onMouseUp(int button, int mouseX, int mouseY) {
		return false;
	}

	/**
	 * Called when this component get's a mouse moved event
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @param mouseDeltaX
	 * @param mouseDeltaY
	 */
	public boolean onMouseMove(int mouseX, int mouseY, int mouseDeltaX,
			int mouseDeltaY) {
		return false;
	}

	/**
	 * Called when a key was pressed
	 * 
	 * @param key
	 * @return
	 */
	public boolean onKeyDown(int key) {
		return false;
	}

	/**
	 * Called when a key was released
	 * 
	 * @param key
	 * @return
	 */
	public boolean onKeyUp(int key) {
		return false;
	}

	/**
	 * Check whether the given point is inside this components bounds
	 * 
	 * @param _x
	 * @param _y
	 * @return
	 */
	public final boolean isInside(int _x, int _y) {
		if (_x >= x && _x <= x + width && _y >= y && _y <= y + height) {
			return true;
		}
		return false;
	}

	/**
	 * Fire a focus lost event to all the children components, including self
	 */
	public final void fireFocusLost() {
		for (Component c : components) {
			// c.fireFocusLost();
			// c.onFocusLost();
		}
	}

	/**
	 * Called when a mouse button is pressed
	 * 
	 * @param button
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public final boolean fireMouseDown(int button, int mouseX, int mouseY) {
		for (Component c : components) {
			if (c.fireMouseDown(button, mouseX, mouseY)) {
				focusLost = true;
				return true;
			}
		}
		return onMouseDown(button, mouseX, mouseY);
	}

	/**
	 * Called when a mouse button is released
	 * 
	 * @param button
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public final boolean fireMouseUp(int button, int mouseX, int mouseY) {
		for (Component c : components) {
			if (c.fireMouseUp(button, mouseX, mouseY)) {
				focusLost = true;
				return true;
			}
		}
		return onMouseUp(button, mouseX, mouseY);
	}

	/**
	 * Called when the mouse is moved
	 * 
	 * @param button
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public final boolean fireMouseMove(int mouseX, int mouseY, int mouseDeltaX,
			int mouseDeltaY) {
		for (Component c : components) {
			if (c.fireMouseMove(mouseX, mouseY, mouseDeltaX, mouseDeltaY)) {
				focusLost = true;
				return true;
			}
		}
		return onMouseMove(mouseX, mouseY, mouseDeltaX, mouseDeltaY);
	}

	/**
	 * Called when a key is pressed
	 * 
	 * @param key
	 * @return
	 */
	public final boolean fireKeyDown(int key) {
		for (Component c : components) {
			if (c.onKeyDown(key)) {
				focusLost = true;
				return true;
			}
		}
		return onKeyDown(key);
	}

	/**
	 * Called when a key is released
	 * 
	 * @param key
	 * @return
	 * 
	 */
	public final boolean fireKeyUp(int key) {

		/*
		 * Start from the bottom and up
		 */
		for (Component c : components) {
			if (c.onKeyUp(key)) {
				focusLost = true;
				return true;
			}
		}
		return onKeyUp(key);
	}

	/**
	 * Fire the render command
	 */
	public final void fireRender() {
		if (!isVisible()) {
			return;
		}
		render();
		for (int i = components.size() - 1; i >= 0; i--) {
			components.get(i).fireRender();
		}
	}

	/**
	 * Render the component
	 */
	public abstract void render();

	/**
	 * 
	 */
	public final void fireUpdate() {
		if (toFront.size() > 0) {
			for (Component c : toFront) {
				components.remove(c);
				components.addFirst(c);
			}
			toFront.clear();
		}
		if (toBack.size() > 0) {
			for (Component c : toBack) {
				components.remove(c);
				components.addLast(c);
			}
			toBack.clear();
		}
		if (focusLost) {
			for (Component c : focusNotifications) {
				c.fireFocusLost();
			}
			focusNotifications.clear();
			focusLost = false;
		}
		for (Component component : components) {
			component.update();
		}
		update();
	}

	/**
	 * Can be called from a super
	 */
	public void update() {

	}

	/**
	 * Helper functions
	 */
}
