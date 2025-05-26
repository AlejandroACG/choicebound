package com.alejandroacg.choicebound.ui;

import com.alejandroacg.choicebound.utils.GameConfig;
import com.alejandroacg.choicebound.resources.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import lombok.Getter;

public class UIElementFactory {
    @Getter
    private final ResourceManager resourceManager;
    @Getter
    private final Skin skin;
    private final Texture backgroundTexture;

    public UIElementFactory(ResourceManager resourceManager, Skin skin) {
        this.resourceManager = resourceManager;
        this.skin = skin;

        // Crear fondo para text fields
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(skin.getColor("soft_white"));
        pixmap.fill();
        backgroundTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public TextButton createGoogleButton() {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        Label.LabelStyle labelStyle = skin.get("google", Label.LabelStyle.class);

        // Clon de fuente para evitar mutaciones
        BitmapFont fontCopy = new BitmapFont(labelStyle.font.getData(), labelStyle.font.getRegions(), labelStyle.font.usesIntegerPositions());
        fontCopy.getData().setScale(1f);

        buttonStyle.font = fontCopy;
        buttonStyle.fontColor = labelStyle.fontColor;
        buttonStyle.up = new TextureRegionDrawable(resourceManager.getAtlas("intro").findRegion("google_button_up"));
        buttonStyle.down = new TextureRegionDrawable(resourceManager.getAtlas("intro").findRegion("google_button_down"));

        TextButton googleButton = new TextButton(GameConfig.getString("sign_in_with_google"), buttonStyle);

        // Padding responsivo
        float screenWidth = Gdx.graphics.getWidth();
        float paddingLeft = screenWidth * 0.05f;
        googleButton.getLabelCell().padLeft(paddingLeft);

        googleButton.pack();
        return googleButton;
    }

    public TextButton createDefaultButton(String text) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        Label.LabelStyle labelStyle = skin.get("roleplay_button", Label.LabelStyle.class);

        // Clon de fuente para evitar mutaciones
        BitmapFont fontCopy = new BitmapFont(labelStyle.font.getData(), labelStyle.font.getRegions(), labelStyle.font.usesIntegerPositions());
        fontCopy.getData().setScale(1f);

        buttonStyle.font = fontCopy;
        buttonStyle.fontColor = labelStyle.fontColor;
        buttonStyle.up = new TextureRegionDrawable(resourceManager.getAtlas("ui").findRegion("button_parchment"));
        buttonStyle.down = new TextureRegionDrawable(resourceManager.getAtlas("ui").findRegion("button_parchment_pressed"));
        buttonStyle.disabled = new TextureRegionDrawable(resourceManager.getAtlas("ui").findRegion("button_parchment_disabled"));
        buttonStyle.pressedOffsetX = 1;
        buttonStyle.pressedOffsetY = -1;

        TextButton button = new TextButton(text, buttonStyle);
        button.getLabelCell().pad(25f);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resourceManager.getSound("ui_click").play();
            }
        });
        return button;
    }

    public <T extends Actor> Container<T> createDefaultContainer() {
        Container<T> container = new Container<>();
        container.setBackground(new TextureRegionDrawable(resourceManager.getAtlas("ui").findRegion("container_parchment")));
        return container;
    }

    public Label createTitleLabel(String text) {
        Label.LabelStyle labelStyle = skin.get("roleplay_title", Label.LabelStyle.class);
        BitmapFont fontCopy = new BitmapFont(labelStyle.font.getData(), labelStyle.font.getRegions(), labelStyle.font.usesIntegerPositions());
        fontCopy.getData().setScale(1f);
        Label.LabelStyle clonedStyle = new Label.LabelStyle(fontCopy, labelStyle.fontColor);

        return new Label(text, clonedStyle);
    }

    public Label createBoldTitleLabel(String text) {
        Label.LabelStyle labelStyle = skin.get("roleplay_title_bold", Label.LabelStyle.class);
        BitmapFont fontCopy = new BitmapFont(labelStyle.font.getData(), labelStyle.font.getRegions(), labelStyle.font.usesIntegerPositions());
        fontCopy.getData().setScale(1f);
        Label.LabelStyle clonedStyle = new Label.LabelStyle(fontCopy, labelStyle.fontColor);

        return new Label(text, clonedStyle);
    }

    public TextField createTextField() {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        Label.LabelStyle labelStyle = skin.get("google", Label.LabelStyle.class);

        BitmapFont scaledFont = new BitmapFont(labelStyle.font.getData(), labelStyle.font.getRegions(), false);
        scaledFont.getData().setScale(1.25f);

        style.font = scaledFont;
        style.fontColor = labelStyle.fontColor;

        // Background y cursor visibles
        style.background = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        Pixmap cursorPixmap = new Pixmap(2, 40, Pixmap.Format.RGBA8888);
        cursorPixmap.setColor(Color.BLACK);
        cursorPixmap.fill();
        style.cursor = new TextureRegionDrawable(new TextureRegion(new Texture(cursorPixmap)));
        cursorPixmap.dispose();

        return new TextField("", style);
    }

    public Table createHeader() {
        Table headerTable = new Table();
        float screenHeight = Gdx.graphics.getHeight();

        Color headerColor = skin.getColor("brown_header");

        // Fondo sólido marrón suave
        headerTable.setBackground(createSolidColorDrawable(headerColor));

        // Altura proporcional al alto de la pantalla (puedes ajustar el 0.2f)
        headerTable.setHeight(screenHeight * 0.2f);
        headerTable.top(); // Alinea contenido en la parte superior del header

        return headerTable;
    }

    public TextureRegionDrawable createSolidColorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public Dialog createConfirmationDialog(String message, ConfirmationListener listener) {
        // Crear estilo del diálogo
        Label.LabelStyle labelStyle = skin.get("roleplay_title_bold", Label.LabelStyle.class);
        BitmapFont fontCopy = new BitmapFont(labelStyle.font.getData(), labelStyle.font.getRegions(), labelStyle.font.usesIntegerPositions());
        fontCopy.getData().setScale(1f);

        TextureRegion backgroundRegion = resourceManager.getAtlas("ui").findRegion("container_parchment");

        WindowStyle dialogStyle = new WindowStyle();
        dialogStyle.titleFont = fontCopy;
        dialogStyle.titleFontColor = labelStyle.fontColor;
        dialogStyle.background = new TextureRegionDrawable(backgroundRegion);

        // Crear el diálogo sin título
        Dialog dialog = new Dialog("", dialogStyle) {
            @Override
            public float getPrefWidth() {
                return Gdx.graphics.getWidth() * 0.75f;
            }

            @Override
            public float getPrefHeight() {
                return super.getPrefHeight();
            }
        };

        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);

        // Crear tabla interna para el contenido
        Table content = new Table();
        content.defaults().pad(5f); // Padding interno global

        // Mensaje centrado con mayor escala y wrapping
        Label messageLabel = createBoldTitleLabel(message);
        messageLabel.setWrap(true);
        messageLabel.setFontScale(1.5f);
        messageLabel.setAlignment(Align.center);
        float labelWidth = Gdx.graphics.getWidth() * 0.75f - 160f; // 80f padding a cada lado
        content.add(messageLabel).width(labelWidth).center().row();

        // Botones personalizados
        TextButton yesButton = createDefaultButton(GameConfig.getString("yes"));
        TextButton noButton = createDefaultButton(GameConfig.getString("no"));

        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                yesButton.setDisabled(true);
                noButton.setDisabled(true);
                dialog.hide();
                listener.onConfirm();
            }
        });

        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                yesButton.setDisabled(true);
                noButton.setDisabled(true);
                dialog.hide();
                listener.onCancel();
            }
        });

        // Subtabla para botones centrados y bien espaciados
        Table buttonTable = new Table();
        buttonTable.add(yesButton).pad(10f);
        buttonTable.add(noButton).pad(10f);

        content.add(buttonTable).center().row();

        dialog.getContentTable().add(content).expand().fill().pad(80f);

        return dialog;
    }

    public interface ConfirmationListener {
        void onConfirm();
        void onCancel();
    }
}
