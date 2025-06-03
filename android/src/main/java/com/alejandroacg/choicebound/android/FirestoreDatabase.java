package com.alejandroacg.choicebound.android;

import com.alejandroacg.choicebound.interfaces.DatabaseInterface;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.badlogic.gdx.Gdx;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                Gdx.app.error("FirestoreDatabase", "Error al eliminar subcolección Progress: " + e.getMessage());
                onError.accept(e.getMessage());
            });
    }

    @Override
    public void saveUserProgress(String uid, String adventureId, ProgressDTO progressDTO, Consumer<Void> onSuccess, Consumer<String> onError) {
        if (uid == null || adventureId == null) {
            onError.accept("UID o adventureId no pueden ser nulos");
            return;
        }
        db.collection("users").document(uid).collection("Progress").document(adventureId)
            .set(progressDTO, SetOptions.merge())
            .addOnSuccessListener(aVoid -> {
                Gdx.app.log("FirestoreDatabase", "Progreso guardado para aventura: " + adventureId);
                onSuccess.accept(null);
            })
            .addOnFailureListener(e -> {
                Gdx.app.error("FirestoreDatabase", "Error al guardar progreso: " + e.getMessage());
                onError.accept(e.getMessage());
            });
    }

    @Override
    public void readUserProgress(String uid, Consumer<List<Map.Entry<String, ProgressDTO>>> onSuccess, Consumer<String> onError) {
        db.collection("users").document(uid).collection("Progress")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map.Entry<String, ProgressDTO>> progressEntries = new ArrayList<>();
                Gdx.app.log("FirestoreDatabase", "Documentos encontrados en Progress: " + querySnapshot.getDocuments().size());
                for (var doc : querySnapshot.getDocuments()) {
                    Gdx.app.log("FirestoreDatabase", "Documento: " + doc.getId() + ", Datos: " + doc.getData());
                    ProgressDTO progress = doc.toObject(ProgressDTO.class);
                    if (progress != null) {
                        progressEntries.add(new AbstractMap.SimpleEntry<>(doc.getId(), progress));
                        Gdx.app.log("FirestoreDatabase", "Progreso mapeado para aventura: " + doc.getId());
                    } else {
                        Gdx.app.log("FirestoreDatabase", "Progreso nulo para documento: " + doc.getId());
                    }
                }
                Gdx.app.log("FirestoreDatabase", "Progresos cargados con éxito: " + progressEntries.size());
                onSuccess.accept(progressEntries);
            })
            .addOnFailureListener(e -> {
                Gdx.app.error("FirestoreDatabase", "Error al cargar progresos: " + e.getMessage());
                onError.accept(e.getMessage());
            });
    }

    @Override
    public void readAllAdventures(Consumer<List<Map.Entry<String, AdventureDTO>>> onSuccess, Consumer<String> onError) {
        db.collection("adventures")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map.Entry<String, AdventureDTO>> adventures = new ArrayList<>();
                Gdx.app.log("FirestoreDatabase", "Documentos encontrados en adventures: " + querySnapshot.getDocuments().size());
                for (var doc : querySnapshot.getDocuments()) {
                    Gdx.app.log("FirestoreDatabase", "Documento: " + doc.getId() + ", Datos: " + doc.getData());
                    AdventureDTO adventure = doc.toObject(AdventureDTO.class);
                    if (adventure != null) {
                        adventures.add(new AbstractMap.SimpleEntry<>(doc.getId(), adventure));
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

    @Override
    public void readAdventure(String adventureId, Consumer<AdventureDTO> onSuccess, Consumer<String> onError) {
        db.collection("adventures").document(adventureId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    AdventureDTO adventure = documentSnapshot.toObject(AdventureDTO.class);
                    Gdx.app.log("FirestoreDatabase", "Aventura cargada: " + adventureId);
                    onSuccess.accept(adventure);
                } else {
                    Gdx.app.error("FirestoreDatabase", "No se encontró la aventura: " + adventureId);
                    onError.accept("No se encontró la aventura: " + adventureId);
                }
            })
            .addOnFailureListener(e -> {
                Gdx.app.error("FirestoreDatabase", "Error al cargar aventura: " + e.getMessage());
                onError.accept(e.getMessage());
            });
    }

    @Override
    public void readNode(String adventureId, String nodeId, Consumer<NodeDTO> onSuccess, Consumer<String> onError) {
        db.collection("adventures").document(adventureId).collection("nodes").document(nodeId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    NodeDTO node = documentSnapshot.toObject(NodeDTO.class);
                    Gdx.app.log("FirestoreDatabase", "Nodo cargado: " + nodeId);
                    onSuccess.accept(node);
                } else {
                    Gdx.app.error("FirestoreDatabase", "No se encontró el nodo: " + nodeId);
                    onError.accept("No se encontró el nodo: " + nodeId);
                }
            })
            .addOnFailureListener(e -> {
                Gdx.app.error("FirestoreDatabase", "Error al cargar nodo: " + e.getMessage());
                onError.accept(e.getMessage());
            });
    }

    @Override
    public void readChoices(String adventureId, String nodeId, Consumer<List<Map.Entry<String, ChoiceDTO>>> onSuccess, Consumer<String> onError) {
        db.collection("adventures").document(adventureId).collection("nodes").document(nodeId).collection("choices")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map.Entry<String, ChoiceDTO>> choices = new ArrayList<>();
                for (var doc : querySnapshot.getDocuments()) {
                    ChoiceDTO choice = doc.toObject(ChoiceDTO.class);
                    if (choice != null) {
                        choices.add(new AbstractMap.SimpleEntry<>(doc.getId(), choice));
                    }
                }
                Gdx.app.log("FirestoreDatabase", "Opciones cargadas para nodo " + nodeId + ": " + choices.size());
                onSuccess.accept(choices);
            })
            .addOnFailureListener(e -> {
                Gdx.app.error("FirestoreDatabase", "Error al cargar opciones: " + e.getMessage());
                onError.accept(e.getMessage());
            });
    }
}
