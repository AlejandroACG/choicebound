package com.alejandroacg.choicebound.data;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.interfaces.DatabaseInterface;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UserDataManager {
    private final ChoiceboundGame game;
    private final String currentLanguage = "es";

    public UserDataManager(ChoiceboundGame game) {
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
                    game.getLocalUser().setPrefLanguage(userDTO.pref_language);
                    GameConfig.setCurrentLanguage(userDTO.pref_language);
                    Gdx.app.log("UserDataManager", "Datos de usuario cargados desde Firestore: " + userDTO.username);

                    game.getDatabase().readUserProgress(
                        uid,
                        progressEntries -> {
                            Map<String, LocalUser.LocalProgress> progressMap = new HashMap<>();
                            for (Map.Entry<String, DatabaseInterface.ProgressDTO> entry : progressEntries) {
                                DatabaseInterface.ProgressDTO progressDTO = entry.getValue();
                                String adventureId = entry.getKey();
                                LocalUser.LocalProgress progress = new LocalUser.LocalProgress(
                                    adventureId,
                                    progressDTO.unlocked,
                                    progressDTO.current_node,
                                    progressDTO.current_values
                                );
                                progress.setLastNode(progressDTO.last_node);
                                progress.setTriggers(new HashSet<>(progressDTO.triggers));
                                progressMap.put(adventureId, progress);
                            }
                            game.getLocalUser().setProgress(progressMap);
                            Gdx.app.log("UserDataManager", "Progresos cargados con éxito: " + progressMap.size());
                            onSuccess.run();
                        },
                        error -> {
                            Gdx.app.error("UserDataManager", "Error al cargar progresos: " + error);
                            onError.accept(error);
                        }
                    );
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

        DatabaseInterface.UserDTO userDTO = new DatabaseInterface.UserDTO(
            localUser.getUsername(),
            localUser.getPrefLanguage()
        );

        game.getDatabase().saveUserData(
            uid,
            userDTO,
            success -> {
                Map<String, LocalUser.LocalProgress> progressMap = localUser.getProgress();
                if (progressMap != null && !progressMap.isEmpty()) {
                    int[] count = {0};
                    int total = progressMap.size();
                    for (Map.Entry<String, LocalUser.LocalProgress> entry : progressMap.entrySet()) {
                        String adventureId = entry.getKey();
                        LocalUser.LocalProgress progress = entry.getValue();
                        progress.setAdventureId(adventureId);
                        DatabaseInterface.ProgressDTO progressDTO = new DatabaseInterface.ProgressDTO(
                            progress.isUnlocked(),
                            progress.getCurrentNode(),
                            progress.getLastNode(),
                            progress.getCurrentValues(),
                            new ArrayList<>(progress.getTriggers())
                        );
                        game.getDatabase().saveUserProgress(
                            uid,
                            adventureId,
                            progressDTO,
                            successProgress -> {
                                count[0]++;
                                if (count[0] == total) {
                                    Gdx.app.log("UserDataManager", "Usuario y progresos guardados con éxito: " + uid);
                                    onSuccess.run();
                                }
                            },
                            error -> {
                                Gdx.app.error("UserDataManager", "Error al guardar progreso para aventura " + adventureId + ": " + error);
                                onError.accept(error);
                            }
                        );
                    }
                } else {
                    Gdx.app.log("UserDataManager", "Usuario guardado con éxito (sin progresos): " + uid);
                    onSuccess.run();
                }
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
}
