package com.alejandroacg.choicebound;

import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.alejandroacg.choicebound.resources.ResourceManager;
import com.alejandroacg.choicebound.screens.SplashScreen;
import com.alejandroacg.choicebound.ui.OverlayManager;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ChoiceboundGame extends Game {
    private final PlatformBridge platformBridge;
    private ResourceManager resourceManager;
    private Skin skin;
    private OverlayManager overlayManager;

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
        skin = new Skin(Gdx.files.internal("ui/skin.json"));
        overlayManager = new OverlayManager(skin);
        GameConfig.initialize();
        setScreen(new SplashScreen(this, resourceManager));
    }

    @Override
    public void dispose() {
        super.dispose();
        resourceManager.dispose();
        skin.dispose();
    }

    public Screen getCurrentScreen() {
        return getScreen();
    }

    public OverlayManager getOverlayManager() {
        return overlayManager;
    }
}
