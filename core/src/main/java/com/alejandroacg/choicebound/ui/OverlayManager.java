package com.alejandroacg.choicebound.ui;

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
import com.badlogic.gdx.utils.Align;

public class OverlayManager {
    private final Skin skin;

    public OverlayManager(Skin skin) {
        this.skin = skin;
    }

    public Group showOverlay(Stage stage) {
        Group overlayGroup = new Group();
        overlayGroup.setSize(stage.getWidth(), stage.getHeight());

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        Color color = skin.getColor("black_semi_transparent");
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);

        Image background = new Image(drawable);
        background.setSize(stage.getWidth(), stage.getHeight());
        overlayGroup.addActor(background);

        stage.addActor(overlayGroup);

        pixmap.dispose();

        return overlayGroup;
    }

    public void hideOverlay(Group overlayGroup) {
        if (overlayGroup != null) {
            overlayGroup.remove();
        }
    }

    public void showMessageOverlay(Stage stage, String messageText) {
        Group overlayGroup = showOverlay(stage);

        Label message = new Label(messageText, skin, "overlay_message");
        message.setWrap(true);
        message.setWidth(stage.getWidth() * 0.8f);
        message.setAlignment(Align.center);
        message.setPosition((stage.getWidth() - message.getWidth()) / 2, stage.getHeight() / 2);
        overlayGroup.addActor(message);

        overlayGroup.setTouchable(Touchable.enabled);
        overlayGroup.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideOverlay(overlayGroup);
            }
        });
    }
}
