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
    private Group loadingOverlay;

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
        if (loadingOverlay == null) {
            loadingOverlay = new Group();
            loadingOverlay.setSize(stage.getWidth(), stage.getHeight());

            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(skin.getColor("black_semi_transparent"));
            pixmap.fill();
            Texture bgTexture = new Texture(pixmap);
            Image bgImage = new Image(new TextureRegionDrawable(new TextureRegion(bgTexture)));
            bgImage.setSize(stage.getWidth(), stage.getHeight());
            pixmap.dispose();
            loadingOverlay.addActor(bgImage);

            TextureRegionDrawable loadingDrawable = new TextureRegionDrawable(
                game.getResourceManager().getAtlas("ui_pre_loaded").findRegion("loading_circle"));
            Image spinner = new Image(loadingDrawable);
            spinner.setSize(200, 200);
            spinner.setOrigin(Align.center);
            spinner.setPosition(
                (stage.getWidth() - spinner.getWidth()) / 2,
                (stage.getHeight() - spinner.getHeight()) / 2
            );
            spinner.addAction(Actions.forever(Actions.rotateBy(-360, 1)));
            loadingOverlay.addActor(spinner);
        }

        if (loadingOverlay.getParent() == null) {
            stage.addActor(loadingOverlay);
        }

        return loadingOverlay;
    }

    public void hideOverlay(Group overlayGroup) {
        if (overlayGroup != null) {
            overlayGroup.remove();
        }
    }

    public void hideLoadingOverlay() {
        if (loadingOverlay != null) {
            loadingOverlay.remove();
        }
    }

    public void showMessageOverlay(Stage stage, String messageText) {
        Group overlayGroup = showOverlay(stage);

        Label.LabelStyle baseStyle = skin.get("overlay_message", Label.LabelStyle.class);
        BitmapFont safeFont = new BitmapFont(baseStyle.font.getData(), baseStyle.font.getRegions(), baseStyle.font.usesIntegerPositions());
        safeFont.getData().setScale(1f);
        Label.LabelStyle protectedStyle = new Label.LabelStyle(safeFont, baseStyle.fontColor);

        Label message = new Label(messageText, protectedStyle);
        message.setWrap(true);
        message.setAlignment(Align.center);

        float maxTextWidth = stage.getWidth() * 0.9f;
        message.setWidth(maxTextWidth);
        message.layout();

        float textHeight = message.getPrefHeight();
        float paddingY = textHeight * 0.3f;

        float bgWidth = maxTextWidth + 20f;
        float bgHeight = textHeight + paddingY * 2;

        float bgX = (stage.getWidth() - bgWidth) / 2;
        float bgY = stage.getHeight() * 0.5f;

        TextureRegionDrawable messageBgDrawable = new TextureRegionDrawable(
            game.getResourceManager().getAtlas("ui_pre_loaded").findRegion("message_box_bg"));

        Image messageBg = new Image(messageBgDrawable);
        messageBg.setBounds(bgX, bgY, bgWidth, bgHeight);
        overlayGroup.addActor(messageBg);

        message.setPosition(
            (stage.getWidth() - message.getWidth()) / 2,
            bgY + paddingY
        );
        overlayGroup.addActor(message);

        overlayGroup.setTouchable(Touchable.enabled);
        overlayGroup.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideOverlay(overlayGroup);
            }
        });
    }

    public void showTempMessageOverlay(Stage stage, String messageText) {
        Group overlayGroup = showOverlay(stage);

        Label.LabelStyle baseStyle = skin.get("overlay_message", Label.LabelStyle.class);
        BitmapFont safeFont = new BitmapFont(baseStyle.font.getData(), baseStyle.font.getRegions(), baseStyle.font.usesIntegerPositions());
        safeFont.getData().setScale(1f);
        Label.LabelStyle protectedStyle = new Label.LabelStyle(safeFont, baseStyle.fontColor);

        Label message = new Label(messageText, protectedStyle);
        message.setWrap(true);
        message.setAlignment(Align.center);

        float maxTextWidth = stage.getWidth() * 0.9f;
        message.setWidth(maxTextWidth);
        message.layout();

        float textHeight = message.getPrefHeight();
        float paddingY = textHeight * 0.3f;

        float bgWidth = maxTextWidth + 20f;
        float bgHeight = textHeight + paddingY * 2;

        float bgX = (stage.getWidth() - bgWidth) / 2;
        float bgY = stage.getHeight() * 0.5f;

        TextureRegionDrawable messageBgDrawable = new TextureRegionDrawable(
            game.getResourceManager().getAtlas("ui_pre_loaded").findRegion("message_box_bg"));

        Image messageBg = new Image(messageBgDrawable);
        messageBg.setBounds(bgX, bgY, bgWidth, bgHeight);
        overlayGroup.addActor(messageBg);

        message.setPosition(
            (stage.getWidth() - message.getWidth()) / 2,
            bgY + paddingY
        );
        overlayGroup.addActor(message);
    }
}
