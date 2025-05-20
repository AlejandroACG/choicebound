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
import com.alejandroacg.choicebound.ui.ButtonHandler;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.alejandroacg.choicebound.utils.MusicManager;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HomeScreen implements Screen {
    private final ChoiceboundGame game;
    private final Stage stage;
    private final Color backgroundColor;
    private final ButtonHandler buttonHandler;

    public HomeScreen(ChoiceboundGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.buttonHandler = new ButtonHandler(game.getResourceManager(), game.getSkin());
        Gdx.input.setInputProcessor(stage);

        this.backgroundColor = game.getSkin().getColor("parchment_light");
        setupUI();
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Título "Choicebound"
        String titleText = GameConfig.getString("title_choicebound");
        Label titleLabel = new Label(titleText, game.getSkin(), "roleplay_title");
        titleLabel.setFontScale(2.0f);
        mainTable.add(titleLabel).expandX().center().padTop(20).padBottom(10).row();

        // Mensaje de bienvenida "Welcome, [nombre de usuario]"
        String welcomeText = GameConfig.getString("welcome_message") + ", " + game.getUserInfo().getDisplayName();;
        Label welcomeLabel = new Label(welcomeText, game.getSkin(), "roleplay_narrative_grey");
        welcomeLabel.setFontScale(1.5f);
        mainTable.add(welcomeLabel).expandX().center().padBottom(20).row();

        // Botón "Store"
        TextButton storeButton = buttonHandler.createDefaultButton(GameConfig.getString("store_button"));
        storeButton.setDisabled(true);
        storeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("HomeScreen", "Botón Shop pulsado");
            }
        });
        mainTable.add(storeButton).width(storeButton.getWidth()).height(storeButton.getHeight()).padBottom(50).row();

        // Área central para las diferentes campañas
        mainTable.add().expand().row();

        // Botón de Sign Out en la parte inferior
        TextButton signOutButton = buttonHandler.createDefaultButton(GameConfig.getString("sign_out"));
        signOutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getPlatformBridge().signOut();
                Gdx.app.postRunnable(() -> game.setScreen(new SplashScreen(game)));
            }
        });
        mainTable.add(signOutButton).width(signOutButton.getWidth()).height(signOutButton.getHeight()).padBottom(20);
    }

    @Override
    public void show() {
        MusicManager musicManager = game.getMusicManager();
        if (!musicManager.isPlaying("main_menu")) {
            musicManager.playMusic("main_menu");
        }
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

    public Stage getStage() {
        return stage;
    }
}
