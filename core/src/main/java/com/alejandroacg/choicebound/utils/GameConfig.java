package com.alejandroacg.choicebound.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class GameConfig {
    private static I18NBundle strings;
    public static final float HEADER_HEIGHT_RATIO = 0.20f;
    @Getter
    private static String currentLanguage;

    public static void initialize() {
        I18NBundle.setSimpleFormatter(true);

        // Obtener el idioma del sistema
        String systemLanguage = Locale.getDefault().getLanguage(); // Ej. "es", "en"
        Gdx.app.log("GameConfig", "Idioma del sistema detectado: " + systemLanguage);

        // Idiomas soportados
        String[] supportedLanguages = {"es", "en"};
        String selectedLanguage = "en";

        // Verificar si el idioma del sistema está soportado
        for (String lang : supportedLanguages) {
            if (systemLanguage.equals(lang)) {
                selectedLanguage = lang;
                break;
            }
        }

        currentLanguage = selectedLanguage;
        Gdx.app.log("GameConfig", "Idioma seleccionado: " + currentLanguage);

        // Cargar el archivo de idioma correspondiente
        strings = I18NBundle.createBundle(
            Gdx.files.internal("i18n/strings_" + currentLanguage),
            new Locale(currentLanguage),
            StandardCharsets.UTF_8.name()
        );
    }

    public static String getString(String key) {
        return strings.get(key);
    }
}
