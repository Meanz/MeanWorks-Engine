package org.meanworks.engine.core;

import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.render.opengl.Screen;

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
public class Input {
	
	/**
	 * We are going with the singleton strat
	 */
	private static Input singleton;
	
	/**
	 * The mouse and key listeners
	 */
	private LinkedList<KeyListener> keyListeners;
	private LinkedList<MouseListener> mouseListeners;
	/**
	 * Mouse values
	 */
	private int mouseX;
	private int mouseY;
	private int deltaX;
	private int deltaY;
	private int deltaWheel;

	/**
	 * The pressed values
	 */
	private LinkedList<Integer> mousePressedButtons;
	private LinkedList<Integer> mouseReleasedButtons;
	private LinkedList<Integer> mouseHeldButtons;
	private LinkedList<Integer> keyboardPressedKeys;
	private LinkedList<Integer> keyboardReleasedKeys;

	/**
	 * Some cool stuff
	 */
	private String allowedKeys = "abcdefghijklmnopqrstuvwxyzæøåABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ1234567890§\"![]{}$#%&/()=?\\-+,.-_:;<>*^ ";
	private HashMap<Integer, Character> itc = new HashMap<Integer, Character>();
	private LinkedList<Integer> keysDown = new LinkedList<Integer>();
	private LinkedList<Integer> keysPressed = new LinkedList<Integer>();

	/**
	 * Initializes a new input manager
	 */
	public Input() {
		if(singleton != null) {
			throw new RuntimeException("Can only have one input handler");
		}
		singleton = this;
		keyListeners = new LinkedList<KeyListener>();
		mouseListeners = new LinkedList<MouseListener>();
		mousePressedButtons = new LinkedList<Integer>();
		mouseReleasedButtons = new LinkedList<Integer>();
		mouseHeldButtons = new LinkedList<Integer>();
		keyboardPressedKeys = new LinkedList<Integer>();
		keyboardReleasedKeys = new LinkedList<Integer>();

		/**
		 * Initialize
		 */
		/**
		 * Create Mouse
		 */
		if (!Mouse.isCreated()) {
			try {
				Mouse.create();
			} catch (LWJGLException lwjgle) {
				EngineLogger
						.error("    - Unable to create mouse, error occured:"
								+ lwjgle.getMessage());
			}
		} else {
			EngineLogger.error("    - Unable to create mouse, already created");
		}
		/**
		 * Create Keyboard
		 */
		if (!Keyboard.isCreated()) {
			try {
				Keyboard.create();
			} catch (LWJGLException lwjgle) {
				EngineLogger
						.error("    - Unable to create keyboard, error occured:"
								+ lwjgle.getMessage());
			}
		} else {
			EngineLogger
					.error("    - Unable to create keyboard, already created");
		}
	}

	public static boolean isMouseGrabbed() {
		return Mouse.isGrabbed();
	}

	public static void grabMouse() {
		Mouse.setGrabbed(true);
	}

	public static void ungrabMouse() {
		Mouse.setGrabbed(false);
	}

	/**
	 * Add a mouse listener to this input manager
	 * 
	 * @param mouseListener
	 */
	public static void addMouseListener(MouseListener mouseListener) {
		singleton.mouseListeners.add(mouseListener);
	}

	/**
	 * Add a key listener to this input manager
	 * 
	 * @param keyListener
	 */
	public static void addKeyListener(KeyListener keyListener) {
		singleton.keyListeners.add(keyListener);
	}

	/**
	 * Key to char
	 * 
	 * @param key
	 * @return
	 */
	public static char keyToChar(int key) {
		if (singleton.itc.containsKey(key)) {
			return singleton.itc.get(key);
		} else {
			return '0';
		}
	}

	/**
	 * The main update loop
	 */
	public void update() {
		/**
		 * Stored mouse delta values
		 */
		mouseX = Mouse.getX();
		mouseY = Screen.getHeight() - Mouse.getY();
		// deltaX = mouseX - saveX;
		// deltaY = saveY - mouseY;
		deltaX = Mouse.getDX();
		deltaY = -Mouse.getDY();
		deltaWheel = Mouse.getDWheel();

		/**
		 * Check if the mouse was moved or not
		 */
		if (deltaX != 0 || deltaY != 0) {
			for (MouseListener mouseListener : mouseListeners) {
				mouseListener.mouseMoved(deltaX, deltaY);
			}
		}

		/**
		 * Check if any mouse button was pressed
		 */

		// Unflag old pressed
		for (Integer i : mousePressedButtons) {
			mouseHeldButtons.add(i);
		}
		mousePressedButtons.clear();
		mouseReleasedButtons.clear();

		while (Mouse.next()) {
			int button = Mouse.getEventButton();
			if (Mouse.getButtonName(button) == null) {
				continue;
			}
			// System.err.println("Button: " + Mouse.getButtonName(button) +
			// " was " + (Mouse.getEventButtonState() ? "pressed" :
			// "released"));
			/**
			 * Check if mouse was newly pressed
			 */
			if (Mouse.isButtonDown(button)) {

				/**
				 * Update states
				 */
				if (!isMouseDown(button)) {
					mousePressedButtons.add(button);
				}

				/**
				 * Fire a mouse pressed event and continue
				 */
				for (MouseListener mouseListener : mouseListeners) {
					mouseListener.mousePressed(button, mouseX, mouseY);
				}
			} else {

				// Simple, but may bug out, who cares :p
				mouseHeldButtons.remove((Object)button);
				mouseReleasedButtons.add(button);

				/**
				 * Fire a mouse released event and continue
				 */
				for (MouseListener mouseListener : mouseListeners) {
					mouseListener.mouseReleased(button, mouseX, mouseY);
				}
			}
		}

		/**
		 * Check for keyboard presses
		 */
		while (Keyboard.next()) {
			int key = Keyboard.getEventKey();
			boolean press = Keyboard.getEventKeyState();
			if (!itc.containsKey(Keyboard.getEventKey())) {
				itc.put(Keyboard.getEventKey(), Keyboard.getEventCharacter());
			}
			if (press) {
				/**
				 * Save the state of the key
				 */
				if (keysPressed.contains(key)) {
					keysPressed.remove(key); // Cast to object to make sure we
												// don't use remove(index)
				} else {
					keysPressed.add(key);
				}
				if (!keysDown.contains(key)) {
					keysDown.add(key);
				}
				/**
				 * Fire a keyboard pressed event and continue
				 */
				for (KeyListener keyListener : keyListeners) {
					keyListener.keyPressed(key);
				}
			} else {
				/**
				 * Remove the state of the key
				 */
				if (keysPressed.contains(key)) {
					keysPressed.remove((Object) key); // Cast to object to make
														// sure we don't use
														// remove(index)
				}
				if (keysDown.contains(key)) {
					keysDown.remove((Object) key); // Cast to object to make
													// sure we don't use
													// remove(index)
				}
				/**
				 * Fire a keyboard released event and continue
				 */
				for (KeyListener keyListener : keyListeners) {
					keyListener.keyReleased(key);
				}
			}
		}
	}

	public static boolean isValidKey(int key) {
		if (singleton.allowedKeys.contains("" + keyToChar(key))) {
			return true;
		} else {
			return false;
		}
	}

	public static int getSize() {
		return singleton.keysPressed.size() + singleton.keysDown.size();
	}

	public static boolean isMousePressed(int button) {
		return singleton.mousePressedButtons.contains(button);
	}

	public static boolean isMouseReleased(int button) {
		return singleton.mouseReleasedButtons.contains(button);
	}

	public static boolean isMouseDown(int button) {
		return singleton.mouseHeldButtons.contains(button);
	}

	public static boolean isKeyPressed(int key) {
		return singleton.keysPressed.contains(key);
	}

	public static boolean isKeyDown(int key) {
		return singleton.keysDown.contains(key);
	}

	public static int getDeltaWheel() {
		return singleton.deltaWheel;
	}

	public static int getDX() {
		return singleton.deltaX;
	}

	public static int getDY() {
		return singleton.deltaY;
	}

	public static int getMouseX() {
		return singleton.mouseX;
	}

	public static int getMouseY() {
		return singleton.mouseY;
	}
}
