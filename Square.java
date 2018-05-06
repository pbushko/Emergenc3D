package com.example.pbush.modelhelp;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by pbush on 2/28/2018.
 */

public class Square {

    private FloatBuffer vertexBuffer, colorBuffer, texBuffer;
    private final int mProgram;
    private final int mBytesPerFloat = 4;
    final String vertexShaderCode =
            "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
                    + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.
                    + "attribute vec2 a_TexCoord;     \n"     // Per-vertex texture coordinate info

                    + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.
                    + "varying vec2 v_TexCoord;       \n"     //to be passed into the frag shader

                    + "void main()                    \n"     // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
                    + "   v_TexCoord = a_TexCoord;    \n"
                    + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    final String fragmentShaderCode =
            "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
                    + "uniform sampler2D u_Texture;   \n" //the input texture
                    + "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
                    + "varying vec2 v_TexCoord;       \n"
                    + "void main()                    \n"     // The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = (v_Color * texture2D(u_Texture, v_TexCoord));     \n"
                    //+ " gl_FragColor = v_Color;        \n"
                    + "}                              \n";


    // Use to access and set the view transformation
    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mColorHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordHandle;
    private int mTextureDataHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static final int COORDS_PER_COLOR = 4;

    private float coords[];
    private float colors[];
    private float texCoords[];

    private static int loadTex() {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
            GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, MainActivity.toDisplay, 0);
        }
        else
            throw new RuntimeException("Error loading texture.");

        return textureHandle[0];
    }

    public Square(float[] cs, float[] color, float[] tex) {
        coords = cs;
        colors = color;
        texCoords = tex;

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

        ByteBuffer bbTex = ByteBuffer.allocateDirect(texCoords.length * 4);
        bbTex.order(ByteOrder.nativeOrder());
        texBuffer = bbTex.asFloatBuffer();
        texBuffer.put(texCoords);
        texBuffer.position(0);

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
        GLES20.glBindAttribLocation(mProgram, 2, "a_TexCoord");

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
        int [] ls = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, ls, 0);
        if (ls[0] == 0) {
            throw new RuntimeException("Error creating program.");
        }

        mTextureDataHandle = loadTex();
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
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoord");

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

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

        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false,
                0, texBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }

}
