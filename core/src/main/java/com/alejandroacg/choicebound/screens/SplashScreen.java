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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.resources.ResourceManager;
import com.alejandroacg.choicebound.ui.ButtonHandler;
import com.alejandroacg.choicebound.utils.ConnectivityChecker;

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
    private ConnectivityChecker connectivityChecker;
    private ButtonHandler buttonHandler;
    private Group loginOverlay;

    public SplashScreen(ChoiceboundGame game, ResourceManager assetLoader) {
        this.game = game;
        this.resourceManager = assetLoader;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());

        animationTime = 0f;
        delayTime = 0f;
        isAnimationStarted = false;
        isAnimationFinished = false;
        isGameAssetsLoaded = false;

        ArrayList<TextureRegion> frameList = new ArrayList<>();
        int frameIndex = 0;
        while (true) {
            String frameName = "frame" + frameIndex;
            TextureRegion frame = assetLoader.getIntroAtlas().findRegion(frameName);
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

        assetLoader.loadGameAssets();

        Gdx.input.setInputProcessor(stage);
        this.connectivityChecker = new ConnectivityChecker(game.getPlatformBridge(), game.getSkin(), game.getOverlayManager());
        this.buttonHandler = new ButtonHandler(resourceManager, game.getSkin());
    }

    private void createGoogleButton() {
        TextButton googleButton = buttonHandler.createGoogleButton();

        googleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("SplashScreen", "Botón pulsado, iniciando One Tap Sign-In");
                if (connectivityChecker.checkConnectivity(stage)) {
                    loginOverlay = game.getOverlayManager().showOverlay(stage);
                    game.getPlatformBridge().startOneTapSignIn();
                }
            }
        });

        buttonContainer = new Container<>(googleButton);
        buttonContainer.pack();
        buttonContainer.setPosition(Gdx.graphics.getWidth() / 2f - buttonContainer.getWidth() / 2, Gdx.graphics.getHeight() * 0.3f);

        stage.addActor(buttonContainer);
    }

    public void onLoginSuccess() {
        Gdx.app.log("SplashScreen", "Login exitoso, cambiando a la pantalla principal");
        game.setScreen(new HomeScreen(game));
    }

    public void onLoginFailure(String errorMessage) {
        Gdx.app.log("SplashScreen", "Fallo en el login: " + errorMessage);
        game.getOverlayManager().hideOverlay(loginOverlay);
        final Group overlayGroup = game.getOverlayManager().showOverlay(stage);

        Label message = new Label("Se ha producido un error y no se ha podido procesar la solicitud.", game.getSkin(), "error_message");
        message.setWrap(true);
        message.setWidth(stage.getWidth() * 0.8f);
        message.setAlignment(Align.center);
        message.setPosition((stage.getWidth() - message.getWidth()) / 2, stage.getHeight() / 2);
        overlayGroup.addActor(message);

        overlayGroup.setTouchable(Touchable.enabled);
        overlayGroup.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getOverlayManager().hideOverlay(overlayGroup);
            }
        });
    }

    @Override
    public void show() {
        game.getMusicManager().playMusic("main_menu");
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
