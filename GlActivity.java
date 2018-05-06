package com.example.pbush.modelhelp;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

/**
 * Created by pbush on 3/14/2018.
 */



public class GlActivity extends Activity{

    private GLSurfaceView openGLView;
    private float mScaleFactor = 1.f;
    public void setScaledZoomIn(View view) {
        float curScale = OpenGLRenderer.getScaled();
        OpenGLRenderer.setScaled((curScale += mScaleFactor));
    }
    public void setScaledZoomOut(View view) {
        float curScale = OpenGLRenderer.getScaled();
        OpenGLRenderer.setScaled((curScale -= mScaleFactor));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_gl_view);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        openGLView = findViewById(R.id.openGLView);
    }
}
