package com.alejandroacg.choicebound;

import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ChoiceboundGame extends Game {
    private final PlatformBridge platformBridge;
    private ResourceManager resourceManager;

    public ChoiceboundGame(PlatformBridge platformBridge) {
        this.platformBridge = platformBridge;
    }

    public PlatformBridge getPlatformBridge() {
        return platformBridge;
    }

    @Override
    public void create() {
        resourceManager = new ResourceManager();
        resourceManager.loadSplashAssets();
        while (!resourceManager.update()) {}

        setScreen(new SplashScreen(this, resourceManager));
    }

    @Override
    public void dispose() {
        super.dispose();
        resourceManager.dispose();
    }

    public Screen getCurrentScreen() {
        return getScreen();
    }
}
