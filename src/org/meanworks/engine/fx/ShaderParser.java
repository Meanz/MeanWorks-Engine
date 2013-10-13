package org.meanworks.engine.fx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.meanworks.engine.EngineLogger;
import org.meanworks.render.opengl.shader.Shader;
import org.meanworks.render.opengl.shader.Shader.ShaderType;
import org.meanworks.render.opengl.shader.ShaderProgram;

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
public class ShaderParser {

	/**
	 * Reads a file
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static LinkedList<String> readFile(InputStream is)
			throws IOException {
		LinkedList<String> lines = new LinkedList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = "";
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}

	/**
	 * Reads a file
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static LinkedList<String> readFile(String fileName)
			throws IOException {
		return readFile(new FileInputStream(new File(fileName)));
	}

	/**
	 * Parses the shader from the given input stream
	 * 
	 * @param is
	 * @return
	 */
	public static ShaderProgram parseShader(ShaderProgram program,
			String basePath, InputStream is) throws IOException,
			ShaderParseException {
		return parseShader(program, basePath, readFile(is));
	}

	/**
	 * Parses the shader from the given input stream
	 * 
	 * @param is
	 * @return
	 */
	public static ShaderProgram parseShader(ShaderProgram program,
			String basePath, String fileName) throws IOException,
			ShaderParseException {
		return parseShader(program, basePath, readFile(fileName));
	}

	/**
	 * Parses the shader from the given list of strings
	 * 
	 * @param lines
	 * @return
	 */
	public static ShaderProgram parseShader(ShaderProgram program,
			String basePath, LinkedList<String> lines)
			throws ShaderParseException {

		String version = null;
		StringBuffer fragSource = new StringBuffer();
		StringBuffer vertSource = new StringBuffer();

		boolean parsingFrag = false;
		boolean parsingVert = false;

		int lineNo = 0;
		for (String line : lines) {
			if (line.startsWith("#version")) {
				if (version != null) {
					throw new ShaderParseException(
							"Duplicate shader version at line " + lineNo);
				}
				version = line;
				fragSource.append(version + "\n");
				vertSource.append(version + "\n");
			} else if (line.startsWith("#frag")) {
				if (parsingVert) {
					throw new ShaderParseException("Expected #end at line "
							+ lineNo);
				}
				if (version == null) {
					throw new ShaderParseException(
							"Expected #version before #frag at line " + lineNo);
				}
				parsingFrag = true;
			} else if (line.startsWith("#vert")) {
				if (parsingFrag) {
					throw new ShaderParseException("Expected #end at line "
							+ lineNo);
				}
				if (version == null) {
					throw new ShaderParseException(
							"Expected #version before #vert at line " + lineNo);
				}
				parsingVert = true;
			} else if (line.startsWith("#end")) {
				parsingFrag = false;
				parsingVert = false;
			} else if (line.startsWith("#include")) {
				try {
					String includeFile = line.split("#include ")[1];
					// Read the file
					LinkedList<String> libLines = readFile(basePath
							+ includeFile + ".shader");

					String libSource = parseLib(includeFile, libLines);
					if (parsingFrag) {
						fragSource.append(libSource);
					} else if (parsingVert) {
						vertSource.append(libSource);
					} else {
						throw new ShaderParseException(
								"#include before #frag or #vert");
					}
				} catch (ShaderParseException | IOException ex) {
					throw new ShaderParseException(ex.getMessage()
							+ " -- line no " + lineNo);
				}
			} else {
				if (parsingFrag) {
					fragSource.append(line + "\n");
				}
				if (parsingVert) {
					vertSource.append(line + "\n");
				}
			}

			lineNo++;
		}

		// Post process checks
		if (version == null) {
			throw new ShaderParseException("No shader version specified.");
		}

		if (parsingFrag || parsingVert) {
			throw new ShaderParseException("Expected #end at line " + lineNo);
		}

		/*
		 * Create vertex shader
		 */
		Shader vertexShader = new Shader(ShaderType.VERTEX_SHADER);
		if (!vertexShader.create()) {
			return program;
		}
		vertexShader.setShaderData(vertSource.toString());

		/*
		 * Create fragment shader
		 */
		Shader fragmentShader = new Shader(ShaderType.FRAGMENT_SHADER);
		if (!fragmentShader.create()) {
			vertexShader.delete();
			return program;
		}
		fragmentShader.setShaderData(fragSource.toString());

		// If we have come this far let's go on to
		// fixing up the shader
		LinkedList<Shader> shaders = program.detatchAll();

		program.attach(vertexShader);
		program.attach(fragmentShader);

		if (!program.compile()) {
			vertexShader.delete();
			fragmentShader.delete();
			program.detatchAll();
			if (shaders.size() > 0) {
				for (Shader shader : shaders) {
					program.attach(shader);
				}
				if (!program.compile()) {
					EngineLogger
							.error("Something odd happened here, what now ?");
				}
			}
		} else {
			// Delete the old shaders
			for (Shader shader : shaders) {
				shader.delete();
			}
		}

		return program;
	}

	/**
	 * Parses a lib file
	 * 
	 * @param lines
	 * @return
	 */
	private static String parseLib(String libFile, LinkedList<String> lines)
			throws ShaderParseException {

		StringBuffer libSource = new StringBuffer();
		boolean parsingLib = false;
		boolean didParseLib = false;

		int lineNo = 0;
		for (String line : lines) {
			if (line.startsWith("#lib")) {
				parsingLib = true;
			} else if (line.startsWith("#end")) {
				if (!parsingLib) {
					throw new ShaderParseException(
							"Expected #lib before #end in " + libFile
									+ " at line " + lineNo);
				}
				parsingLib = false;
				didParseLib = true;
			} else {
				if (parsingLib) {
					libSource.append(line + "\n");
				}
			}
			lineNo++;
		}

		if (!didParseLib) {
			throw new ShaderParseException("Expected #lib in lib file "
					+ libFile + " at line " + lineNo);
		}

		return libSource.toString();
	}
}
