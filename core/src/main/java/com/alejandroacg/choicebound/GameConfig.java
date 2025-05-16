package com.alejandroacg.choicebound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class GameConfig {
    private static I18NBundle strings;

    public static void initialize() {
        strings = I18NBundle.createBundle(Gdx.files.internal("strings"), new Locale("es"));
    }

    public static String getString(String key) {
        return strings.get(key);
    }
}
