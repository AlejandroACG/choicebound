package com.alejandroacg.choicebound.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.utils.MusicManager;

public class HomeScreen implements Screen {
    private final ChoiceboundGame game;
    private final Stage stage;
    private final Color backgroundColor;

    public HomeScreen(ChoiceboundGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Obtener el color de fondo del Skin
        this.backgroundColor = game.getSkin().getColor("parchment_light");
    }

    @Override
    public void show() {
        // Reproducir main_menu si no está sonando
        MusicManager musicManager = game.getMusicManager();
        if (!musicManager.isPlaying("main_menu")) {
            musicManager.playMusic("main_menu");
        }
    }

    @Override
    public void render(float delta) {
        // Establecer el color de fondo
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
