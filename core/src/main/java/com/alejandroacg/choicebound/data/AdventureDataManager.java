package com.alejandroacg.choicebound.data;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.interfaces.DatabaseInterface;
import com.badlogic.gdx.Gdx;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdventureDataManager {
    private final ChoiceboundGame game;

    public AdventureDataManager(ChoiceboundGame game) {
        this.game = game;
    }

    public void loadAllAdventures(DatabaseInterface.Consumer<List<LocalAdventure>> onSuccess, DatabaseInterface.Consumer<String> onError) {
        game.getDatabase().readAllAdventures(
            adventureEntries -> {
                List<LocalAdventure> adventures = new ArrayList<>();
                for (Map.Entry<String, DatabaseInterface.AdventureDTO> entry : adventureEntries) {
                    DatabaseInterface.AdventureDTO dto = entry.getValue();
                    String uid = entry.getKey();
                    String title = getLocalizedTitle(dto);
                    LocalAdventure adventure = new LocalAdventure(
                        uid,
                        title,
                        dto.cover,
                        dto.initial_values
                    );
                    adventures.add(adventure);
                }
                Gdx.app.log("AdventureDataManager", "Aventuras cargadas con éxito: " + adventures.size());
                onSuccess.accept(adventures);
            },
            error -> {
                Gdx.app.error("AdventureDataManager", "Error al cargar aventuras: " + error);
                onError.accept(error);
            }
        );
    }

    public void loadAdventure(String adventureId, DatabaseInterface.Consumer<LocalAdventure> onSuccess, DatabaseInterface.Consumer<String> onError) {
        game.getDatabase().readAdventure(
            adventureId,
            adventureDTO -> {
                String title = getLocalizedTitle(adventureDTO);
                LocalAdventure adventure = new LocalAdventure(
                    adventureId,
                    title,
                    adventureDTO.cover,
                    adventureDTO.initial_values
                );
                Gdx.app.log("AdventureDataManager", "Aventura cargada con éxito: " + adventureId);
                onSuccess.accept(adventure);
            },
            error -> {
                Gdx.app.error("AdventureDataManager", "Error al cargar aventura: " + error);
                onError.accept(error);
            }
        );
    }

    private String getLocalizedTitle(DatabaseInterface.AdventureDTO dto) {
        String lang = game.getLocalUser().getPrefLanguage();
        try {
            Field titleField = DatabaseInterface.AdventureDTO.class.getDeclaredField("title_" + lang);
            titleField.setAccessible(true);
            String title = (String) titleField.get(dto);
            if (title != null) return title;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Gdx.app.log("AdventureDataManager", "Campo title_" + lang + " no encontrado. Usando title_es como fallback.");
        }
        return dto.title_es != null ? dto.title_es : "Título desconocido";
    }
}
