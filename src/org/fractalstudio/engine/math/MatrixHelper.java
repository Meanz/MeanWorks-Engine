package org.fractalstudio.engine.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class MatrixHelper {

    private static Matrix4f biasMatrix = new Matrix4f();

    static {
        biasMatrix.load((FloatBuffer) BufferUtils.createFloatBuffer(4 * 4).put(new float[]{
                    0.5f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.5f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.5f, 0.0f,
                    0.5f, 0.5f, 0.5f, 1.0f}).flip()); //bias from [-1, 1] to [0, 1]
    }

    public static Matrix4f getBiasMatrix() {
        return biasMatrix;
    }

    /**
     * Set's up the matrices!
     *
     * @param projection
     * @param modelview
     */
    public static void setupMatrices(Matrix4f projection, Matrix4f modelview) {
        //Setup projection matrix
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        glLoadMatrix(projection);
        //Setup modelview matrix
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        glLoadMatrix(modelview);
    }

    /**
     * Calls glLoadMatrix to load a matrix
     *
     * @param matrix
     */
    public static void glLoadMatrix(Matrix4f matrix) {
        GL11.glLoadMatrix(storeMatrix(matrix));
    }

    /**
     * Stores a matrix into a FloatBuffer
     *
     * @param matrix
     * @return
     */
    public static FloatBuffer storeMatrix(Matrix4f matrix) {
        FloatBuffer sMatrix = BufferUtils.createFloatBuffer(4 * 4);
        matrix.store(sMatrix);
        sMatrix.flip();
        return sMatrix;
    }

    /**
     * Gets a matrix from openGL eg: GL_MODELVIEW, GL_PROJECTION, GL_TEXTURE
     *
     * @param pMatrix
     * @return The Matrix stored in a float buffer
     */
    public static FloatBuffer getMatrixf(int pMatrix) {
        FloatBuffer matrix = BufferUtils.createFloatBuffer(4 * 4);
        GL11.glGetFloat(pMatrix, matrix);
        return matrix;
    }

    /**
     * Gets a matrix from openGL eg: GL_MODELVIEW, GL_PROJECTION, GL_TEXTURE
     *
     * @param pMatrix
     * @return The Matrix fully loaded!
     */
    public static Matrix4f getMatrix(int pMatrix) {
        FloatBuffer matrixBuff = BufferUtils.createFloatBuffer(4 * 4);
        GL11.glGetFloat(pMatrix, matrixBuff);
        matrixBuff.flip();
        Matrix4f matrix = new Matrix4f();
        matrix.load(matrixBuff);
        return matrix;
    }

    /**
     * Gets a matrix from openGL eg: GL_MODELVIEW, GL_PROJECTION, GL_TEXTURE And
     * store it into a matrix supplied
     *
     * @param pMatrix
     * @return The Matrix to store the data into
     */
    public static Matrix4f getMatrix(int pMatrix, Matrix4f matrix) {
        matrix.load(getMatrixf(pMatrix));
        return matrix;
    }
}
