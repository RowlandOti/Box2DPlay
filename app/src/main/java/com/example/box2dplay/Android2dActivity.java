package com.example.box2dplay;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public final class Android2dActivity extends Activity {
    public static final int TARGET_FPS = 60;

    private CCGLSurfaceView surface;
    private CCScene scene;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        surface = new CCGLSurfaceView(this);

        setContentView(surface);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Attach the OpenGL view to a window
        CCDirector.sharedDirector().attachInView(surface);

        // Show FPS, set false to disable FPS display
        CCDirector.sharedDirector().setDisplayFPS(false);

        // Frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / TARGET_FPS);

        // Make the Scene active
        scene = MainScene.scene();

        CCDirector.sharedDirector().runWithScene(scene);
    }

    @Override
    public void onPause() {
        super.onPause();

        CCDirector.sharedDirector().pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        CCDirector.sharedDirector().resume();
    }

    @Override
    public void onStop() {
        super.onStop();

        CCDirector.sharedDirector().end();
    }
}
