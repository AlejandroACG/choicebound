package com.alejandroacg.choicebound.data;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.interfaces.DatabaseInterface;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.badlogic.gdx.Gdx;

public class UserDataManager {
    private final ChoiceboundGame game;

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
        // Verificar que el uid no sea null
        String uid = localUser.getUid();
        if (uid == null) {
            Gdx.app.error("UserDataManager", "No se puede guardar usuario, UID es null");
            onError.accept("No se puede guardar usuario, UID es null");
            return;
        }

        // Mapear LocalUser a UserDTO
        DatabaseInterface.UserDTO userDTO = new DatabaseInterface.UserDTO(localUser.getUsername(), uid);

        // Guardar en Firestore
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
}
