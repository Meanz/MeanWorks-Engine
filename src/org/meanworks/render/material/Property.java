package org.meanworks.render.material;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.meanworks.engine.EngineLogger;
import org.meanworks.engine.math.MatrixHelper;
import org.meanworks.render.opengl.shader.ShaderProgram;

/**
 * 
 * @author meanz
 * 
 */
public class Property {

	/**
	 * The type of property
	 * 
	 * @author meanz
	 * 
	 */
	public enum PropertyType {

		PROP_BOOL,
		PROP_INT,
		PROP_FLOAT,
		PROP_FLOAT_ARRAY,
		PROP_INT_ARRAY,
		PROP_BOOL_ARRAY,
		PROP_MAT4,
		PROP_MAT4_ARRAY,
		PROP_VEC2i,
		PROP_VEC3i,
		PROP_VEC4i,
		PROP_VEC2f,
		PROP_VEC3f,
		PROP_VEC4f,

	}

	/**
	 * The type of property
	 */
	private PropertyType propertyType;
	/**
	 * The value of this property
	 */
	private Object value;

	/**
	 * Propert Constructor
	 * 
	 * @param propertyType
	 * @param value
	 */
	public Property(Object value) {
		if (value instanceof Boolean) {
			propertyType = PropertyType.PROP_BOOL;
		} else if (value instanceof Integer) {
			propertyType = PropertyType.PROP_INT;
		} else if (value instanceof Float) {
			propertyType = PropertyType.PROP_FLOAT;
		} else if (value instanceof FloatBuffer) {
			propertyType = PropertyType.PROP_FLOAT_ARRAY;
		} else if (value instanceof Float[]) {
			Float[] _value = (Float[]) value;
			FloatBuffer _newValue = BufferUtils
					.createFloatBuffer(_value.length);
			for (int i = 0; i < _value.length; i++) {
				_newValue.put((float) _value[i]);
			}
			_newValue.flip();
			value = _newValue;
			propertyType = PropertyType.PROP_FLOAT_ARRAY;
		} else if (value instanceof Integer[]) {
			Integer[] _value = (Integer[]) value;
			IntBuffer _newValue = BufferUtils.createIntBuffer(_value.length);
			for (int i = 0; i < _value.length; i++) {
				_newValue.put((int) _value[i]);
			}
			_newValue.flip();
			value = _newValue;
			propertyType = PropertyType.PROP_INT_ARRAY;
		} else if (value instanceof Vector2f) {
			propertyType = PropertyType.PROP_VEC2f;
		} else if (value instanceof Vector3f) {
			propertyType = PropertyType.PROP_VEC3f;
		} else if (value instanceof Vector4f) {
			propertyType = PropertyType.PROP_VEC4f;
		} else if (value instanceof Matrix4f) {
			propertyType = PropertyType.PROP_MAT4;
		} else if (value instanceof Matrix4f[]) {
			propertyType = PropertyType.PROP_MAT4_ARRAY;
		} else {
			EngineLogger.warning("Unknown Property type " + value.toString());
		}
		this.value = value;
	}

	/**
	 * Send this property to the shader
	 * 
	 * @param location
	 * @param program
	 */
	public void sendToShader(int location, ShaderProgram program) {
		if (propertyType == PropertyType.PROP_BOOL) {
			ARBShaderObjects.glUniform1iARB(location,
					((boolean) value) == true ? 1 : 0);
		} else if (propertyType == PropertyType.PROP_INT) {
			ARBShaderObjects.glUniform1iARB(location, (int) value);
		} else if (propertyType == PropertyType.PROP_FLOAT_ARRAY) {
			ARBShaderObjects.glUniform1ARB(location, (FloatBuffer) value);
		} else if (propertyType == PropertyType.PROP_VEC2f) {
			ARBShaderObjects.glUniform2fARB(location, ((Vector2f) value).x,
					((Vector2f) value).y);
		} else if (propertyType == PropertyType.PROP_VEC3f) {
			ARBShaderObjects.glUniform3fARB(location, ((Vector3f) value).x,
					((Vector3f) value).y, ((Vector3f) value).z);
		} else if (propertyType == PropertyType.PROP_VEC4f) {
			ARBShaderObjects.glUniform4fARB(location, ((Vector4f) value).x,
					((Vector4f) value).y, ((Vector4f) value).z,
					((Vector4f) value).w);
		} else if (propertyType == PropertyType.PROP_MAT4) {
			ARBShaderObjects.glUniformMatrix4ARB(location, false,
					MatrixHelper.storeMatrix(((Matrix4f) value)));
		} else if (propertyType == PropertyType.PROP_MAT4_ARRAY) {
			// This is going to be a pain in the ass, but let's do it n'e ways
			// Holy crap if this works!
			Matrix4f[] mats = (Matrix4f[]) value;
			FloatBuffer fb = BufferUtils.createFloatBuffer(16 * mats.length);
			for (int i = 0; i < mats.length; i++) {
				mats[i].store(fb);
			}
			fb.flip();
			ARBShaderObjects.glUniformMatrix4ARB(location, false, fb);
		} else {
			// We don't want to do anything here
		}
	}

}
