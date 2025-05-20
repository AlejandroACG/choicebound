package com.alejandroacg.choicebound.utils;

import com.badlogic.gdx.audio.Music;
import com.alejandroacg.choicebound.resources.ResourceManager;

public class MusicManager {
    private final ResourceManager resourceManager;
    private Music currentMusic;

    public MusicManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.currentMusic = null;
    }

    public void playMusic(String musicName) {
        // Detener la música actual si está reproduciéndose
        stopMusic();

        // Cargar y reproducir la nueva música
        try {
            currentMusic = resourceManager.getMusic(musicName);
            if (currentMusic != null) {
                currentMusic.setLooping(true);
                currentMusic.play();
            }
        } catch (Exception e) {
            System.err.println("Error al reproducir música " + musicName + ": " + e.getMessage());
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    public void dispose() {
        stopMusic();
    }
}
