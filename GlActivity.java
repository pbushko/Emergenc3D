package com.example.pbush.modelhelp;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * Created by pbush on 3/14/2018.
 */



public class GlActivity extends Activity{

    private GLSurfaceView openGLView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_gl_view);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        openGLView = findViewById(R.id.openGLView);
    }
}
