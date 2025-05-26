package com.alejandroacg.choicebound.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.resources.ResourceManager;
import com.alejandroacg.choicebound.ui.UIElementFactory;
import com.alejandroacg.choicebound.utils.ConnectivityChecker;
import com.alejandroacg.choicebound.utils.GameConfig;
import java.util.ArrayList;

public class SplashScreen implements Screen {
    private final ChoiceboundGame game;
    private final ResourceManager resourceManager;
    private final SpriteBatch batch;
    private final Stage stage;
    private Animation<TextureRegion> introAnimation;
    private float animationTime;
    private float delayTime;
    private boolean isAnimationStarted, isAnimationFinished, isGameAssetsLoaded;
    private TextureRegion lastFrame;
    private Container<Table> buttonContainer;
    private UIElementFactory uiElementFactory;
    private Group currentOverlay;

    public SplashScreen(ChoiceboundGame game) {
        this.game = game;
        this.resourceManager = game.getResourceManager();
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());

        game.clearLocalUser();

        animationTime = 0f;
        delayTime = 0f;
        isAnimationStarted = false;
        isAnimationFinished = false;
        isGameAssetsLoaded = false;

        ArrayList<TextureRegion> frameList = new ArrayList<>();
        int frameIndex = 0;
        while (true) {
            String frameName = "frame" + frameIndex;
            TextureRegion frame = resourceManager.getAtlas("intro").findRegion(frameName);
            if (frame == null) break;
            frameList.add(frame);
            frameIndex++;
        }

        if (frameList.isEmpty()) {
            Gdx.app.error("SplashScreen", "No se encontraron fotogramas");
            isAnimationFinished = true;
            return;
        }

        TextureRegion[] frames = frameList.toArray(new TextureRegion[0]);
        introAnimation = new Animation<>(1f / 25f, frames);
        introAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        resourceManager.loadGameAssets();

        Gdx.input.setInputProcessor(stage);
        this.uiElementFactory = new UIElementFactory(game.getResourceManager(), game.getSkin());
    }

    private void createGoogleButton() {
        TextButton googleButton = uiElementFactory.createGoogleButton();

        googleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("SplashScreen", "Botón pulsado, iniciando One Tap Sign-In");
                if (game.getConnectivityChecker().checkConnectivity(stage)) {
                    currentOverlay = game.getOverlayManager().showOverlay(stage);
                    game.getPlatformBridge().startOneTapSignIn();
                }
            }
        });

        buttonContainer = new Container<>(googleButton);
        buttonContainer.pack();
        buttonContainer.setPosition(Gdx.graphics.getWidth() / 2f - buttonContainer.getWidth() / 2, Gdx.graphics.getHeight() * 0.3f);

        stage.addActor(buttonContainer);
    }

    public void onSignInSuccess() {
        Gdx.app.log("SplashScreen", "Sign In exitoso, cargando datos del usuario");
        game.getUserDataManager().loadUserDataFromFirestore(
            () -> {
                // Éxito: datos cargados, ahora redirigir a HomeScreen
                Gdx.app.log("SplashScreen", "Datos cargados, cambiando a la pantalla principal");
                game.getMusicManager().stop();
                Gdx.app.postRunnable(() -> game.setScreen(new HomeScreen(game)));
            },
            error -> {
                // Error al cargar datos, ejecutar signOut
                Gdx.app.log("SplashScreen", "Error al cargar datos del usuario: " + error);
                game.getPlatformBridge().signOut();
                game.getOverlayManager().hideOverlay(currentOverlay);
            }
        );
    }

    public void onFirstSignInSuccess() {
        Gdx.app.log("SplashScreen", "First Sign In exitoso, cambiando a la pantalla de registro");
        game.getLocalUser().setUid(game.getPlatformBridge().getCurrentUserId());
        // Encolar el cambio de pantalla en el hilo principal
        game.getMusicManager().stop();
        Gdx.app.postRunnable(() -> game.setScreen(new SignUpScreen(game)));
    }

    public void onSignInFailure(String errorMessage) {
        Gdx.app.log("SplashScreen", "Fallo en el sign in: " + errorMessage);
        game.getOverlayManager().hideOverlay(currentOverlay);
        game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
    }

    @Override
    public void show() {
        game.getMusicManager().playExclusive("splash_screen");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isAnimationStarted) {
            delayTime += delta;
            if (delayTime >= 2.9f) isAnimationStarted = true;
            return;
        }

        batch.begin();

        if (!isAnimationFinished) {
            animationTime += delta;

            if (animationTime >= introAnimation.getAnimationDuration()) {
                isAnimationFinished = true;
                lastFrame = introAnimation.getKeyFrame(introAnimation.getAnimationDuration());

                createGoogleButton();
                currentOverlay = game.getOverlayManager().showOverlay(stage);
                String uid = game.getPlatformBridge().getCurrentUserId();

                // Verificar conectividad antes de autenticación
                if (game.getConnectivityChecker().checkConnectivity(stage) && game.getPlatformBridge().isUserAuthenticated() && uid != null) {
                    game.getDatabase().doesUserExist(
                        uid,
                        exists -> {
                            if (exists) {
                                onSignInSuccess();
                            } else {
                                game.getPlatformBridge().signOut();
                                game.getOverlayManager().hideOverlay(currentOverlay);
                            }
                        },
                        error -> {
                            Gdx.app.error("SplashScreen", "Error al verificar existencia de usuario: " + error);
                            game.getPlatformBridge().signOut();
                            game.getOverlayManager().hideOverlay(currentOverlay);
                        }
                    );
                } else {
                    game.getPlatformBridge().signOut();
                    game.getOverlayManager().hideOverlay(currentOverlay);
                }
            }

            float alpha = Math.min(1f, animationTime / 2f);
            batch.setColor(1, 1, 1, alpha);

            TextureRegion current = introAnimation.getKeyFrame(animationTime);
            if (current != null) {
                batch.draw(current, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }

            batch.setColor(1, 1, 1, 1);
        } else {
            if (!isGameAssetsLoaded && resourceManager.update()) {
                isGameAssetsLoaded = true;
            }

            if (lastFrame != null) {
                batch.draw(lastFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        }

        batch.end();
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
        batch.dispose();
        stage.dispose();
    }
}
