package org.fractalstudio.engine.gui;

import org.fractalstudio.engine.EngineLogger;
import org.fractalstudio.engine.gui.event.ActionEvent;
import org.fractalstudio.engine.gui.event.EventType;
import org.fractalstudio.engine.gui.event.MouseEvent;
import org.fractalstudio.render.opengl.ImmediateRenderer;
import org.fractalstudio.render.texture.Texture;
import org.fractalstudio.render.texture.TextureLoader;
import org.lwjgl.opengl.GL11;

public class Image extends Component {

	/**
	 * 
	 */
	private Texture imageTexture;

	/**
	 * 
	 */
	private boolean dragging;

	/**
	 * The constructor
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 */
	public Image(String textureLocation, int x, int y) {
		super("Image" + Component.getNextId(), x, y, 0, 0);
		imageTexture = TextureLoader.loadTexture(textureLocation);
		if (imageTexture != null) {
			setSize(imageTexture.getWidth(), imageTexture.getHeight());
		} else {
			EngineLogger.error("Added component " + getName()
					+ " type Image with null texture.");
		}
	}

	/**
	 * The constructor
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 */
	public Image(Texture texture, int x, int y) {
		super("Image" + Component.getNextId(), x, y, texture.getWidth(),
				texture.getHeight());
		this.imageTexture = texture;
	}

	/**
	 * Draw the image
	 */
	@Override
	public void render() {
		if (imageTexture != null) {
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			imageTexture.bind();
			ImmediateRenderer.drawTexturedQuad(getX(), getY(),
					imageTexture.getWidth(), imageTexture.getHeight());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fractalstudio.engine.gui.Component#actionPerformed(org.fractalstudio
	 * .engine.gui.GuiHandler, org.fractalstudio.engine.gui.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(GuiHandler guiHandler, ActionEvent actionEvent) {

		if (actionEvent.getEventType() == EventType.MOUSE_PRESSED) {
			guiHandler.mouseLock(this);
			dragging = true;
		} else if (actionEvent.getEventType() == EventType.MOUSE_RELEASED) {
			guiHandler.mouseRelease(this);
			dragging = false;
		} else if (actionEvent.getEventType() == EventType.MOUSE_MOVED) {
			MouseEvent mouseEvent = (MouseEvent) actionEvent;
			if (dragging) {
				this.translate(mouseEvent.getDX(), mouseEvent.getDY());
			}
		}

	}
}
