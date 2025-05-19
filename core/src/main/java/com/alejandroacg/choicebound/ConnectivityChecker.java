package com.alejandroacg.choicebound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.badlogic.gdx.utils.Align;

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
            // Crear un Group para el mensaje de error
            final Group overlayGroup = new Group();
            overlayGroup.setSize(stage.getWidth(), stage.getHeight());

            // Crear un Pixmap con el color negro semitransparente
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            Color color = skin.getColor("black_semi_transparent");
            pixmap.setColor(color);
            pixmap.fill();

            // Crear un Texture y un Drawable
            Texture texture = new Texture(pixmap);
            TextureRegionDrawable drawable = new TextureRegionDrawable(texture);

            // Fondo negro semitransparente
            Image background = new Image(drawable);
            background.setSize(stage.getWidth(), stage.getHeight());
            overlayGroup.addActor(background);

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
                    overlayGroup.remove();
                }
            });

            stage.addActor(overlayGroup);

            // Liberar recursos
            pixmap.dispose();
        }
        return isConnected;
    }
}
