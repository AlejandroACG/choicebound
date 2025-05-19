package com.alejandroacg.choicebound;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ResourceManager {
    private final AssetManager manager;

    public ResourceManager() {
        manager = new AssetManager();
    }

    // Carga los assets necesarios para el splash
    public void loadSplashAssets() {
        manager.load("textures/intro.atlas", TextureAtlas.class);
        manager.load("sounds/main_menu.mp3", Music.class);
    }

    // Carga los assets necesarios para el resto del juego
    public void loadGameAssets() {
    }

    // Verifica si los assets están cargados
    public boolean update() {
        return manager.update();
    }

    // Obtiene el progreso de carga (de 0 a 1)
    public float getProgress() {
        return manager.getProgress();
    }

    // Acceso a los assets cargados
    public TextureAtlas getIntroAtlas() {
        return manager.get("textures/intro.atlas", TextureAtlas.class);
    }

    public Music getMainMenuMusic() {
        return manager.get("sounds/main_menu.mp3", Music.class);
    }

    public void dispose() {
        manager.dispose();
    }
}
