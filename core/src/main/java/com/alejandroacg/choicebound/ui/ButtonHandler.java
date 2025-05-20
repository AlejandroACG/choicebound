package com.alejandroacg.choicebound.ui;

import com.alejandroacg.choicebound.utils.GameConfig;
import com.alejandroacg.choicebound.resources.ResourceManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ButtonHandler {
    private final ResourceManager resourceManager;
    private final Skin skin;

    public ButtonHandler(ResourceManager resourceManager, Skin skin) {
        this.resourceManager = resourceManager;
        this.skin = skin;
    }

    public TextButton createGoogleButton() {
        // Crear el estilo del TextButton dinámicamente
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        Label.LabelStyle labelStyle = skin.get("google", Label.LabelStyle.class);
        buttonStyle.font = labelStyle.font;
        buttonStyle.fontColor = labelStyle.fontColor;
        buttonStyle.up = new TextureRegionDrawable(resourceManager.getAtlas("intro").findRegion("google_button_up"));
        buttonStyle.down = new TextureRegionDrawable(resourceManager.getAtlas("intro").findRegion("google_button_down"));

        // Escalar la fuente para que la letra sea más grande
        buttonStyle.font.getData().setScale(1.5f);

        // Crear un TextButton con el estilo dinámico
        TextButton googleButton = new TextButton("     " + GameConfig.getString("sign_in_with_google"), buttonStyle);

        // Forzar que el botón calcule su tamaño
        googleButton.pack();

        return googleButton;
    }

    public TextButton createDefaultButton(String text) {
        // Crear el estilo del TextButton dinámicamente
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        Label.LabelStyle labelStyle = skin.get("roleplay_button", Label.LabelStyle.class);
        buttonStyle.font = labelStyle.font;
        buttonStyle.fontColor = labelStyle.fontColor;
        buttonStyle.up = new TextureRegionDrawable(resourceManager.getAtlas("ui").findRegion("button_parchment"));
        buttonStyle.down = new TextureRegionDrawable(resourceManager.getAtlas("ui").findRegion("button_parchment_pressed"));
        buttonStyle.pressedOffsetX = 1;
        buttonStyle.pressedOffsetY = -1;

        // Crear un TextButton con el estilo dinámico
        TextButton button = new TextButton(text, buttonStyle);

        // Forzar que el botón calcule su tamaño
        button.pack();

        return button;
    }

    public <T extends Actor> Container<T> createDefaultContainer() {
        Container<T> container = new Container<>();
        container.setBackground(new TextureRegionDrawable(resourceManager.getAtlas("ui").findRegion("container_parchment")));
        return container;
    }
}
