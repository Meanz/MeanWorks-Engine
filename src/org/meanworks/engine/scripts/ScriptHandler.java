package org.meanworks.engine.scripts;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.meanworks.engine.core.Application;

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
public class ScriptHandler {

	/*
	 * The java script engine
	 */
	private ScriptEngine js;
	/*
	 * The java script bindings
	 */
	private Bindings jsBindings;

	/**
	 * Construct a new ScriptHandler
	 */
	public ScriptHandler() {
		js = new ScriptEngineManager().getEngineByName("javascript");
		exportJSObjects();
		// Prints "-1.0" to the standard output stream.
	}

	/**
	 * Evaluate JavaScript code
	 * 
	 * @param code
	 * @throws ScriptException
	 */
	public void evalCode(String code) throws ScriptException {
		js.eval(code, jsBindings);
	}

	/**
	 * Evaluate a JavaScript file
	 * 
	 * @param file
	 * @throws ScriptException
	 */
	public void evalFile(Reader file) throws ScriptException {
		js.eval(file, jsBindings);
	}

	/**
	 * Export the JavaScript objects to the bindings
	 */
	public void exportJSObjects() {
		jsBindings = js.getBindings(ScriptContext.ENGINE_SCOPE);
		jsBindings.put("console", Application.getApplication().getConsole());
	}

}
