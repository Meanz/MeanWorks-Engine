package org.fractalstudio.testgame;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.fractalstudio.engine.GameApplication;
import org.fractalstudio.engine.gui.Button;
import org.fractalstudio.engine.gui.GuiHandler;
import org.fractalstudio.engine.gui.PerformanceGraph;
import org.fractalstudio.engine.gui.Tooltip;
import org.fractalstudio.engine.math.Ray;
import org.fractalstudio.engine.model.ColladaImporter;
import org.fractalstudio.render.geometry.Geometry;
import org.fractalstudio.render.geometry.Vertex;
import org.fractalstudio.render.opengl.ImmediateRenderer;
import org.fractalstudio.render.opengl.Window;
import org.fractalstudio.render.opengl.shader.ShaderProgram;
import org.fractalstudio.render.texture.Texture;
import org.fractalstudio.testgame.world.Player;
import org.fractalstudio.testgame.world.Region;
import org.fractalstudio.testgame.world.Tile;
import org.fractalstudio.testgame.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

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
	private ShaderProgram geometryShader;	
	
	/*
	 * 
	 */
	private Texture waterTexture;

	/*
	 * 
	 */
	private Texture geometryTexture;	
	
	/*
	 * 
	 */
	private boolean flying = true;
	
	/*
	 * 
	 */
	private Geometry geometry;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fractalstudio.engine.GameApplication#setup()
	 */
	@Override
	public void setup() {
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

		getCamera().yaw(90);
		getCamera().pitch(45);
		getCamera().translate(-1.0f, 5.0f, -1.0f);

		world = new World();

		player = new Player();
		player.translate(0.0f, 150, 0.0f);

		guiHandler = new GuiHandler(this);
		getInputHandler().addKeyListener(guiHandler);
		getInputHandler().addMouseListener(guiHandler);

		/*
		 * Setup Gui
		 */
		guiHandler.addComponent(new Button("Wireframe", 10, 100) {

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

		guiHandler.addComponent(new Button("Fly", 10, 140) {

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
		
		// Let's try to load a model
		geometry = ColladaImporter.loadModel("./data/models/Zoey.dae");
		geometryShader = getAssetManager().loadShader("./data/shaders/colorShader");
		geometryTexture = getAssetManager().loadTexture("./data/models/wurm/bodybag.png");
	}

	/**
	 * Get the window ray
	 * 
	 * @return
	 */
	public Ray getWindowRay() {
		float factor = (float) Math.cos(Math.toRadians(getCamera().getPitch()));
		Vector3f forward = new Vector3f();
		forward.x = (float) Math.sin(Math.toRadians(getCamera().getYaw()))
				* factor;
		forward.y = (float) Math.sin(Math.toRadians(-getCamera().getPitch()));
		forward.z = (float) -Math.cos(Math.toRadians(getCamera().getYaw()))
				* factor;
		forward.normalise();
		return new Ray(new Vector3f(getCamera().getPosition().x, getCamera()
				.getPosition().y, getCamera().getPosition().z), forward);
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

		// Update the world
		world.update((int) (player.getPosition().x / Region.REGION_WIDTH),
				(int) (player.getPosition().z / Region.REGION_HEIGHT));

		// Make the camera follow the player's position
		getCamera().setPosition(player.getPosition());

		// Set the camera height to the terrain height
		if (!flying) {
			// TODO: Add jumping and such
			player.translate(0.0f, -player.getPosition().y + 2.0f, 0.0f);
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
				player.translate(
						moveSpeed
								* (float) Math.sin(Math.toRadians(getCamera()
										.getYaw())),
						0.0f,
						-moveSpeed
								* (float) Math.cos(Math.toRadians(getCamera()
										.getYaw())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				player.translate(
						moveSpeed
								* (float) Math.sin(Math.toRadians(getCamera()
										.getYaw() + 90)),
						0.0f,
						-moveSpeed
								* (float) Math.cos(Math.toRadians(getCamera()
										.getYaw() + 90)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				player.translate(
						moveSpeed
								* (float) Math.sin(Math.toRadians(getCamera()
										.getYaw() - 90)),
						0.0f,
						-moveSpeed
								* (float) Math.cos(Math.toRadians(getCamera()
										.getYaw() - 90)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				player.translate(
						-moveSpeed
								* (float) Math.sin(Math.toRadians(getCamera()
										.getYaw())),
						0.0f,
						moveSpeed
								* (float) Math.cos(Math.toRadians(getCamera()
										.getYaw())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				player.translate(0.0f, -0.5f * moveSpeed, 0.0f);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				player.translate(0.0f, 0.5f * moveSpeed, 0.0f);
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
		geometryTexture.bind();
		geometryShader.use();
		geometryShader.setProjectionViewMatrix(getCamera()
				.getProjectionViewMatrix());
		geometryShader.setModelMatrix(getCamera().getModelMatrix());
		geometryShader.setTextureLocation("tColorMap", 0);
		geometry.render();
		
		geometryShader.useNone();
		
		glDisable(GL_CULL_FACE);

		// Draw the selected tile
		if (selectedTile != null) {
			float p1H = world.getTileHeight(
					(int) selectedTile.getTilePosition().x + 1,
					(int) selectedTile.getTilePosition().y) + 0.01f;
			float p2H = world.getTileHeight(
					(int) selectedTile.getTilePosition().x,
					(int) selectedTile.getTilePosition().y) + 0.01f;
			float p3H = world.getTileHeight(
					(int) selectedTile.getTilePosition().x,
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
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new TestGame().start();
	}

}
