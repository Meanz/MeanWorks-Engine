package org.meanworks.engine.gui.impl;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;

import java.io.FileReader;
import java.util.ArrayList;

import org.meanworks.engine.EngineConfig;
import org.meanworks.engine.core.Application;
import org.meanworks.engine.gui.Component;
import org.meanworks.engine.render.FontRenderer;
import org.meanworks.engine.render.texture.Texture;

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
public class Console extends Component {

	/*
	 * Whether or not the console is active
	 */
	private boolean activated = false;

	/*
	 * The current console input
	 */
	private String currentInput = "";

	/*
	 * The lines
	 */
	private ArrayList<String> lines = new ArrayList<String>();

	/*
	 * 
	 */
	private String lastCommand = "";

	/*
	 * The console bg
	 */
	private Texture consoleLogo;

	/**
	 * Construct a new console
	 */
	public Console() {
		super("engineConsole", 0, 0, Application.getApplication().getWindow()
				.getWidth(), 256);

		print("MeanWorks Engine v " + EngineConfig.MW_VERSION + ".");
	}

	/**
	 * Called to handle on key presses!
	 */
	public boolean onKeyDown(int key) {
		if (key == 0) {
			activated = !activated;
		}

		if (activated) {
			if (key == 57) {
				currentInput += " ";
			} else if (key == 28) {
				doCommand(currentInput);
				lastCommand = currentInput;
				currentInput = "";
			} else if (key == 14) {
				if (currentInput.length() > 0) {
					currentInput = currentInput.substring(0,
							currentInput.length() - 1);
				}
			} else if (key == 200) {
				currentInput = lastCommand;
			} else {
				if (Application.getApplication().getInputHandler()
						.isValidKey(key)) {
					currentInput += Application.getApplication()
							.getInputHandler().keyToChar(key);
				}
			}

			System.err.println("Key: " + key);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Parse the given command
	 * 
	 * @param command
	 */
	public void doCommand(String command) {
		if (command == null) {
			print("doCommand null input");
			return;
		}
		// Parse command
		String[] tokens = command.split(" ");
		String firstToken = null;
		if (tokens.length == 0) {
			firstToken = command;
		} else {
			firstToken = tokens[0];
		}
		if (firstToken.equals("say")) {
			print(command.substring(4));
		} else if (firstToken.startsWith("exit")) {
			Application.getApplication().stop(); // Send stop to application
		} else if (firstToken.startsWith("js")) {

			try {

				FileReader fr = new FileReader(tokens[1]);

				Application.getApplication().getScriptHandler().evalFile(fr);

			} catch (Exception ex) {
				ex.printStackTrace();
				print(ex.getMessage());
			}

		} else {
			print("Unknown command " + firstToken);
		}
	}

	/**
	 * Print the given object to the console
	 * 
	 * @param obj
	 */
	public void print(Object obj) {

		/*
		 * Let's do some parsing
		 */
		String s = obj == null ? "null" : "" + obj;

		if (s.contains("\n")) {
			String[] nls = s.split("\n");
			for (String nl : nls) {
				lines.add(nl == null ? "null" : nl);
			}
		} else {
			lines.add(s);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meanworks.engine.gui.Component#render()
	 */
	@Override
	public void render() {
		// TODO Auto-generated method stub
		if (activated) {

			// Render dimmed background!
			glDisable(GL_TEXTURE_2D);

			// Draw background part of button
			glBegin(GL_QUADS);
			{
				glColor4f(0.6f, 0.6f, 0.6f, 1.0f);
				drawQuad(getX(), getY(), getWidth(), getHeight());
			}
			glEnd();

			// Render text
			// Number of lines allowed to render = (256 - 6) / 15 = 15

			// Draw the font
			glColor3f(1.0f, 1.0f, 1.0f);

			int maxLines = 14;
			int end = lines.size() > maxLines ? lines.size() - maxLines : lines
					.size() + 1;
			int off = 0;
			for (int i = lines.size() - 1; i > lines.size() - end; i--) {

				FontRenderer.arial14_white.drawString(lines.get(i),
						getX() + 10, getY() - 6 + ((maxLines - (off++)) * 15));
			}

			FontRenderer.arial14_white.drawString("cmd> " + currentInput
					+ " | ", getX() + 10, 235);

			// Draw logo
			if (consoleLogo == null) {
				consoleLogo = Application.getApplication().getAssetManager()
						.loadTexture("./data/logo/meanworks.png");
			}
			paintImage(consoleLogo, getWidth() - 500, getY() + 5, 500, 250);
		}
	}

}
