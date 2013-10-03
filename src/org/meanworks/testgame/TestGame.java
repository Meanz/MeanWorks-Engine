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

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.meanworks.engine.GameApplication;
import org.meanworks.engine.gui.Button;
import org.meanworks.engine.gui.GuiHandler;
import org.meanworks.engine.gui.impl.PerformanceGraph;
import org.meanworks.engine.gui.impl.Tooltip;
import org.meanworks.engine.gui.impl.radialmenu.RadialButton;
import org.meanworks.engine.gui.impl.radialmenu.RadialMenu;
import org.meanworks.engine.math.Ray;
import org.meanworks.engine.model.MWMLoader;
import org.meanworks.render.geometry.AnimatedModel;
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
		 * Setup basic controls
		 */

		getCamera().yaw(-25);
		getCamera().pitch(30);
		getCamera().translate(0.0f, 0.0f, 0.0f);

		world = new World();

		player = new Player();
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

		// Load the water shader
		waterShader = getAssetManager().loadShader("./data/shaders/water");
		waterTexture = getAssetManager().loadTexture("./data/images/water.png");

		getScene().getRootNode().addChild(player);
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

		// Make the camera follow the player's position
		getCamera().setPosition(player.getTransform().getPosition());

		// Update the world
		world.update((int) (getCamera().getPosition().x / Region.REGION_WIDTH),
				(int) (getCamera().getPosition().z / Region.REGION_HEIGHT));

		// Set the camera height to the terrain height
		if (!flying) {
			// TODO: Add jumping and such
			player.getTransform().translate(0.0f,
					-player.getTransform().getPosition().y + 2.0f, 0.0f);
			getCamera().translate(
					0.0f,
					(float) world.getInterpolatedHeight(getCamera()
							.getPosition().x, getCamera().getPosition().z),
					0.0f);
		}
		// Update the player
		player.update();

		if (!guiHandler.didConsumeInput()) {
			if (Mouse.isButtonDown(1)) { // RMB
				float mouseRatio = 0.2f;

				float yincr = getInputHandler().getDX() * mouseRatio;
				float pincr = -getInputHandler().getDY() * mouseRatio;
				getCamera().yaw(yincr);
				getCamera().pitch(-pincr);
			}

			float moveSpeed = 0.1f;
			// Ray trace the ground
			currentRay = getCamera().getPickRay(Mouse.getX(), Mouse.getY());
			// Find out where we hit the ground
			selectedTile = world.pickRay(currentRay, 20);

			Tooltip.setTooltip(selectedTile != null ? selectedTile
					.getTileType().getName() : null);
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				this.stop();
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				moveSpeed = 1.0f;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				player.getTransform().translate(
						moveSpeed
								* (float) Math.sin(Math.toRadians(getCamera()
										.getYaw())),
						0.0f,
						-moveSpeed
								* (float) Math.cos(Math.toRadians(getCamera()
										.getYaw())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				player.getTransform().translate(
						moveSpeed
								* (float) Math.sin(Math.toRadians(getCamera()
										.getYaw() + 90)),
						0.0f,
						-moveSpeed
								* (float) Math.cos(Math.toRadians(getCamera()
										.getYaw() + 90)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				player.getTransform().translate(
						moveSpeed
								* (float) Math.sin(Math.toRadians(getCamera()
										.getYaw() - 90)),
						0.0f,
						-moveSpeed
								* (float) Math.cos(Math.toRadians(getCamera()
										.getYaw() - 90)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				player.getTransform().translate(
						-moveSpeed
								* (float) Math.sin(Math.toRadians(getCamera()
										.getYaw())),
						0.0f,
						moveSpeed
								* (float) Math.cos(Math.toRadians(getCamera()
										.getYaw())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				player.getTransform().translate(0.0f, -0.5f * moveSpeed, 0.0f);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				player.getTransform().translate(0.0f, 0.5f * moveSpeed, 0.0f);
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
