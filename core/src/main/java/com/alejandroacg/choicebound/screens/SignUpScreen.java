package com.alejandroacg.choicebound.screens;

import com.alejandroacg.choicebound.data.LocalUser;
import com.alejandroacg.choicebound.utils.ConnectivityChecker;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.ui.UIElementFactory;
import com.alejandroacg.choicebound.utils.GameConfig;

public class SignUpScreen implements Screen {
    private final ChoiceboundGame game;
    private final Stage stage;
    private final UIElementFactory uiElementFactory;
    private final Color backgroundColor;
    private final Table table;
    private Cell<?> paddingTopCell;
    private float lastKeyboardHeight = 0f;
    final int MAX_USERNAME_LENGTH = 10;
    private TextField usernameField;

    public SignUpScreen(ChoiceboundGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.uiElementFactory = new UIElementFactory(game.getResourceManager(), game.getSkin());
        this.table = new Table();
        Gdx.input.setInputProcessor(stage);

        this.backgroundColor = game.getSkin().getColor("parchment_light");
        setupUI();
    }

    private void setupUI() {
        table.setFillParent(true);
        table.align(Align.top); // Alinear el contenido del Table en la parte superior
        stage.addActor(table);

        // Calcular el padding superior para mover los elementos al 75% de la altura
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float padTop = screenHeight * 0.25f; // 25% de la altura desde la parte superior (para centrar al 75%)
        float titleScale = 3.0f;
        float labelScale = 2.0f;

        // Añadir espacio superior para mover los elementos hacia abajo
        paddingTopCell = table.add().height(padTop);
        table.row();

        // Título "Sign Up" con escala duplicada
        Label titleLabel = uiElementFactory.createTitleLabel(GameConfig.getString("sign_up"));
        titleLabel.setFontScale(titleScale);
        table.add(titleLabel).expandX().center().padBottom(60).row();

        // Etiqueta "Username" en negrita con escala duplicada
        Label usernameLabel = uiElementFactory.createBoldTitleLabel(GameConfig.getString("username_label"));
        usernameLabel.setFontScale(labelScale);
        table.add(usernameLabel).expandX().center().padBottom(60).row();

        // Campo de entrada para el nombre de usuario
        usernameField = uiElementFactory.createTextField();

        // Añadir un filtro para limitar la longitud del texto
        usernameField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                // Permitir el carácter solo si la longitud actual del texto es menor que el máximo
                if (textField.getText().length() >= MAX_USERNAME_LENGTH) {
                    return false;
                }

                return Character.isLetterOrDigit(c) || c == '-' || c == '_';
            }
        });

        usernameField.setAlignment(Align.center);

        table.add(usernameField).width(screenWidth * 0.6f).height(screenHeight * 0.05f).padBottom(screenHeight * 0.05f).row();

        // Botón de registro
        TextButton registerButton = uiElementFactory.createDefaultButton(GameConfig.getString("register_button"));
        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String username = usernameField.getText();
                String uid = game.getPlatformBridge().getCurrentUserId();

                if (game.getPlatformBridge().isUserAuthenticated() && uid != null) {
                    if (game.getConnectivityChecker().checkConnectivity(stage)) {
                        if (!username.isEmpty()) {
                            Gdx.app.log("SignUpScreen", "Registro con username: " + username);
                            LocalUser potentialUser = new LocalUser(username, uid);
                            game.getUserDataManager().saveUserData(
                                potentialUser,
                                () -> onSignUpSuccess(),
                                error -> onSignUpFailure(error)
                            );
                        } else {
                            Gdx.app.log("SignUpScreen", "Username vacío");
                            onSignUpFailure(GameConfig.getString("error_no_username_message"));
                        }
                    }
                } else {
                    game.getPlatformBridge().signOut();
                }
            }
        });
        table.add(registerButton).padBottom(60);
    }

    private void onSignUpSuccess() {
        Gdx.app.log("SignUpScreen", "Registro exitoso, redirigiendo a HomeScreen");
        game.setLocalUser(new LocalUser(usernameField.getText(), game.getPlatformBridge().getCurrentUserId()));
        Gdx.input.setOnscreenKeyboardVisible(false);
        Gdx.app.postRunnable(() -> game.setScreen(new HomeScreen(game)));
    }

    private void onSignUpFailure(String errorMessage) {
        Gdx.app.log("SignUpScreen", "Fallo en el registro: " + errorMessage);
        game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
    }

    @Override
    public void show() {
        game.getMusicManager().playIfDifferent("main_menu");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float keyboardHeight = game.getPlatformBridge().getKeyboardHeight();

        if (keyboardHeight != lastKeyboardHeight) {
            float screenHeight = Gdx.graphics.getHeight();
            float newPadTop = screenHeight * 0.25f - keyboardHeight;
            if (newPadTop < 0) newPadTop = 0;

            paddingTopCell.height(newPadTop); // Uso correcto de la celda del padding
            table.invalidate(); // Forzamos recalculo del layout

            lastKeyboardHeight = keyboardHeight;
        }

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
