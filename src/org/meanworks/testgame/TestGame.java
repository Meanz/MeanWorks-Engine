package org.meanworks.testgame;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.io.File;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.meanworks.engine.Application;
import org.meanworks.engine.GameApplication;
import org.meanworks.engine.gui.Button;
import org.meanworks.engine.gui.GuiHandler;
import org.meanworks.engine.gui.impl.PerformanceGraph;
import org.meanworks.engine.gui.impl.Tooltip;
import org.meanworks.engine.gui.impl.radialmenu.RadialButton;
import org.meanworks.engine.gui.impl.radialmenu.RadialMenu;
import org.meanworks.engine.math.Ray;
import org.meanworks.engine.model.MWMLoader;
import org.meanworks.render.geometry.Geometry;
import org.meanworks.render.geometry.Vertex;
import org.meanworks.render.geometry.animation.AnimationChannel;
import org.meanworks.render.geometry.animation.LoopMode;
import org.meanworks.render.opengl.ImmediateRenderer;
import org.meanworks.render.opengl.Window;
import org.meanworks.render.opengl.shader.ShaderProgram;
import org.meanworks.render.texture.Texture;
import org.meanworks.testgame.world.Player;
import org.meanworks.testgame.world.Region;
import org.meanworks.testgame.world.Tile;
import org.meanworks.testgame.world.World;

public class TestGame extends GameApplication {

	/*
	 * The world
	 */
	private World world;

	/*
	 * 
	 */
	private Ray currentRay;

	/*
	 * 
	 */
	private Tile selectedTile;

	/*
	 * 
	 */
	private Player player;

	/*
	 * 
	 */
	private GuiHandler guiHandler;

	/*
	 * 
	 */
	private ShaderProgram waterShader;

	/*
	 * 
	 */
	private Texture waterTexture;

	/*
	 * 
	 */
	private boolean flying = true;

	/*
	 * 
	 */
	private Geometry kostjaModel = null;

	/**
	 * 
	 * @return
	 */
	public static TestGame getGame() {
		return (TestGame) Application.getApplication();
	}

	/**
	 * 
	 * @return
	 */
	public static World getWorld() {
		return getGame().world;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.GameApplication#setup()
	 */
	@Override
	public void setup() {
		try {
			String osName = System.getProperty("os.name").toLowerCase();
			boolean isMacOs = osName.startsWith("mac os x");
			if (isMacOs) {
				System.setProperty("java.library.path",
						System.getProperty("java.library.path") + ";"
								+ new File("native/macosx").getAbsolutePath());
				System.setProperty("org.lwjgl.librarypath", new File(
						"native/macosx").getAbsolutePath());
			} else {
				System.setProperty("java.library.path",
						System.getProperty("java.library.path")
								+ ";"
								+ new File("native/windows/").getAbsolutePath()
										.replaceAll("\\/", "\\"));
				System.setProperty("org.lwjgl.librarypath",
						new File("native/windows/").getAbsolutePath()
								.replaceAll("\\/", "\\"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		setWindow(Window.createWindow(1200, 800));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.Application#preload()
	 */
	@Override
	public void preload() {

		/*
		 * Create the gui handler
		 */
		guiHandler = new GuiHandler(this);
		getInputHandler().addKeyListener(guiHandler);
		getInputHandler().addMouseListener(guiHandler);

		/*
		 * Setup Gui
		 */
		RadialMenu radialMenu = new RadialMenu() {
		};
		guiHandler.addComponent(radialMenu);
		radialMenu.addButton(new RadialButton("Test Button 1"));
		radialMenu.addButton(new RadialButton("Test Button 2"));
		guiHandler.addComponent(new Button("Wireframe", 10, 100, 110, 35) {

			private boolean wireframe = false;

			@Override
			public void onButtonClick() {
				wireframe = !wireframe;
				GL11.glPolygonMode(GL11.GL_FRONT, wireframe ? GL11.GL_LINE
						: GL11.GL_FILL);
				GL11.glPolygonMode(GL11.GL_BACK, wireframe ? GL11.GL_LINE
						: GL11.GL_FILL);
			}
		});

		guiHandler.addComponent(new Button("Stop Flying", 10, 140, 110, 35) {

			@Override
			public void onButtonClick() {
				if (!flying) {
					flying = true;
					setText("Stop Flying");
				} else {
					flying = false;
					setText("Fly");
				}
			}
		});

		guiHandler.addComponent(new PerformanceGraph() {
		});
		guiHandler.addComponent(new Tooltip() {
		});

		/*
		 * Setup basic controls
		 */
		getCamera().yaw(-25);
		getCamera().pitch(45);
		getCamera().translate(315f, 134f, 224f);

		/*
		 * Create out world
		 */
		world = new World();

		/*
		 * Create our player
		 */
		player = new Player();
		player.getTransform().setPosition(315,
				world.getInterpolatedHeight(315, 213), 213);
		getScene().getRootNode().addChild(player);

		// Let's try to load a model
		kostjaModel = MWMLoader.loadModel("./data/models/kostja.mwm");
		kostjaModel.getTransform().setPosition(315, 135, 213);
		kostjaModel.getTransform().setScale(0.2f, 0.2f, 0.2f);
		getScene().getRootNode().addChild(kostjaModel);

		/*
		 * Load our water
		 */
		waterShader = getAssetManager().loadShader("./data/shaders/water");
		waterTexture = getAssetManager().loadTexture("./data/images/water.png");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.Application#update()
	 */
	@Override
	public void update() {
		// Update the gui handler
		guiHandler.update();

		// Update the camera
		getCamera().update();

		// Update the world
		world.update((int) (getCamera().getPosition().x / Region.REGION_WIDTH),
				(int) (getCamera().getPosition().z / Region.REGION_HEIGHT));

		// Set the camera height to the terrain height
		if (!flying) {
			// TODO: Add jumping and such
			getCamera().translate(
					0.0f,
					(float) world.getInterpolatedHeight(getCamera()
							.getPosition().x, getCamera().getPosition().z),
					0.0f);
		}

		if (!guiHandler.didConsumeInput()) {

			float moveSpeed = 0.1f;
			// Ray trace the ground
			currentRay = getCamera().getPickRay(Mouse.getX(), Mouse.getY());
			// Find out where we hit the ground
			selectedTile = world.pickRay(currentRay, 20);

			Tooltip.setTooltip(selectedTile != null ? selectedTile
					.getTileType().getName() : null);

			if (Mouse.isButtonDown(1)) { // RMB
				float mouseRatio = 0.2f;

				float yincr = getInputHandler().getDX() * mouseRatio;
				float pincr = -getInputHandler().getDY() * mouseRatio;
				getCamera().yaw(yincr);
			} else if (Mouse.isButtonDown(0)) {

				// We want to find out the exact coordinate of where we clicked
				// But for now let's just take the tile
				if (selectedTile != null) {
					System.err.println("Moving to point "
							+ selectedTile.getTilePosition().toString());
					player.moveTowards(selectedTile.getTilePosition().x,
							selectedTile.getTilePosition().y);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.Application#render()
	 */
	@Override
	public void render() {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_DEPTH_TEST);

		glEnable(GL_CULL_FACE);
		getCamera().updateCamera();

		// Draw the world
		world.render();

		glEnable(GL11.GL_TEXTURE_2D);
		glDisable(GL_CULL_FACE);

		// Draw the selected tile
		if (selectedTile != null) {
			drawSelectedTile();
		}

		// Draw a water thing
		waterShader.use();
		waterShader.setProjectionViewMatrix(getCamera()
				.getProjectionViewMatrix());
		waterShader.setModelMatrix(getCamera().getModelMatrix());

		// Texture.enable();
		waterTexture.bind();
		ImmediateRenderer.enableBlending();
		ImmediateRenderer.drawPlane(world.getWorldX() * Region.REGION_WIDTH
				- world.VIEW_DISTANCE * Region.REGION_WIDTH, 100.0f,
				world.getWorldY() * Region.REGION_WIDTH - world.VIEW_DISTANCE
						* Region.REGION_HEIGHT, world.getWorldX()
						* Region.REGION_WIDTH + world.VIEW_DISTANCE
						* Region.REGION_WIDTH, world.getWorldY()
						* Region.REGION_WIDTH - world.VIEW_DISTANCE
						* Region.REGION_HEIGHT);
		ImmediateRenderer.disableBlending();
		waterShader.useNone();

		// Draw the gui
		guiHandler.render();

		glEnable(GL_CULL_FACE);
	}

	/**
	 * Draw the selected tile
	 */
	public void drawSelectedTile() {
		float p1H = world.getTileHeight(
				(int) selectedTile.getTilePosition().x + 1,
				(int) selectedTile.getTilePosition().y) + 0.01f;
		float p2H = world.getTileHeight((int) selectedTile.getTilePosition().x,
				(int) selectedTile.getTilePosition().y) + 0.01f;
		float p3H = world.getTileHeight((int) selectedTile.getTilePosition().x,
				(int) selectedTile.getTilePosition().y + 1) + 0.01f;
		float p4H = world.getTileHeight(
				(int) selectedTile.getTilePosition().x + 1,
				(int) selectedTile.getTilePosition().y + 1) + 0.01f;

		// p4----p3
		// |_____|
		// |_____|
		// p1----p2
		Vertex p1 = new Vertex(new Vector3f(
				(float) (int) selectedTile.getTilePosition().x
						+ Region.TILE_WIDTH, p1H,
				(float) (int) selectedTile.getTilePosition().y));
		Vertex p2 = new Vertex(new Vector3f(
				(float) (int) selectedTile.getTilePosition().x, p2H,
				(float) (int) selectedTile.getTilePosition().y));
		Vertex p3 = new Vertex(new Vector3f(
				(float) (int) selectedTile.getTilePosition().x, p3H,
				(float) (int) selectedTile.getTilePosition().y
						+ Region.TILE_LENGTH));
		Vertex p4 = new Vertex(new Vector3f(
				(float) (int) selectedTile.getTilePosition().x
						+ Region.TILE_WIDTH, p4H,
				(float) (float) (int) selectedTile.getTilePosition().y
						+ Region.TILE_LENGTH));

		glColor4f(1.0f, 1.0f, 1.0f, 0.4f);

		Texture.disable();
		ImmediateRenderer.enableBlending();
		ImmediateRenderer.drawPlane(p1, p2, p3, p4);
		ImmediateRenderer.disableBlending();
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new TestGame().start();
	}

}
