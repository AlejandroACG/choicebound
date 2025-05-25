package com.alejandroacg.choicebound.resources;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ResourceManager {
    private final AssetManager manager;

    public ResourceManager() {
        manager = new AssetManager();
    }

    // Carga los assets necesarios para el splash
    public void loadSplashAssets() {
        manager.load("textures/intro.atlas", TextureAtlas.class);
        manager.load("music/splash_screen.mp3", Music.class);
    }

    // Carga los assets necesarios para el resto del juego
    public void loadGameAssets() {
        manager.load("textures/ui.atlas", TextureAtlas.class);
        manager.load("music/main_menu.mp3", Music.class);
        manager.load("sounds/ui_click.wav", Sound.class);
    }

    // Verifica si los assets están cargados
    public boolean update() {
        return manager.update();
    }

    // Finaliza la carga de todos los assets en cola de manera síncrona
    public void finishLoading() {
        manager.finishLoading();
    }

    // Obtiene el progreso de carga (de 0 a 1)
    public float getProgress() {
        return manager.getProgress();
    }

    // Acceso a los atlas cargados
    public TextureAtlas getAtlas(String atlasName) {
        return manager.get("textures/" + atlasName + ".atlas", TextureAtlas.class);
    }

    public Music getMusic(String musicName) {
        String path = "music/" + musicName + ".mp3";
        // Si el archivo no está cargado, lo cargamos
        if (!manager.isLoaded(path)) {
            manager.load(path, Music.class);
            manager.finishLoadingAsset(path);
        }
        return manager.get(path, Music.class);
    }

    public Sound getSound(String soundName) {
        String path = "sounds/" + soundName + ".wav";
        if (!manager.isLoaded(path)) {
            manager.load(path, Sound.class);
            manager.finishLoadingAsset(path);
        }
        return manager.get(path, Sound.class);
    }

    public void dispose() {
        manager.dispose();
    }
}
