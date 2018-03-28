package com.example.pbush.modelhelp;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
     * Created by pbush on 2/28/2018.
     */

    public class OpenGLView extends GLSurfaceView {
        private final OpenGLRenderer mRenderer;

        public OpenGLView(Context context, AttributeSet attrs, int i){
            super(context);
            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);
            //setPreserveEGLContextOnPause(true);

            mRenderer = new OpenGLRenderer();
            // Set the Renderer for drawing on the GLSurfaceView
            setRenderer(mRenderer);
            // Render the view only when there is a change in the drawing data
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        public OpenGLView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private float mPreviousX;
        private float mPreviousY;

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1 ;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1 ;
                    }

                    mRenderer.setAngleX(
                            mRenderer.getAngleX() +
                                    (dx * TOUCH_SCALE_FACTOR));
                    mRenderer.setAngleY(mRenderer.getAngleY()
                        + (dy * TOUCH_SCALE_FACTOR));
                    requestRender();
            }

            mPreviousX = x;
            mPreviousY = y;
            return true;
        }

    }

