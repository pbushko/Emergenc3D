package com.example.pbush.modelhelp;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by pbush on 2/28/2018.
 */

public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private Triangle tri;
    private Triangle tetra;
    private Square square;
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];
    public volatile float mAngleX;
    public volatile float mAngleY;

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

//    private float color[] = {
//            0.8f, 0.0f, 0.0f, 1.0f,
//            0.0f, 0.8f, 0.0f, 1.0f,
//            0.0f, 0.0f, 0.8f, 1.0f,
//            0.8f, 0.8f, 0.0f, 1.0f,
//            0.0f, 0.8f, 0.8f, 1.0f,
//            0.8f, 0.0f, 0.8f, 1.0f
//    };

    private float colors[] = {
            0.8f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.8f, 1.0f,
            0.8f, 0.8f, 0.0f, 1.0f,
            0.0f, 0.8f, 0.8f, 1.0f,
            0.8f, 0.0f, 0.8f, 1.0f
    };

    float cubeCoords[] = {   // in counterclockwise order:
//            -0.5f, -0.5f, 0.0f,   // front bottom left
//            -0.5f,  0.5f, 0.0f,   // front top left
//            0.5f, -0.5f, 0.0f,   // front bottom right
//            0.5f,  0.5f, 0.0f,    // front top right
//            -0.5f, -0.5f, -0.5f,   // back bottom left
//            -0.5f,  0.5f, -0.5f,   // back top left
//            0.5f, -0.5f, -0.5f,   // back bottom right
//            0.5f,  0.5f, -0.5f     //back top right
            -0.5f,  0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            -0.5f, -0.5f, 0.5f,
            -0.5f,  0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //setting the color to red
        GLES20.glClearColor(0.8f, 0.5f, 0.5f, 1f);

        float coords[] = setCoords(0.5f, 0.5f, 0.0f, 0.0f);
        coords = toDrawCoords(coords);
        Log.d("coords", "here are the DrawCoords: " + Arrays.toString(coords) + "\n");

        //setting the shape up
        tetra = new Triangle(coords, colors);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    //creating coords based on the x and y coords given.
    public float[] setCoords(float x1, float y1, float x2, float y2) {
        // if the x and y coords are out of bounds, return an empty array
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0) {
            float[] retVal = new float[0];
            return retVal;
        }
        else {
            //using the v notation from:
            //http://doc.qt.io/archives/qt-5.6/qtopengl-cube-example.html
            float coords[] = {
                    x1, y1, 0f, //front bottom left, v0, v13, v18
                    x2, y2, 0f, //front bottom right, v1, v9, v19
                    x1, y1, 1f, //front top left, v2, v15, v20
                    x2, y2, 1f, //front top right, v3, v11, v21
                    x1, (y1+0.5f), 0f, //back bottom left, v4, v12, v16
                    x2, (y2+0.5f), 0f, //back bottom right, v5, v8, v17
                    x1, (y1+0.5f), 1f, //back top left, v6, v14, v22
                    x2, (y2+0.5f), 1f //back top right, v7, v10, v23
            };
            return coords;
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
        //Log.d("drawCoords", "lengths of retVal and order: " + retVal.length +
        // " " + order.length + "\n");
        int j = 0;
        for (int i = 0; i < order.length; i++) {
            //Log.d("drawCoords", "here is i and j: " + i + " " + j + "\n");
            int k = order[i];
            //Log.d("drawCoords", "here is order[i]: " + k + "\n");
            retVal[j++] = cs[k*3];
            retVal[j++] = cs[k*3 + 1];
            retVal[j++] = cs[k*3 + 2];
            //Log.d("drawCoords", "current retVal:" + Arrays.toString(retVal) + "\n");
        }
        return retVal;
    }

//    //finding the normals of the surfaces from the coords
//    private float[] getNormals() {
//        int numFaces = cubeCoords.length - 2;
//        float[] norms = new float[numFaces * 3];
//        int j = 0;
//        //for every face on the object, find the normal and add to the array
//        for (int i = 0; i < numFaces; i+=9) {
//            //gets the range of
//            float[] temp = calcNorm(Arrays.copyOfRange(cubeCoords,i,i+9));
//            //adding the normal vector to the normal array
//            norms[j++] = temp[0];
//            norms[j++] = temp[1];
//            norms[j++] = temp[2];
//        }
//        return norms;
//    }
//
//    //subtracts two vec3s from each other
//    private float[] subVec3(float[] a, float[] b) {
//        float[] retVal = {(a[0] - b[0]), (a[1] - b[1]), (a[2] - b[2])};
//        return retVal;
//    }
//
//    private float[] calcNorm(float[] tri) {
//        float[] norm = new float[3];
//        float[] p1 = {tri[0], tri[1], tri[2]};
//        float[] p2 = {tri[3], tri[4], tri[5]};
//        float[] p3 = {tri[6], tri[7], tri[8]};
//        float[] u = subVec3(p2, p1);
//        float[] v = subVec3(p3, p1);
//        norm[0] = (u[1] * v[2]) - (u[2] * v[1]);
//        norm[1] = (u[2] * v[0]) - (u[0] * v[1]);
//        norm[2] = (u[0] * v[1]) - (u[1] * v[0]);
//        return norm;
//    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        float[] scratch = new float[16];

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        //Matrix.setIdentityM(mRotationMatrix, 0);
        //Matrix.rotateM(mRotationMatrix, 0, mAngleX, 0, 1, 0);
        //Matrix.rotateM(mRotationMatrix, 0, mAngleY, 1, 0, 0);
        Matrix.setRotateM(mRotationMatrix, 0, mAngleX, 0, 0, 1);
        Matrix.rotateM(mRotationMatrix, 0, mAngleY, 0, 1, 0);
        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

//        Log.d("coords", "here are the DrawCoords: " + Arrays.toString(coords) + "\n");
//        Log.d("color", "make sure the coords and colors are the same length: " +
//            coords.length + " " + color.length + "\n");
        // Draw shape
        tetra.draw(scratch);
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
