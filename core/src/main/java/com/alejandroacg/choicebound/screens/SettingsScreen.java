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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
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

        // Botón "Eliminar cuenta"
        TextButton deleteAccountButton = uiElementFactory.createDefaultButton(GameConfig.getString("delete_account"));
        deleteAccountButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Dialog confirmationDialog = uiElementFactory.createConfirmationDialog(
                    GameConfig.getString("delete_confirmation"),
                    new UIElementFactory.ConfirmationListener() {
                        @Override
                        public void onConfirm() {
                            if (game.getConnectivityChecker().checkConnectivity(stage)) {
                                game.getDataManager().deleteUserData(
                                    () -> {
                                        Gdx.app.log("SettingsScreen", "Cuenta eliminada con éxito, redirigiendo a SplashScreen");
                                        game.signOut();
                                    },
                                    error -> {
                                        Gdx.app.log("SettingsScreen", "Error al eliminar cuenta: " + error);
                                        game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
                                    }
                                );
                            }
                        }

                        @Override
                        public void onCancel() {
                            Gdx.app.log("SettingsScreen", "Eliminación cancelada.");
                        }
                    }
                );
                confirmationDialog.show(stage);
            }
        });
        mainTable.add(deleteAccountButton).center().padBottom(20).row();

        // Botón "Idiomas"
        TextButton languagesButton = uiElementFactory.createDefaultButton(GameConfig.getString("languages"));
        languagesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("SettingsScreen", "Yendo a la pantalla de idiomas.");
                game.setScreen(new LanguagesScreen(game));
            }
        });
        mainTable.add(languagesButton).center().padBottom(20).row();

        // Botón "Atrás"
        TextButton backButton = uiElementFactory.createDefaultButton(GameConfig.getString("back"));
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("SettingsScreen", "Volviendo a la pantalla anterior.");
                game.setScreen(new HomeScreen(game));
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
