package com.alejandroacg.choicebound.android;

import com.alejandroacg.choicebound.interfaces.DatabaseInterface;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.badlogic.gdx.Gdx;

public class FirestoreDatabase implements DatabaseInterface {
    private final FirebaseFirestore db;

    public FirestoreDatabase() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void saveUserData(UserDTO userData, Consumer<Void> onSuccess, Consumer<String> onError) {
        String uid = userData.uid;
        if (uid == null) {
            onError.accept("UID no puede ser nulo");
            return;
        }
        db.collection("users").document(uid)
            .set(userData, SetOptions.merge())
            .addOnSuccessListener(aVoid -> {
                Gdx.app.log("FirestoreDatabase", "Datos guardados: " + uid);
                onSuccess.accept(null);
            })
            .addOnFailureListener(e -> {
                Gdx.app.error("FirestoreDatabase", "Error al guardar: " + e.getMessage());
                onError.accept(e.getMessage());
            });
    }

    @Override
    public void readUserData(String uid, Consumer<UserDTO> onSuccess, Consumer<String> onError) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    UserDTO userData = documentSnapshot.toObject(UserDTO.class);
                    onSuccess.accept(userData);
                } else {
                    onError.accept("No se encontraron datos para el usuario: " + uid);
                }
            })
            .addOnFailureListener(e -> onError.accept("Error al leer: " + e.getMessage()));
    }

    @Override
    public void doesUserExist(String uid, Consumer<Boolean> onSuccess, Consumer<String> onError) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener(documentSnapshot -> onSuccess.accept(documentSnapshot.exists()))
            .addOnFailureListener(e -> onError.accept("Error al comprobar existencia de usuario: " + e.getMessage()));
    }
}
