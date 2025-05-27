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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.alejandroacg.choicebound.utils.GameConfig.HEADER_HEIGHT_RATIO;

public class LanguagesScreen implements Screen {
    private final ChoiceboundGame game;
    private final Stage stage;
    private final UIElementFactory uiElementFactory;
    private final Color backgroundColor;

    public LanguagesScreen(ChoiceboundGame game) {
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
        Label titleLabel = uiElementFactory.createTitleLabel(GameConfig.getString("languages"));
        titleLabel.setFontScale(titleScale);
        titleLabel.setAlignment(Align.center);

        // Crear una subtabla para centrar verticalmente
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

        // Espacio entre el header y los botones
        mainTable.add().height(50).row();

        // Botón "Inglés"
        TextButton englishButton = uiElementFactory.createDefaultButton(GameConfig.getString("english"));
        englishButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("LanguagesScreen", "Seleccionando idioma inglés.");
                if (game.getConnectivityChecker().checkConnectivity(stage)) {
                    GameConfig.setCurrentLanguage("en");
                    game.getLocalUser().setPrefLanguage("en");
                    game.getDataManager().saveUserData(
                        game.getLocalUser(),
                        () -> Gdx.app.postRunnable(() -> game.setScreen(new LanguagesScreen(game))),
                        error -> Gdx.app.log("LanguagesScreen", "Error al guardar idioma: " + error)
                    );
                }
            }
        });
        mainTable.add(englishButton).center().padBottom(20).row();

        // Botón "Español"
        TextButton spanishButton = uiElementFactory.createDefaultButton(GameConfig.getString("spanish"));
        spanishButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("LanguagesScreen", "Seleccionando idioma español.");
                if (game.getConnectivityChecker().checkConnectivity(stage)) {
                    GameConfig.setCurrentLanguage("es");
                    game.getLocalUser().setPrefLanguage("es");
                    game.getDataManager().saveUserData(
                        game.getLocalUser(),
                        () -> Gdx.app.postRunnable(() -> game.setScreen(new LanguagesScreen(game))),
                        error -> Gdx.app.log("LanguagesScreen", "Error al guardar idioma: " + error)
                    );
                }
            }
        });
        mainTable.add(spanishButton).center().padBottom(20).row();

        // Botón "Atrás"
        TextButton backButton = uiElementFactory.createDefaultButton(GameConfig.getString("back"));
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("LanguagesScreen", "Volviendo a la pantalla anterior.");
                game.setScreen(new SettingsScreen(game));
            }
        });
        mainTable.add(backButton).center();
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
