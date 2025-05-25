package com.alejandroacg.choicebound.screens;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.ui.UIElementFactory;
import com.alejandroacg.choicebound.utils.GameConfig;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.alejandroacg.choicebound.utils.GameConfig.HEADER_HEIGHT_RATIO;

public class SettingsScreen implements Screen {
    private final ChoiceboundGame game;
    private final Stage stage;
    private final UIElementFactory uiElementFactory;
    private final Color backgroundColor;

    public SettingsScreen(ChoiceboundGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.uiElementFactory = new UIElementFactory(game.getResourceManager(), game.getSkin());
        Gdx.input.setInputProcessor(stage);
        this.backgroundColor = game.getSkin().getColor("parchment_light");
        setupUI();
    }

    private void setupUI() {
        float screenHeight = Gdx.graphics.getHeight();
        float titleScale = 3f;

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        stage.addActor(mainTable);

        // Crear el header
        Table header = uiElementFactory.createHeader();

        // Crear y configurar el título
        Label titleLabel = uiElementFactory.createTitleLabel(GameConfig.getString("settings"));
        titleLabel.setFontScale(titleScale);
        titleLabel.setAlignment(1); // Align.center

        // Subtabla interna para centrar verticalmente
        Table headerContent = new Table();
        headerContent.setFillParent(true);
        headerContent.add(titleLabel).expand().center();
        header.addActor(headerContent);

        // Añadir el header al layout principal
        mainTable.add(header)
            .height(screenHeight * HEADER_HEIGHT_RATIO)
            .width(Gdx.graphics.getWidth())
            .expandX()
            .fillX()
            .row();

        // Subtabla para centrar contenido debajo del header
        Table bodyTable = new Table();
        bodyTable.center();

        // Botón "Eliminar cuenta"
        TextButton deleteAccountButton = uiElementFactory.createDefaultButton(GameConfig.getString("delete_account"));
        deleteAccountButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("SettingsScreen", "Botón Eliminar cuenta pulsado");
                // TODO: lógica para eliminar cuenta
            }
        });

        // Botón "Atrás"
        TextButton backButton = uiElementFactory.createDefaultButton(GameConfig.getString("back"));
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("SettingsScreen", "Botón Atrás pulsado");
                game.setScreen(new HomeScreen(game));
            }
        });

        bodyTable.add(deleteAccountButton).padBottom(20).row();
        bodyTable.add(backButton);

        // El body ocupa todo el espacio disponible restante
        mainTable.add(bodyTable).expand().center().row();
    }

    @Override
    public void show() {}

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
