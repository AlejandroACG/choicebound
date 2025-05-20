package com.alejandroacg.choicebound.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class OverlayManager {
    private final Skin skin;

    public OverlayManager(Skin skin) {
        this.skin = skin;
    }

    public Group showOverlay(Stage stage) {
        // Crear un Group para el fondo
        Group overlayGroup = new Group();
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

        // Añadir el Group al Stage
        stage.addActor(overlayGroup);

        // Liberar recursos
        pixmap.dispose();

        return overlayGroup;
    }

    public void hideOverlay(Group overlayGroup) {
        if (overlayGroup != null) {
            overlayGroup.remove();
        }
    }
}
