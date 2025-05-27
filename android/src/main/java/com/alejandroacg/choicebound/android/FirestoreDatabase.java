package com.alejandroacg.choicebound.android;

import com.alejandroacg.choicebound.interfaces.DatabaseInterface;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class FirestoreDatabase implements DatabaseInterface {
    private final FirebaseFirestore db;

    public FirestoreDatabase() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void saveUserData(String uid, UserDTO userData, Consumer<Void> onSuccess, Consumer<String> onError) {
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

    @Override
    public void deleteUserData(String uid, Consumer<Void> onSuccess, Consumer<String> onError) {
        db.collection("users").document(uid).collection("Progress")
            .get()
            .addOnSuccessListener(progressSnapshots -> {
                for (var doc : progressSnapshots.getDocuments()) {
                    doc.getReference().delete();
                }
                db.collection("users").document(uid).collection("Settings")
                    .get()
                    .addOnSuccessListener(settingsSnapshots -> {
                        for (var doc : settingsSnapshots.getDocuments()) {
                            doc.getReference().delete();
                        }
                        db.collection("users").document(uid)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Gdx.app.log("FirestoreDatabase", "Usuario eliminado con éxito: " + uid);
                                onSuccess.accept(null);
                            })
                            .addOnFailureListener(e -> {
                                Gdx.app.error("FirestoreDatabase", "Error al eliminar usuario: " + e.getMessage());
                                onError.accept(e.getMessage());
                            });
                    })
                    .addOnFailureListener(e -> {
                        Gdx.app.error("FirestoreDatabase", "Error al eliminar subcolección Settings: " + e.getMessage());
                        onError.accept(e.getMessage());
                    });
            })
            .addOnFailureListener(e -> {
                Gdx.app.error("FirestoreDatabase", "Error al eliminar subcolección Progress: " + e.getMessage());
                onError.accept(e.getMessage());
            });
    }

    @Override
    public void readAllAdventures(Consumer<List<AdventureDTO>> onSuccess, Consumer<String> onError) {
        db.collection("adventures")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<AdventureDTO> adventures = new ArrayList<>();
                Gdx.app.log("FirestoreDatabase", "Documentos encontrados en adventures: " + querySnapshot.getDocuments().size());
                for (var doc : querySnapshot.getDocuments()) {
                    Gdx.app.log("FirestoreDatabase", "Documento: " + doc.getId() + ", Datos: " + doc.getData());
                    AdventureDTO adventure = doc.toObject(AdventureDTO.class);
                    if (adventure != null) {
                        adventures.add(adventure);
                        Gdx.app.log("FirestoreDatabase", "Aventura mapeada: " + adventure.title_es);
                    } else {
                        Gdx.app.log("FirestoreDatabase", "Aventura nula para documento: " + doc.getId());
                    }
                }
                Gdx.app.log("FirestoreDatabase", "Aventuras cargadas con éxito: " + adventures.size());
                onSuccess.accept(adventures);
            })
            .addOnFailureListener(e -> {
                Gdx.app.error("FirestoreDatabase", "Error al cargar aventuras: " + e.getMessage());
                onError.accept(e.getMessage());
            });
    }
}
