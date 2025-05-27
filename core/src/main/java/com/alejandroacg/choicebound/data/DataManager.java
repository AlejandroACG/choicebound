package com.alejandroacg.choicebound.data;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.interfaces.DatabaseInterface;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.badlogic.gdx.Gdx;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private final ChoiceboundGame game;

    public DataManager(ChoiceboundGame game) {
        this.game = game;
    }

    public void loadUserDataFromFirestore(Runnable onSuccess, DatabaseInterface.Consumer<String> onError) {
        String uid = game.getPlatformBridge().getCurrentUserId();
        if (uid != null) {
            game.getDatabase().readUserData(
                uid,
                userDTO -> {
                    game.getLocalUser().setUsername(userDTO.username);
                    game.getLocalUser().setUid(uid);
                    Gdx.app.log("UserDataManager", "Datos de usuario cargados desde Firestore: " + userDTO.username);
                    onSuccess.run();
                },
                error -> {
                    Gdx.app.error("UserDataManager", "Error al cargar datos desde Firestore: " + error);
                    onError.accept(error);
                }
            );
        } else {
            Gdx.app.error("UserDataManager", "No se pudo cargar usuario, UID es null");
            game.getPlatformBridge().signOut();
            onError.accept("No se pudo cargar usuario, UID es null");
        }
    }

    public void saveUserData(LocalUser localUser, Runnable onSuccess, DatabaseInterface.Consumer<String> onError) {
        String uid = localUser.getUid();
        if (uid == null) {
            Gdx.app.error("UserDataManager", "No se puede guardar usuario, UID es null");
            onError.accept("No se puede guardar usuario, UID es null");
            return;
        }

        DatabaseInterface.UserDTO userDTO = new DatabaseInterface.UserDTO(localUser.getUsername(), uid);

        game.getDatabase().saveUserData(
            userDTO,
            success -> {
                Gdx.app.log("UserDataManager", "Usuario guardado con éxito: " + uid);
                onSuccess.run();
            },
            error -> {
                Gdx.app.error("UserDataManager", "Error al guardar usuario: " + error);
                onError.accept(error);
            }
        );
    }

    public void deleteUserData(Runnable onSuccess, DatabaseInterface.Consumer<String> onError) {
        String uid = game.getLocalUser().getUid();
        if (uid == null) {
            Gdx.app.error("UserDataManager", "No se puede eliminar usuario, UID es null");
            onError.accept("No se puede eliminar usuario, UID es null");
            return;
        }

        game.getDatabase().deleteUserData(
            uid,
            success -> {
                Gdx.app.log("UserDataManager", "Usuario y subcolecciones eliminados con éxito: " + uid);
                game.clearLocalUser();
                onSuccess.run();
            },
            error -> {
                Gdx.app.error("UserDataManager", "Error al eliminar usuario: " + error);
                onError.accept(error);
            }
        );
    }

    public void loadAllAdventures(DatabaseInterface.Consumer<List<LocalAdventure>> onSuccess, DatabaseInterface.Consumer<String> onError) {
        game.getDatabase().readAllAdventures(
            adventureDTOs -> {
                List<LocalAdventure> adventures = new ArrayList<>();
                for (DatabaseInterface.AdventureDTO dto : adventureDTOs) {
                    // Seleccionar el título dinámicamente según el idioma actual
                    String title = null;
                    try {
                        Field titleField = DatabaseInterface.AdventureDTO.class.getDeclaredField("title_" + GameConfig.getCurrentLanguage());
                        titleField.setAccessible(true);
                        title = (String) titleField.get(dto);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        Gdx.app.log("UserDataManager", "Idioma no encontrado: " + GameConfig.getCurrentLanguage() + ", usando español como fallback");
                        title = dto.title_es;
                    }
                    if (title == null) {
                        Gdx.app.log("UserDataManager", "Título no encontrado para idioma: " + GameConfig.getCurrentLanguage());
                        title = dto.title_es;
                    }
                    LocalAdventure adventure = new LocalAdventure(
                        dto.uid,
                        dto.acquired,
                        title,
                        dto.cover
                    );
                    adventures.add(adventure);
                }
                Gdx.app.log("UserDataManager", "Aventuras cargadas con éxito: " + adventures.size());
                onSuccess.accept(adventures);
            },
            error -> {
                Gdx.app.error("UserDataManager", "Error al cargar aventuras: " + error);
                onError.accept(error);
            }
        );
    }
}
