package com.alejandroacg.choicebound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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
    private Music mainMenuMusic;
    private Image googleLogoImage;
    private TextButton googleButton;
    private Skin skin;
    private Container<Table> buttonContainer;
    private Table buttonTable;

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

        try {
            mainMenuMusic = assetLoader.getMainMenuMusic();
            if (mainMenuMusic != null) {
                mainMenuMusic.setLooping(true);
                mainMenuMusic.play();
            }
        } catch (Exception e) {
            Gdx.app.error("SplashScreen", "Error al reproducir música: " + e.getMessage());
        }

        assetLoader.loadGameAssets();

        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin.json"));
    }

    private void createGoogleButton() {
        // Crear el estilo del TextButton dinámicamente
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        Label.LabelStyle labelStyle = skin.get("google", Label.LabelStyle.class);
        buttonStyle.font = labelStyle.font;
        buttonStyle.fontColor = labelStyle.fontColor;
        buttonStyle.up = new TextureRegionDrawable(resourceManager.getIntroAtlas().findRegion("google_button_up"));
        buttonStyle.down = new TextureRegionDrawable(resourceManager.getIntroAtlas().findRegion("google_button_down"));

        // Escalar la fuente para que la letra sea más grande
        buttonStyle.font.getData().setScale(1.5f);

        // Crear un TextButton con el estilo dinámico
        // TODO Encontrar una manera más elegante de alinear el texto en horizontal.
        TextButton googleButton = new TextButton("     " + GameConfig.getString("sign_in_with_google"), buttonStyle);

        // Forzar que el botón calcule su tamaño
        googleButton.pack();

        // Envolver el botón en un Container para hacerlo interactivo
        buttonContainer = new Container<>(googleButton);
        // Ajustar el tamaño del contenedor al del botón
        buttonContainer.pack();
        // Centrar horizontalmente y mover más abajo
        buttonContainer.setPosition(Gdx.graphics.getWidth() / 2f - buttonContainer.getWidth() / 2, Gdx.graphics.getHeight() * 0.3f);
        ClickListener listener = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("SplashScreen", "Botón de LibGDX pulsado");
            }
        };
        listener.setVisualPressed(false);
        buttonContainer.addListener(listener);

        stage.addActor(buttonContainer);
    }

    public void onLoginSuccess() {
        Gdx.app.log("SplashScreen", "Login exitoso, cambiando a la pantalla principal");
        game.setScreen(new HomeScreen(game));
        // Destruir el botón de login
        game.getPlatformBridge().destroyGoogleButton();
    }

    public void onLoginFailure(String errorMessage) {
        Gdx.app.log("SplashScreen", "Fallo en el login: " + errorMessage);
        // Aquí podrías mostrar un mensaje de error al usuario
        // Por ejemplo, usando una UI de LibGDX (Scene2D) para mostrar un mensaje
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

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }

    @Override public void pause() {}

    @Override public void resume() {}

    @Override public void show() {}

    @Override public void hide() {}

    @Override
    public void dispose() {
        if (mainMenuMusic != null) mainMenuMusic.stop();
        batch.dispose();
        stage.dispose();
        if (skin != null) skin.dispose();
    }
}
