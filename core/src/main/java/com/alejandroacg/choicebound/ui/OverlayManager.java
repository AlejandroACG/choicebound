package com.alejandroacg.choicebound.ui;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class OverlayManager {
    private final Skin skin;
    private final ChoiceboundGame game;

    public OverlayManager(Skin skin, ChoiceboundGame game) {
        this.skin = skin;
        this.game = game;
    }

    public Group showOverlay(Stage stage) {
        Group overlayGroup = new Group();
        overlayGroup.setSize(stage.getWidth(), stage.getHeight());

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        Color color = skin.getColor("black_semi_transparent");
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));

        Image background = new Image(drawable);
        background.setSize(stage.getWidth(), stage.getHeight());
        overlayGroup.addActor(background);

        stage.addActor(overlayGroup);

        pixmap.dispose();

        return overlayGroup;
    }

    public Group showLoadingOverlay(Stage stage) {
        Group overlayGroup = showOverlay(stage);

        // Añadir círculo giratorio en el centro
        TextureRegionDrawable loadingCircleDrawable = new TextureRegionDrawable(
            game.getResourceManager().getAtlas("ui_pre_loaded").findRegion("loading_circle"));
        Image loadingCircle = new Image(loadingCircleDrawable);
        loadingCircle.setSize(200, 200);
        loadingCircle.setPosition(
            (stage.getWidth() - loadingCircle.getWidth()) / 2,
            (stage.getHeight() - loadingCircle.getHeight()) / 2
        );
        loadingCircle.setOrigin(Align.center);
        loadingCircle.addAction(Actions.forever(Actions.rotateBy(360, 1)));
        overlayGroup.addActor(loadingCircle);

        return overlayGroup;
    }

    public void hideOverlay(Group overlayGroup) {
        if (overlayGroup != null) {
            overlayGroup.remove();
        }
    }

    public void showMessageOverlay(Stage stage, String messageText) {
        Group overlayGroup = showOverlay(stage);

        // Fuente protegida
        Label.LabelStyle baseStyle = skin.get("overlay_message", Label.LabelStyle.class);
        BitmapFont safeFont = new BitmapFont(baseStyle.font.getData(), baseStyle.font.getRegions(), baseStyle.font.usesIntegerPositions());
        safeFont.getData().setScale(1f);
        Label.LabelStyle protectedStyle = new Label.LabelStyle(safeFont, baseStyle.fontColor);

        // Crear Label del mensaje
        Label message = new Label(messageText, protectedStyle);
        message.setWrap(true);
        message.setAlignment(Align.center);

        float maxTextWidth = stage.getWidth() * 0.9f;
        message.setWidth(maxTextWidth);
        message.layout(); // calcular la altura real del texto según el contenido

        float textHeight = message.getPrefHeight(); // altura real del contenido
        float paddingY = textHeight * 0.3f;

        float bgWidth = maxTextWidth + 20f; // dejar un poco de margen horizontal
        float bgHeight = textHeight + paddingY * 2;

        float bgX = (stage.getWidth() - bgWidth) / 2;
        float bgY = stage.getHeight() * 0.5f;

        // Crear imagen decorativa de fondo desde intro.atlas
        TextureRegionDrawable messageBgDrawable = new TextureRegionDrawable(
            game.getResourceManager().getAtlas("ui_pre_loaded").findRegion("message_box_bg"));

        Image messageBg = new Image(messageBgDrawable);
        messageBg.setBounds(bgX, bgY, bgWidth, bgHeight);
        overlayGroup.addActor(messageBg); // fondo primero

        // Posicionar el mensaje centrado sobre el fondo con padding vertical reducido
        message.setPosition(
            (stage.getWidth() - message.getWidth()) / 2,
            bgY + paddingY
        );
        overlayGroup.addActor(message); // mensaje encima del fondo

        overlayGroup.setTouchable(Touchable.enabled);
        overlayGroup.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideOverlay(overlayGroup);
            }
        });
    }
}
