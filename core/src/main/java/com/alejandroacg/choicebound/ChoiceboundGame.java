package com.alejandroacg.choicebound;

import com.alejandroacg.choicebound.data.*;
import com.alejandroacg.choicebound.interfaces.DatabaseInterface;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.alejandroacg.choicebound.resources.ResourceManager;
import com.alejandroacg.choicebound.screens.SplashScreen;
import com.alejandroacg.choicebound.ui.OverlayManager;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.alejandroacg.choicebound.utils.MusicManager;
import com.alejandroacg.choicebound.utils.ConnectivityChecker;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import lombok.Getter;
import lombok.Setter;

public class ChoiceboundGame extends Game {
    @Getter
    private final PlatformBridge platformBridge;
    @Getter
    private final DatabaseInterface database;
    @Getter
    private ResourceManager resourceManager;
    @Getter
    private Skin skin;
    @Getter
    private OverlayManager overlayManager;
    @Getter
    private MusicManager musicManager;
    @Getter
    private Screen currentScreen;
    private Screen lastScreen;
    private boolean hasRenderedFirstFrame;
    @Getter @Setter
    private LocalUser localUser;
    @Getter
    private final UserDataManager userDataManager;
    @Getter
    private final AdventureDataManager adventureDataManager;
    @Getter
    private final NodeDataManager nodeDataManager;
    @Getter
    private ConnectivityChecker connectivityChecker;

    public ChoiceboundGame(PlatformBridge platformBridge, DatabaseInterface database) {
        this.platformBridge = platformBridge;
        this.database = database;
        this.localUser = new LocalUser();
        this.userDataManager = new UserDataManager(this);
        this.adventureDataManager = new AdventureDataManager(this);
        this.nodeDataManager = new NodeDataManager(this);
    }

    @Override
    public void create() {
        resourceManager = new ResourceManager();
        resourceManager.loadSplashAssets();
        resourceManager.finishLoading();
        skin = new Skin(Gdx.files.internal("ui/skin.json"));
        overlayManager = new OverlayManager(skin, this);
        connectivityChecker = new ConnectivityChecker(this);
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
        hasRenderedFirstFrame = false;
        super.setScreen(screen);
    }

    @Override
    public void render() {
        if (lastScreen != null && hasRenderedFirstFrame) {
            lastScreen.dispose();
            lastScreen = null;
        }
        super.render();
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

    public void clearLocalUser() {
        this.localUser = new LocalUser();
    }

    public void signOut() {
        platformBridge.signOut();
        Gdx.app.postRunnable(() -> setScreen(new SplashScreen(this)));
    }
}
