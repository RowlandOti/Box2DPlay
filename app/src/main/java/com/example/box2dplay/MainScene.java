package com.example.box2dplay;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;

public final class MainScene extends CCLayer {
    public static CCScene scene() {
        /*
         * Create the scene for this layer
         */
        CCScene scene = CCScene.node();

        scene.addChild(new MainLayer());

        return scene;
    }
}
