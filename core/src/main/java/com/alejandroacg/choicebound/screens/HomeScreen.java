package com.alejandroacg.choicebound.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.ui.UIElementFactory;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.alejandroacg.choicebound.utils.GameConfig.HEADER_HEIGHT_RATIO;

public class HomeScreen implements Screen {
    private final ChoiceboundGame game;
    private final Stage stage;
    private final UIElementFactory uiElementFactory;
    private final Color backgroundColor;

    public HomeScreen(ChoiceboundGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.uiElementFactory = new UIElementFactory(game.getResourceManager(), game.getSkin());
        Gdx.input.setInputProcessor(stage);

        this.backgroundColor = game.getSkin().getColor("parchment_light");
        setupUI();
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        stage.addActor(mainTable);

        float screenHeight = Gdx.graphics.getHeight();
        float titleScale = 3f;
        float subtitleScale = 2f;

        // Crear el header con fondo y altura fija
        Table header = uiElementFactory.createHeader();

        // Crear y configurar los elementos del header
        Label titleLabel = uiElementFactory.createTitleLabel(GameConfig.getString("title_choicebound"));
        titleLabel.setFontScale(titleScale);
        titleLabel.setAlignment(Align.center);

        Label welcomeLabel = new Label(
            GameConfig.getString("welcome_message") + ", " + game.getLocalUser().getUsername(),
            game.getSkin(),
            "roleplay_narrative_grey"
        );
        welcomeLabel.setFontScale(subtitleScale);
        welcomeLabel.setAlignment(Align.center);

        TextButton storeButton = uiElementFactory.createDefaultButton(GameConfig.getString("store_button"));
        storeButton.setDisabled(true);
        storeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("HomeScreen", "Botón Store pulsado");
            }
        });

        TextButton settingsButton = uiElementFactory.createDefaultButton(GameConfig.getString("settings"));
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("HomeScreen", "Botón Settings pulsado");
                game.setScreen(new SettingsScreen(game));
            }
        });

        // Subtabla horizontal para los botones
        Table buttonsTable = new Table();
        buttonsTable.add(storeButton).padRight(20);
        buttonsTable.add(settingsButton);

        // Subtabla para centrar verticalmente el contenido del header
        Table headerContent = new Table();
        headerContent.setFillParent(true);
        headerContent.top().center();
        headerContent.add(titleLabel).padTop(20).row();
        headerContent.add(welcomeLabel).padTop(10).row();
        headerContent.add(buttonsTable).padTop(10);

        header.addActor(headerContent);

        // Añadir el header al layout principal
        mainTable.add(header)
            .height(screenHeight * HEADER_HEIGHT_RATIO)
            .width(Gdx.graphics.getWidth())
            .expandX()
            .fillX()
            .row();

        // Área central para las diferentes campañas
        mainTable.add().expand().row();

        // Botón de Sign Out en la parte inferior
        TextButton signOutButton = uiElementFactory.createDefaultButton(GameConfig.getString("sign_out"));
        signOutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.signOut();
            }
        });
        mainTable.add(signOutButton).padBottom(20);
    }

    @Override
    public void show() {
        game.getMusicManager().playIfDifferent("main_menu");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
