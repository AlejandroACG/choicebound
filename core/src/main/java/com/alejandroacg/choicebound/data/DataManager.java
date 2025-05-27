package com.alejandroacg.choicebound.data;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.interfaces.DatabaseInterface;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.badlogic.gdx.Gdx;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private final ChoiceboundGame game;
    private final String currentLanguage = "es";

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
                                    progressDTO.current_lives,
                                    progressDTO.current_hero,
                                    progressDTO.current_coward,
                                    progressDTO.current_killer
                                );
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
                        DatabaseInterface.ProgressDTO progressDTO = new DatabaseInterface.ProgressDTO(
                            progress.isUnlocked(),
                            progress.getCurrentNode(),
                            progress.getCurrentLives(),
                            progress.getCurrentHero(),
                            progress.getCurrentCoward(),
                            progress.getCurrentKiller()
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

    public void loadAllAdventures(DatabaseInterface.Consumer<List<LocalAdventure>> onSuccess, DatabaseInterface.Consumer<String> onError) {
        game.getDatabase().readAllAdventures(
            adventureEntries -> {
                List<LocalAdventure> adventures = new ArrayList<>();
                for (Map.Entry<String, DatabaseInterface.AdventureDTO> entry : adventureEntries) {
                    DatabaseInterface.AdventureDTO dto = entry.getValue();
                    String uid = entry.getKey();
                    String title = null;
                    try {
                        Field titleField = DatabaseInterface.AdventureDTO.class.getDeclaredField("title_" + currentLanguage);
                        titleField.setAccessible(true);
                        title = (String) titleField.get(dto);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        Gdx.app.log("UserDataManager", "Idioma no encontrado: " + currentLanguage + ", usando español como fallback");
                        title = dto.title_es;
                    }
                    if (title == null) {
                        Gdx.app.log("UserDataManager", "Título no encontrado para idioma: " + currentLanguage);
                        title = dto.title_es;
                    }
                    LocalAdventure adventure = new LocalAdventure(
                        uid,
                        title,
                        dto.cover,
                        dto.initial_hero,
                        dto.initial_coward,
                        dto.initial_killer,
                        dto.initial_lives
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

    public void loadAdventure(String adventureId, DatabaseInterface.Consumer<LocalAdventure> onSuccess, DatabaseInterface.Consumer<String> onError) {
        game.getDatabase().readAdventure(
            adventureId,
            adventureDTO -> {
                String title = null;
                try {
                    Field titleField = DatabaseInterface.AdventureDTO.class.getDeclaredField("title_" + currentLanguage);
                    titleField.setAccessible(true);
                    title = (String) titleField.get(adventureDTO);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    Gdx.app.log("UserDataManager", "Idioma no encontrado: " + currentLanguage + ", usando español como fallback");
                    title = adventureDTO.title_es;
                }
                if (title == null) {
                    Gdx.app.log("UserDataManager", "Título no encontrado para idioma: " + currentLanguage);
                    title = adventureDTO.title_es;
                }
                LocalAdventure adventure = new LocalAdventure(
                    adventureId,
                    title,
                    adventureDTO.cover,
                    adventureDTO.initial_hero,
                    adventureDTO.initial_coward,
                    adventureDTO.initial_killer,
                    adventureDTO.initial_lives
                );
                Gdx.app.log("DataManager", "Aventura cargada con éxito: " + adventureId);
                onSuccess.accept(adventure);
            },
            error -> {
                Gdx.app.error("DataManager", "Error al cargar aventura: " + error);
                onError.accept(error);
            }
        );
    }

    public void loadNode(String adventureId, String nodeId, DatabaseInterface.Consumer<LocalNode> onSuccess, DatabaseInterface.Consumer<String> onError) {
        game.getDatabase().readNode(
            adventureId,
            nodeId,
            nodeDTO -> {
                game.getDatabase().readChoices(
                    adventureId,
                    nodeId,
                    choicesDTO -> {
                        List<LocalNode.LocalChoice> choices = new ArrayList<>();
                        for (DatabaseInterface.ChoiceDTO choiceDTO : choicesDTO) {
                            String choiceText = GameConfig.getCurrentLanguage().equals("es") ? choiceDTO.text_es : choiceDTO.text_en;
                            LocalNode.LocalChoice choice = new LocalNode.LocalChoice(
                                choiceText,
                                choiceDTO.next_node_id,
                                choiceDTO.modifier_life,
                                choiceDTO.modifier_hero,
                                choiceDTO.modifier_coward,
                                choiceDTO.modifier_killer
                            );
                            choices.add(choice);
                        }

                        String narrativeText = GameConfig.getCurrentLanguage().equals("es") ? nodeDTO.text_es : nodeDTO.text_en;
                        LocalNode node = new LocalNode(
                            nodeId,
                            adventureId,
                            narrativeText,
                            nodeDTO.image,
                            nodeDTO.music, // Nuevo campo
                            choices
                        );

                        Gdx.app.log("DataManager", "Nodo cargado con éxito: " + nodeId);
                        onSuccess.accept(node);
                    },
                    error -> {
                        Gdx.app.error("DataManager", "Error al cargar opciones del nodo: " + error);
                        onError.accept(error);
                    }
                );
            },
            error -> {
                Gdx.app.error("DataManager", "Error al cargar nodo: " + error);
                onError.accept(error);
            }
        );
    }
}
