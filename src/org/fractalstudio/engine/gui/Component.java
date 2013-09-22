package org.fractalstudio.engine.gui;

import java.util.LinkedList;

import org.fractalstudio.engine.gui.event.ActionEvent;

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
	private LinkedList<Component> components = new LinkedList<>();

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

	/**
	 * Called when an action is performed on this component
	 * 
	 * @param actionEvent
	 */
	public void actionPerformed(GuiHandler guiHandler, ActionEvent actionEvent) {

	}

	/**
	 * Render the component
	 */
	public abstract void render();
}
