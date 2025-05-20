package com.alejandroacg.choicebound.utils;

import com.alejandroacg.choicebound.ui.OverlayManager;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.badlogic.gdx.utils.Align;

public class ConnectivityChecker {
    private final PlatformBridge platformBridge;
    private final Skin skin;
    private final OverlayManager overlayManager;

    public ConnectivityChecker(PlatformBridge platformBridge, Skin skin, OverlayManager overlayManager) {
        this.platformBridge = platformBridge;
        this.skin = skin;
        this.overlayManager = overlayManager;
    }

    public boolean checkConnectivity(Stage stage) {
        boolean isConnected = platformBridge.hasInternetConnection();
        if (!isConnected) {
            // Mostrar la capa semitransparente
            final Group overlayGroup = overlayManager.showOverlay(stage);

            // Mensaje centrado
            Label message = new Label(GameConfig.getString("no_internet_connection"), skin, "error_message");
            message.setWrap(true);
            message.setWidth(stage.getWidth() * 0.8f); // 80% del ancho de la pantalla
            message.setAlignment(Align.center); // Centra el texto dentro del Label
            message.setPosition((stage.getWidth() - message.getWidth()) / 2, stage.getHeight() / 2);
            overlayGroup.addActor(message);

            // Hacer el Group modal y eliminarlo al hacer clic
            overlayGroup.setTouchable(Touchable.enabled);
            overlayGroup.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    overlayManager.hideOverlay(overlayGroup);
                }
            });
        }
        return isConnected;
    }
}
