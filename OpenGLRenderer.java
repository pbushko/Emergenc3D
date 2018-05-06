package com.example.pbush.modelhelp;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by pbush on 2/28/2018.
 */

public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private ArrayList prisms = new ArrayList<Triangle>();
    private Square floor;
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];
    public volatile float mAngleX;
    public volatile float mAngleY;
    public static volatile float scaled = -6;

    public float getAngleX() {
        return mAngleX;
    }
    public void setAngleX(float angle) {
        mAngleX = angle;
    }
    public float getAngleY() {
        return mAngleY;
    }
    public void setAngleY(float angle) {
        mAngleY = angle;
    }
    public static float getScaled() {
        return scaled;
    }
    public static void setScaled(float newScale) {
        if ((scaled >= -6 && scaled <=-4) ||(scaled <= -6 && newScale > scaled) || (scaled >= -4 && newScale < scaled))
            scaled = newScale;
    }

    private float colors[] = {
        0.3f, 0.3f, 0.3f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.3f, 0.3f, 0.3f, 1f,
        0.4f, 0.4f, 0.4f, 1f,
        0.2f, 0.2f, 0.2f, 1f,
        0.4f, 0.4f, 0.4f, 1f
    };

    private float userColors[] = {
            0.9f, 0.5f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            0.9f, 0.5f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f,
            0.8f, 0.4f, 0f, 1f,
            1.0f, 0.6f, 0f, 1f
    };

    private float floorColors[] = {
            0.8f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.8f, 1.0f
    };
    private float texCoord[] = {
                    1f, 0f,
                    0f, 1f,
                    0f, 0f,
                    1f, 1f,
                    0f, 1f,
                    1f, 0f
    };

    private float refColors[] = new float[36*4];

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //setting the color to red
        GLES20.glClearColor(0.8f, 0.5f, 0.5f, 1f);

        float w = MainActivity.toDisplay.getWidth();
        float h = MainActivity.toDisplay.getHeight();
        int[] coords = MainActivity.findWalls();
        int coordsLen = coords.length;
        int[] userCoords = MainActivity.getUserCoords();
        int userLen = userCoords.length;
        int[] winCoords = MainActivity.getWindowCoords();
        int windowLen = winCoords.length;
        float ratio = w/h;
        float wallHeight = 0;
        if(ratio >= 1)
            wallHeight = 0.4f;
        else
            wallHeight = 0.2f;
        //inserting the shapes to draw
        for (int i = 0; i < coordsLen; i+=4) {
            float temp[] = setCoords(coords[i]/h, coords[i+1]/h, coords[i+2]/h, coords[i+3]/h, 0f, wallHeight);
            prisms.add(new Triangle(temp, colors));
        }
        for (int i = 0; i < userLen; i+=4) {
            float temp[] = setCoords(userCoords[i]/h, userCoords[i+1]/h, userCoords[i+2]/h,
                    userCoords[i+3]/h, 0.1f, wallHeight);
            prisms.add(new Triangle(temp, userColors));
        }
        for (int i = 0; i < windowLen; i+=4) {
            float temp[] = setCoords(winCoords[i]/h, winCoords[i+1]/h, winCoords[i+2]/h, winCoords[i+3]/h, 0f, wallHeight/4);
            prisms.add(new Triangle(temp, colors));
            temp = setCoords(winCoords[i]/h, winCoords[i+1]/h, winCoords[i+2]/h, winCoords[i+3]/h, 3* wallHeight/2, wallHeight);
            prisms.add(new Triangle(temp, colors));
        }

        float floorCoords[] = {
                        ratio, 0f, wallHeight,
                        0f, 1f, wallHeight,
                        0f, 0f, wallHeight,
                        ratio, 1f, wallHeight,
                        0f, 1f, wallHeight,
                        ratio, 0f, wallHeight
        };
        floor = new Square(floorCoords, floorColors, texCoord);
        //to reference what is the top and left corner of the image
        float refDown[] = setCoords(0f, 0f, 0.02f, 0.2f, wallHeight, wallHeight-0.05f);
        float refRight[] = setCoords(0f, 0f, 0.2f, 0.02f, wallHeight, wallHeight-0.05f);
        prisms.add(new Triangle(refDown, refColors));
        prisms.add(new Triangle(refRight, refColors));

        // Enable depth test
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // Accept fragment if it closer to the camera than the former one
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    //creating coords based on the x and y coords given.
    public float[] setCoords(float x1, float y1, float x2, float y2, float wallHeightStart, float wallHeight) {
        // if the x and y coords are out of bounds, return an empty array
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0) {
            float[] retVal = new float[0];
            return retVal;
        }
        else {
            //using the v notation from:
            //http://doc.qt.io/archives/qt-5.6/qtopengl-cube-example.html
            float coords[] = {
                    x1, y1, wallHeightStart, //front bottom left, v0, v13, v18
                    x1, y2, wallHeightStart, //front bottom right, v1, v9, v19
                    x1, y1, wallHeight, //front top left, v2, v15, v20
                    x1, y2, wallHeight, //front top right, v3, v11, v21
                    x2, y1, wallHeightStart, //back bottom left, v4, v12, v16
                    x2, y2, wallHeightStart, //back bottom right, v5, v8, v17
                    x2, y1, wallHeight, //back top left, v6, v14, v22
                    x2, y2, wallHeight //back top right, v7, v10, v23
            };
            return toDrawCoords(coords);
        }
    }

    //how to order the coords
    public int order[] = {
            0, 1, 2, 3, 3,     // Face 0 -( v0,  v1,  v2,  v3)
            1, 1, 5, 3, 7, 7, // Face 1 - triangle strip ( v4,  v5,  v6,  v7)
            5, 5, 4, 7, 6, 6, // Face 2 - triangle strip ( v8,  v9, v10, v11)
            4, 4, 0, 6, 2, 2, // Face 3 - triangle strip (v12, v13, v14, v15)
            4, 4, 5, 0, 1, 1, // Face 4 - triangle strip (v16, v17, v18, v19)
            2, 2, 3, 6, 7
    };

    //sets up a coords array based on how openGL has to draw a cube
    public float[] toDrawCoords(float[] cs) {
        //just returns coords when it's not the verticies of a rectangle
        if (cs.length != 24)
            return cs;
        float retVal[] = new float[order.length * 3];
        int j = 0;
        for (int i = 0; i < order.length; i++) {
            int k = order[i];
            retVal[j++] = cs[k*3];
            retVal[j++] = cs[k*3 + 1];
            retVal[j++] = cs[k*3 + 2];
        }
        return retVal;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        float[] scratch = new float[16];
        // Clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, scaled, 0.5f, 0.5f, 0f, 0f, -1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        Matrix.setRotateM(mRotationMatrix, 0, mAngleX, 0, 0, 1);
        Matrix.rotateM(mRotationMatrix, 0, mAngleY, 1, 1, 0);
        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Drawing all the shapes:
        for (int i = 0; i < prisms.size(); i++) {
            Triangle temp = (Triangle) prisms.get(i);
            temp.draw(scratch);
        }
        floor.draw(scratch);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}
