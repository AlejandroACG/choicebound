package com.alejandroacg.choicebound.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class GameConfig {
    private static I18NBundle strings;
    public static final float HEADER_HEIGHT_RATIO = 0.20f;

    public static void initialize() {
        I18NBundle.setSimpleFormatter(true);
        strings = I18NBundle.createBundle(Gdx.files.internal("i18n/strings"), new Locale("es"), StandardCharsets.UTF_8.name());
    }

    public static String getString(String key) {
        return strings.get(key);
    }
}
