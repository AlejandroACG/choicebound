package com.alejandroacg.choicebound.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.async.AsyncExecutor;

public class ResourceManager {
    private final AssetManager manager;
    private final AsyncExecutor asyncExecutor;

    public ResourceManager() {
        manager = new AssetManager();
        asyncExecutor = new AsyncExecutor(1);
    }

    public void loadSplashAssets() {
        manager.load("textures/intro.atlas", TextureAtlas.class);
        manager.load("textures/ui_pre_loaded.atlas", TextureAtlas.class);
        manager.load("music/splash_screen.mp3", Music.class);
    }

    public void loadGameAssets() {
        manager.load("textures/ui.atlas", TextureAtlas.class);
        manager.load("textures/covers.atlas", TextureAtlas.class);
        manager.load("music/main_menu.mp3", Music.class);
        manager.load("music/adventure0_main.mp3", Music.class);
        manager.load("music/adventure0_death.mp3", Music.class);
        manager.load("sounds/ui_click.wav", Sound.class);
    }

    public void loadAdventureArt(String adventure, Runnable onComplete) {
        String atlasPath = "textures/" + adventure + "_art.atlas";
        if (!manager.isLoaded(atlasPath)) {
            manager.load(atlasPath, TextureAtlas.class);
            Gdx.app.postRunnable(() -> {
                manager.finishLoadingAsset(atlasPath);
                onComplete.run();
            });
        } else {
            Gdx.app.postRunnable(onComplete);
        }
    }

    public void unloadAdventureArt(String adventure) {
        String atlasPath = "textures/" + adventure + "_art.atlas";
        if (manager.isLoaded(atlasPath)) {
            manager.unload(atlasPath);
        }
    }

    public boolean update() {
        return manager.update();
    }

    public void finishLoading() {
        manager.finishLoading();
    }

    public float getProgress() {
        return manager.getProgress();
    }

    public TextureAtlas getAtlas(String atlasName) {
        return manager.get("textures/" + atlasName + ".atlas", TextureAtlas.class);
    }

    public Music getMusic(String musicName) {
        String path = "music/" + musicName + ".mp3";
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
        asyncExecutor.dispose();
    }
}
