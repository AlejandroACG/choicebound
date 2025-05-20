package com.alejandroacg.choicebound;

import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.alejandroacg.choicebound.resources.ResourceManager;
import com.alejandroacg.choicebound.screens.SplashScreen;
import com.alejandroacg.choicebound.ui.OverlayManager;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.alejandroacg.choicebound.utils.MusicManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ChoiceboundGame extends Game {
    private final PlatformBridge platformBridge;
    private ResourceManager resourceManager;
    private Skin skin;
    private OverlayManager overlayManager;
    private MusicManager musicManager;
    private Screen currentScreen;
    private Screen lastScreen;
    private boolean hasRenderedFirstFrame; // Para saber si la nueva pantalla ha renderizado
    private UserInfo userInfo;

    public ChoiceboundGame(PlatformBridge platformBridge) {
        this.platformBridge = platformBridge;
        this.userInfo = new UserInfo("", "");
    }

    public PlatformBridge getPlatformBridge() {
        return platformBridge;
    }

    @Override
    public void create() {
        resourceManager = new ResourceManager();
        resourceManager.loadSplashAssets();
        resourceManager.finishLoading();
        skin = new Skin(Gdx.files.internal("ui/skin.json"));
        overlayManager = new OverlayManager(skin);
        musicManager = new MusicManager(resourceManager);
        GameConfig.initialize();
        setScreen(new SplashScreen(this));
    }

    @Override
    public void setScreen(Screen screen) {
        if (currentScreen != null) {
            lastScreen = currentScreen;
            currentScreen.hide();
        }
        currentScreen = screen;
        hasRenderedFirstFrame = false; // Resetear para la nueva pantalla
        super.setScreen(screen);
    }

    @Override
    public void render() {
        // Si hay una pantalla anterior y la nueva ya ha renderizado su primer frame, liberamos la anterior
        if (lastScreen != null && hasRenderedFirstFrame) {
            lastScreen.dispose();
            lastScreen = null;
        }

        // Renderizar la pantalla actual
        super.render();

        // Marcar que la pantalla actual ha renderizado su primer frame
        hasRenderedFirstFrame = true;
    }

    @Override
    public void dispose() {
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen.dispose();
            currentScreen = null;
        }
        if (lastScreen != null) {
            lastScreen.dispose();
            lastScreen = null;
        }
        super.dispose();
        resourceManager.dispose();
        skin.dispose();
        musicManager.dispose();
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public OverlayManager getOverlayManager() {
        return overlayManager;
    }

    public Skin getSkin() {
        return skin;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setUserInfo(String displayName, String uid) {
        this.userInfo = new UserInfo(displayName, uid);
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void clearUserInfo() {
        this.userInfo = new UserInfo("", "");
    }
}
