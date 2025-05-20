package com.alejandroacg.choicebound.utils;

import com.badlogic.gdx.audio.Music;
import com.alejandroacg.choicebound.resources.ResourceManager;

public class MusicManager {
    private final ResourceManager resourceManager;
    private Music currentMusic;
    private String currentMusicName;

    public MusicManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.currentMusic = null;
        this.currentMusicName = null;
    }

    public void playMusic(String musicName) {
        // No reproducir si la música ya está sonando
        if (isPlaying(musicName)) {
            return;
        }

        // Detener la música actual si está reproduciéndose
        stopMusic();

        // Cargar y reproducir la nueva música
        try {
            currentMusic = resourceManager.getMusic(musicName);
            if (currentMusic != null) {
                currentMusic.setLooping(true);
                currentMusic.play();
                currentMusicName = musicName;
            }
        } catch (Exception e) {
            System.err.println("Error al reproducir música " + musicName + ": " + e.getMessage());
        }
    }

    public boolean isPlaying(String musicName) {
        return currentMusic != null && currentMusicName != null && currentMusicName.equals(musicName) && currentMusic.isPlaying();
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
            currentMusicName = null;
        }
    }

    public void dispose() {
        stopMusic();
    }
}
