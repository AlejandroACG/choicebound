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

    // Siempre detiene lo que esté sonando y reproduce la nueva música
    public void playExclusive(String musicName) {
        stop();
        playInternal(musicName);
    }

    // Solo cambia la música si la que se solicita es diferente
    public void playIfDifferent(String musicName) {
        if (!isPlaying(musicName)) {
            stop();
            playInternal(musicName);
        }
    }

    // Detiene toda la música
    public void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
            currentMusicName = null;
        }
    }

    private void playInternal(String musicName) {
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

    public void dispose() {
        stop();
    }
}
