package com.example.pbush.modelhelp;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by pbush on 2/28/2018.
 */

public class Triangle {

    private FloatBuffer vertexBuffer, colorBuffer;
    private final int mProgram;
    private final int mBytesPerFloat = 4;
    final String vertexShaderCode =
            "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
                    + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.

                    + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.

                    + "void main()                    \n"     // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    final String fragmentShaderCode =
            "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
                    // precision in the fragment shader.
                    + "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
                    // triangle per fragment.
                    + "void main()                    \n"     // The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = v_Color;     \n"     // Pass the color directly through the pipeline.
                    + "}                              \n";

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mColorHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static final int COORDS_PER_COLOR = 4;

    private float coords[];
    private float colors[];

    public Triangle(float[] cs, float[] color) {
        coords = cs;
        colors = color;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(coords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        ByteBuffer bbColor = ByteBuffer.allocateDirect(colors.length * 4);
        bbColor.order(ByteOrder.nativeOrder());
        colorBuffer = bbColor.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        // Pass in the shader source.
        GLES20.glShaderSource(vertexShaderHandle, vertexShaderCode);
        GLES20.glCompileShader(vertexShaderHandle);
        // Pass in the shader source.
        GLES20.glShaderSource(fragmentShaderHandle, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShaderHandle);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShaderHandle);
        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShaderHandle);

        //Bind attributes
        GLES20.glBindAttribLocation(mProgram, 0, "a_Position");
        GLES20.glBindAttribLocation(mProgram, 1, "a_Color");

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
        int [] ls = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, ls, 0);
        if (ls[0] == 0) {
            throw new RuntimeException("Error creating program.");
        }
        //culling... only put back in if needed
        GLES20.glFrontFace(GLES20.GL_CW);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    public void draw(float[] mvpMatrix) {
        final int vertexCount = coords.length / COORDS_PER_VERTEX;
        final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mColorHandle, COORDS_PER_COLOR,
                GLES20.GL_FLOAT, false, (vertexStride + 4), colorBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
    }

}
