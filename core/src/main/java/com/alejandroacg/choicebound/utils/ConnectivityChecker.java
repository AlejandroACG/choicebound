package com.alejandroacg.choicebound.utils;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.alejandroacg.choicebound.screens.HomeScreen;

public class ConnectivityChecker {
    private final ChoiceboundGame game;

    public ConnectivityChecker(ChoiceboundGame game) {
        this.game = game;
    }

    public boolean checkConnectivity(Stage stage) {
        boolean isConnected = game.getPlatformBridge().hasInternetConnection();
        if (!isConnected) {
            game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("no_internet_connection"));
        }
        return isConnected;
    }

    public boolean checkConnectivityWithRedirect() {
        boolean isConnected = game.getPlatformBridge().hasInternetConnection();
        if (!isConnected) {
            game.setScreen(new HomeScreen(game));
        }

        return isConnected;
    }
}
