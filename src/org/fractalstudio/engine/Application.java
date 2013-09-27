package org.fractalstudio.engine;

import org.fractalstudio.engine.asset.AssetManager;
import org.fractalstudio.engine.gui.InputHandler;
import org.fractalstudio.engine.gui.impl.PerformanceGraph;
import org.fractalstudio.render.material.Material;
import org.fractalstudio.render.opengl.Renderer;
import org.fractalstudio.render.opengl.Window;
import org.fractalstudio.render.opengl.shader.ShaderHelper;
import org.lwjgl.opengl.Display;

public abstract class Application {

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
	 * The camera for this application
	 */
	private Camera camera;

	/*
	 * Whether the application is running or not
	 */
	private boolean running = false;

	/*
	 * The application window
	 */
	private Window window = null;

	/*
	 * The renderer for the application
	 */
	private Renderer renderer = null;

	/*
	 * The input handler for the application
	 */
	private InputHandler inputHandler = null;

	/*
	 * The asset manager for the application
	 */
	private AssetManager assetManager = null;

	/*
	 * Statistics
	 */
	private int fps = 0;
	private int ups = 0;
	private long renderTime = 0L;

	/**
	 * 
	 */
	public Application() {
		application = this;
	}

	/*
	 * Application settings
	 */
	private int targetUps = 50; // Number of updates per second

	/**
	 * Get the asset manager for this application
	 * 
	 * @return
	 */
	public AssetManager getAssetManager() {
		return assetManager;
	}

	/**
	 * Get the camera for this application
	 * 
	 * @return
	 */
	public Camera getCamera() {
		return camera;
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
	public Window getWindow() {
		return window;
	}

	/**
	 * @param window
	 *            the window to set
	 */
	public void setWindow(Window window) {
		this.window = window;
		camera = new Camera(window.getWidth(), window.getHeight(), 60,
				window.getAspect());
	}

	/**
	 * @return the renderer
	 */
	public Renderer getRenderer() {
		return renderer;
	}

	/**
	 * @param renderer
	 *            the renderer to set
	 */
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
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
	 * 
	 */
	public void start() {
		setup();
		running = true;
		loop();
	}

	/**
	 * 
	 */
	public void stop() {
		running = false;
	}

	/**
	 * A method that can be overridden so we can hook on window close events
	 */
	public void onWindowClose() {
		stop();
	}

	/**
	 * The core application loop
	 */
	public void loop() {
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

		/*
		 * Create the input handler
		 */
		inputHandler = new InputHandler();
		assetManager = new AssetManager();

		/*
		 * Setup the default material
		 */
		Material.DEFAULT_MATERIAL = new Material("DEFAULT_MATERIAL",
				ShaderHelper.createDefaultShader());
		
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
				PerformanceGraph.feedTick(fps);
			}
			/*
			 * Update
			 */
			loops = 0;
			while (System.currentTimeMillis() > next_game_tick
					&& loops < MAX_FRAMESKIP) {

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

				inputHandler.update();

				update(); // Call the update function

				next_game_tick += 1000 / targetUps;

				loops++;
				upsc++;
			}
			/*
			 * Render and also store the time used to render the frame (Useful
			 * for analyzing performance)
			 */
			long renderStart = System.currentTimeMillis();
			render(); // Call the render function
			fpsc++;
			long renderEnd = System.currentTimeMillis();
			long deltaRender = renderEnd - renderStart;
			renderTime = (int) (deltaRender);

			window.update();
		}
	}

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
