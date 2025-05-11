package com.alejandroacg.choicebound;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;

public class SplashScreen implements Screen {
    private final SpriteBatch batch;
    private Animation<TextureRegion> introAnimation;
    private float animationTime;
    private float delayTime;
    private boolean isAnimationStarted;
    private boolean isAnimationFinished;
    private boolean isGameAssetsLoaded;
    private final ChoiceboundGame game;
    private final ResourceManager resourceManager;
    private TextureRegion lastFrame;
    private Music mainMenuMusic;

    public SplashScreen(ChoiceboundGame game, ResourceManager assetLoader) {
        this.game = game;
        this.resourceManager = assetLoader;
        batch = new SpriteBatch();
        animationTime = 0f;
        delayTime = 0f; // Tiempo de espera inicial
        isAnimationStarted = false;
        isAnimationFinished = false;
        isGameAssetsLoaded = false;

        // Crea la animación desde los fotogramas del atlas (ya cargado)
        ArrayList<TextureRegion> frameList = new ArrayList<>();
        int frameIndex = 0;
        while (true) {
            // Los fotogramas en el atlas se nombran frame0, frame1, etc.
            String frameName = "frame" + frameIndex;
            TextureRegion frame = assetLoader.getIntroAtlas().findRegion(frameName);
            if (frame == null) {
                Gdx.app.log("SplashScreen", "Último fotograma encontrado: " + (frameIndex - 1));
                break; // Sale del bucle cuando no encuentra más fotogramas
            }
            frameList.add(frame);
            frameIndex++;
        }

        if (frameList.isEmpty()) {
            Gdx.app.error("SplashScreen", "No se encontraron fotogramas en el atlas");
            isAnimationFinished = true;
            return;
        }

        // Convierte la lista a un array
        TextureRegion[] frames = frameList.toArray(new TextureRegion[0]);
        introAnimation = new Animation<>(1f / 25f, frames); // 25 FPS
        introAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        Gdx.app.log("SplashScreen", "Animación inicializada con " + frames.length + " fotogramas");

        // Reproduce la música (ya cargada)
        try {
            mainMenuMusic = assetLoader.getMainMenuMusic();
            if (mainMenuMusic == null) {
                Gdx.app.error("SplashScreen", "No se pudo cargar la música");
            } else {
                mainMenuMusic.setLooping(true);
                mainMenuMusic.play();
                Gdx.app.log("SplashScreen", "Música iniciada: splash_music.mp3");
            }
        } catch (Exception e) {
            Gdx.app.error("SplashScreen", "Error al reproducir la música: " + e.getMessage());
        }

        // Comienza a cargar los assets del resto del juego
        try {
            assetLoader.loadGameAssets();
            Gdx.app.log("SplashScreen", "Comenzando carga de assets del juego");
        } catch (Exception e) {
            Gdx.app.error("SplashScreen", "Error al cargar assets del juego: " + e.getMessage());
        }
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isAnimationStarted) {
            delayTime += Gdx.graphics.getDeltaTime();
            if (delayTime >= 2.9f) {
                isAnimationStarted = true;
            }
            return;
        }

        if (!isAnimationFinished) {
            animationTime += Gdx.graphics.getDeltaTime();
            if (animationTime >= introAnimation.getAnimationDuration()) { // Termina cuando la animación llega al final
                isAnimationFinished = true;
                lastFrame = introAnimation.getKeyFrame(introAnimation.getAnimationDuration());
                Gdx.app.log("SplashScreen", "Animación terminada, creando botón de Google");
                game.getPlatformBridge().createGoogleButton();
            }

            // Fade-in durante 2 segundos (como lo ajustaste)
            float alpha = Math.min(1f, animationTime / 2f); // Fade-in de 2 segundos
            batch.setColor(1, 1, 1, alpha);

            batch.begin();
            TextureRegion currentFrame = introAnimation.getKeyFrame(animationTime);
            if (currentFrame != null) {
                batch.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            } else {
                Gdx.app.error("SplashScreen", "currentFrame es null");
            }
            batch.end();
            batch.setColor(1, 1, 1, 1); // Restablece el alpha
        } else {
            // Verifica si los assets del juego están cargados
            if (!isGameAssetsLoaded) {
                if (resourceManager.update()) {
                    isGameAssetsLoaded = true;
                    Gdx.app.log("SplashScreen", "Assets del juego cargados");
                }
            }

            // Muestra el último fotograma y el botón de login
            batch.begin();
            if (lastFrame != null) {
                batch.draw(lastFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            } else {
                Gdx.app.error("SplashScreen", "lastFrame es null");
            }
            batch.end();
        }
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
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        if (mainMenuMusic != null && mainMenuMusic.isPlaying()) {
            mainMenuMusic.pause();
        }
    }

    @Override
    public void resume() {
        if (mainMenuMusic != null && !mainMenuMusic.isPlaying()) {
            mainMenuMusic.play();
        }
    }

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (mainMenuMusic != null) {
            mainMenuMusic.stop();
        }
        batch.dispose();
    }
}
