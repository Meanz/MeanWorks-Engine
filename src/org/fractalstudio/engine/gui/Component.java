package org.fractalstudio.engine.gui;

import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.LinkedList;

import org.fractalstudio.engine.Application;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class Component {

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
	 * 
	 */
	private boolean inputLock;
	/*
	 * 
	 */
	private boolean visible;
	/*
	 * 
	 */
	private LinkedList<Component> components = new LinkedList<>();
	/*
	 * 
	 */
	private boolean hovered;

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
		this.name = name == null ? "UntitledComponent" : name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.inputLock = false;
		this.visible = true;
		this.hovered = false;
	}

	/**
	 * Check whether this component is hovered or not
	 * 
	 * @return
	 */
	public boolean isHovered() {
		return hovered;
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
	public void add(Component component) {
		components.add(component);
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
	 * Check whether this component has input lock or not
	 * 
	 * @return
	 */
	public boolean hasInputLock() {
		return inputLock;
	}

	/**
	 * Activate input lock on this component
	 */
	public void activateInputLock() {
		inputLock = true;
	}

	/**
	 * Deactivate the input lock on this component
	 */
	public void deactivateInputLock() {
		inputLock = false;
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
	 * Called when a mouse button is released
	 * 
	 * @param button
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public final boolean fireMouseUp(int button, int mouseX, int mouseY) {
		// If the mouse is inside the bounds of this component pass the action
		// on
		// Or if this component has input lock also pass it on
		if (!isInside(mouseX, mouseY) && !hasInputLock()) {
			return false;
		}

		/*
		 * Start from the bottom and up
		 */
		for (int i = components.size() - 1; i >= 0; i--) {
			if (components.get(i).fireMouseUp(button, mouseX, mouseY)) {
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

		// If the mouse is inside the bounds of this component pass the action
		// on
		// Or if this component has input lock also pass it on
		if (!isInside(mouseX, mouseY)) {
			if (isHovered()) {
				hovered = false;
			}
			if (!hasInputLock()) {
				return false;
			}
		} else {
			if (!isHovered()) {
				hovered = true;
			}
		}

		/*
		 * Start from the bottom and up
		 */
		for (int i = components.size() - 1; i >= 0; i--) {
			if (components.get(i).fireMouseMove(mouseX, mouseY, mouseDeltaX,
					mouseDeltaY)) {
				return true;
			}
		}

		return onMouseMove(mouseX, mouseY, mouseDeltaX, mouseDeltaY);
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
		// If the mouse is inside the bounds of this component pass the action
		// on
		// Or if this component has input lock also pass it on
		if (!isInside(mouseX, mouseY) && !hasInputLock()) {
			return false;
		}

		/*
		 * Start from the bottom and up
		 */
		for (int i = components.size() - 1; i >= 0; i--) {
			if (components.get(i).fireMouseDown(button, mouseX, mouseY)) {
				return true;
			}
		}

		return onMouseDown(button, mouseX, mouseY);
	}

	/**
	 * Fire the render command
	 */
	public final void fireRender() {
		if (!isVisible()) {
			return;
		}
		for (Component component : components) {
			component.render();
		}
		render();
	}

	/**
	 * Render the component
	 */
	public abstract void render();

	/**
	 * Helper functions
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {
		GL11.glBegin(GL11.GL_LINES);
		{
			glVertex2f(x1, y1);
			glVertex2f(x2, y2);
		}
		GL11.glEnd();
	}

	public static void drawQuad(float _x, float _y, float _width, float _height) {
		glVertex2f(_x, _y + _height);
		glVertex2f(_x + _width, _y + _height);
		glVertex2f(_x + _width, _y);
		glVertex2f(_x, _y);
	}
}
