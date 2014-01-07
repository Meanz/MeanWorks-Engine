package org.meanworks.engine.core;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.meanworks.engine.EngineConfig;
import org.meanworks.engine.RenderState;
import org.meanworks.engine.asset.AssetManager;
import org.meanworks.engine.camera.Camera;
import org.meanworks.engine.camera.FirstPersonCamera;
import org.meanworks.engine.gui.GuiHandler;
import org.meanworks.engine.gui.impl.Console;
import org.meanworks.engine.gui.impl.PerformanceGraph;
import org.meanworks.engine.render.material.Material;
import org.meanworks.engine.render.opengl.GLWindow;
import org.meanworks.engine.scene.Scene;
import org.meanworks.engine.scripts.ScriptHandler;
import org.meanworks.engine.util.Timer;

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
public abstract class Application implements Runnable {

	/*
	 * The singleton for the application
	 */
	private static Application application;

	/**
	 * 
	 * @return
	 */
	public static Application getApplication() {
		return application;
	}

	/*
	 * Whether the application is running or not
	 */
	private boolean running = false;

	/*
	 * The application window
	 */
	private GLWindow window = null;

	/*
	 * The input handler for the application
	 */
	private InputHandler inputHandler = null;

	/*
	 * The asset manager for the application
	 */
	private AssetManager assetManager = null;

	/*
	 * The scene graph for this application
	 */
	private Scene scene;

	/*
	 * The gui handler for this application
	 */
	private GuiHandler guiHandler;

	/*
	 * The console for this application, it's an extension of the gui handler
	 */
	private Console console;

	/*
	 * Not used at the moment
	 */
	//private Timer timer;

	/*
	 * The script handler for this application, used for external scripts like
	 * JavaScript
	 */
	private ScriptHandler scriptHandler;

	/*
	 * Statistics
	 */
	private int fps = 0;
	private int ups = 0;
	private double frameTime = 0.0d;

	/**
	 * Construct a new application
	 */
	public Application() {
		application = this;
	}

	/*
	 * Application settings
	 */
	private int targetUps = 50; // Number of updates per second

	/**
	 * Get the script handler of this application
	 * 
	 * @return
	 */
	public ScriptHandler getScriptHandler() {
		return scriptHandler;
	}

	/**
	 * Forward getter from Scene.getCamera
	 * 
	 * @return
	 */
	public Camera getCamera() {
		if (scene == null) {
			return null;
		}
		return Scene.getCamera();
	}

	/**
	 * Get the gui handler of this application
	 * 
	 * @return
	 */
	public GuiHandler getGui() {
		return guiHandler;
	}

	/**
	 * Get the scene graph of this application
	 * 
	 * @return
	 */
	public Scene getScene() {
		return scene;
	}

	/**
	 * Get the frame time for the last frame
	 * 
	 * @return
	 */
	public double getFrameTime() {
		return frameTime;
	}

	/**
	 * Get the asset manager for this application
	 * 
	 * @return
	 */
	public AssetManager getAssetManager() {
		return assetManager;
	}

	/**
	 * Get the input handler of this application
	 * 
	 * @return
	 */
	public InputHandler getInputHandler() {
		return inputHandler;
	}

	/**
	 * @return the window
	 */
	public GLWindow getWindow() {
		return window;
	}

	/**
	 * @param window
	 *            the window to set
	 */
	public void setWindow(GLWindow window) {
		this.window = window;
	}

	/**
	 * Get the console of this application
	 * 
	 * @return
	 */
	public Console getConsole() {
		return console;
	}

	/**
	 * Get the number of frames per second this application is running on
	 * 
	 * @return
	 */
	public int getFps() {
		return fps;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Get the number of updates per second this application is running on
	 * 
	 * @return
	 */
	public int getUps() {
		return ups;
	}

	/**
	 * Start this application
	 */
	public void start() {
		/*
		 * Setup
		 */
		setup();

		running = true;
		run();
	}

	/**
	 * Stops this application
	 */
	public void stop() {
		running = false;
	}

	/**
	 * A method that can be overridden so we can hook on window close events
	 */
	public void onWindowClose() {
		onExit();
		stop();
	}

	/**
	 * The core application loop
	 */
	public void run() {
		/*
		 * Game Loop
		 */
		int MAX_FRAMESKIP = 10;
		long last_game_tick = System.currentTimeMillis();
		long next_game_tick = last_game_tick;
		int loops;
		int fpsc = 0;
		int upsc = 0;
		long last_fps = System.currentTimeMillis();

		int SKIP_TICKS = 1000 / targetUps;

		int savedUps = 0;

		/*
		 * Parse what version we are using
		 */
		String s = GL11.glGetString(GL11.GL_VERSION);
		if (s.startsWith("2")) {
			// We have to use old
			EngineConfig.usingModernOpenGL = false;
		} else if (s.startsWith("3") || s.startsWith("4")) {
			EngineConfig.usingModernOpenGL = true;
		}

		// Create the timer
		//timer = new Timer();

		/*
		 * Create the input handler
		 */
		guiHandler = new GuiHandler(this);
		/**
		 * Add a console to the application
		 */
		getGui().addComponent((console = new Console()));

		inputHandler = new InputHandler();
		assetManager = new AssetManager();
		scene = new Scene();

		getInputHandler().addKeyListener(guiHandler);
		getInputHandler().addMouseListener(guiHandler);

		/*
		 * Setup the default material
		 */
		Material.DEFAULT_MATERIAL = null;

		if (EngineConfig.usingModernOpenGL) {
			Material.DEFAULT_MATERIAL = new Material("DEFAULT_MATERIAL",
					AssetManager.loadShader("./data/shaders/colorShader"));
		} else {
			Material.DEFAULT_MATERIAL = new Material("DEFAULT_MATERIAL",
					AssetManager.loadShader("./data/shaders/150colorShader"));
		}

		/*
		 * Set camera details TODO: Cleanup here
		 */
		Scene.setCamera(new FirstPersonCamera(window.getWidth(), window
				.getHeight(), 60, window.getAspect()));

		/*
		 * Create our script handler
		 */
		scriptHandler = new ScriptHandler();

		/*
		 * Preload
		 */
		preload();

		/*
		 * Start the render/update loop
		 */
		while (running) {
			/*
			 * Logging
			 */
			if (System.currentTimeMillis() - last_fps > 1000) {
				fps = fpsc;
				ups = upsc;
				upsc = 0;
				fpsc = 0;
				last_fps = System.currentTimeMillis();
				PerformanceGraph.tick(1, fps);
			}
			/*
			 * Update
			 */
			long renderStart = System.nanoTime();
			loops = 0;
			while (System.currentTimeMillis() > next_game_tick
					&& loops < MAX_FRAMESKIP) {

				/*
				 * Update the updates per second
				 */
				if (savedUps != targetUps) {
					SKIP_TICKS = 1000 / targetUps;
					savedUps = targetUps;
				}

				/*
				 * Check if we want to close the window
				 */
				if (window.isCloseRequested()) {
					onWindowClose();
				}

				// UPDATE OUR ASSET MANAGER
				assetManager.update();

				// TEMP LWJGL FUNCTIONS
				Display.processMessages();
				// END OF TEMP LWJGL FUNCTIONS

				// Grab input keys
				inputHandler.update();
				// Parse input keys in the gui handler
				guiHandler.update();
				// Parse updates in the application
				update(); // Call the update function
				scene.update();

				next_game_tick += SKIP_TICKS;

				loops++;
				upsc++;
			}
			/*
			 * Render and also store the time used to render the frame (Useful
			 * for analyzing performance)
			 */

			/*
			 * Clear the statistics for the last frame
			 */
			RenderState.clearRenderedVertices();

			// Clear render state for the next render loop
			RenderState.clearState();
			render(); // Call the render function

			// Clear render state for the scene render loop
			RenderState.clearState();
			scene.render();

			// Clear render state for the gui render loop
			RenderState.clearState();

			glDisable(GL_CULL_FACE);
			guiHandler.render();
			glEnable(GL_CULL_FACE);

			fpsc++;

			window.update();

			long renderEnd = System.nanoTime();
			long deltaRender = renderEnd - renderStart;
			frameTime = (double) (deltaRender * Math.pow(10, -9));
		}
	}

	/**
	 * Called when the application is exited
	 */
	public abstract void onExit();

	/**
	 * Setup the application
	 */
	public abstract void setup();

	/**
	 * Called to load the application
	 */
	public abstract void preload();

	/**
	 * Called every update tick
	 */
	public abstract void update();

	/**
	 * Called every frame
	 */
	public abstract void render();

}
