package com.alejandroacg.choicebound;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;

public class ConnectivityChecker {
    private final PlatformBridge platformBridge;
    private final Skin skin;

    public ConnectivityChecker(PlatformBridge platformBridge, Skin skin) {
        this.platformBridge = platformBridge;
        this.skin = skin;
    }

    public boolean checkConnectivity(Stage stage) {
        boolean isConnected = platformBridge.hasInternetConnection();
        if (!isConnected) {
            Dialog dialog = new Dialog("", skin) {
                @Override
                protected void result(Object object) {
                    remove();
                }
            };
            dialog.text(GameConfig.getString("no_internet_connection"));
            dialog.button("OK");
            dialog.setModal(true);
            dialog.show(stage);
        }
        return isConnected;
    }
}
