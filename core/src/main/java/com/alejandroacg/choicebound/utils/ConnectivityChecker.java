package com.alejandroacg.choicebound.utils;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.alejandroacg.choicebound.ui.OverlayManager;

public class ConnectivityChecker {
    private final PlatformBridge platformBridge;
    private final OverlayManager overlayManager;

    public ConnectivityChecker(PlatformBridge platformBridge, OverlayManager overlayManager) {
        this.platformBridge = platformBridge;
        this.overlayManager = overlayManager;
    }

    public boolean checkConnectivity(Stage stage) {
        boolean isConnected = platformBridge.hasInternetConnection();
        if (!isConnected) {
            overlayManager.showMessageOverlay(stage, GameConfig.getString("no_internet_connection"));
        }
        return isConnected;
    }
}
