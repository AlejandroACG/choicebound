package com.alejandroacg.choicebound.data;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.interfaces.DatabaseInterface;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.Map;

public class NodeDataManager {
    private final ChoiceboundGame game;
    private final String currentLanguage = "es";

    public NodeDataManager(ChoiceboundGame game) {
        this.game = game;
    }

    public void loadNode(String adventureId, String nodeId, DatabaseInterface.Consumer<LocalNode> onSuccess, DatabaseInterface.Consumer<String> onError) {
        game.getDatabase().readNode(
            adventureId,
            nodeId,
            nodeDTO -> {
                game.getDatabase().readChoices(
                    adventureId,
                    nodeId,
                    choicesEntries -> {
                        Map<String, LocalNode.LocalChoice> choices = new HashMap<>();
                        for (Map.Entry<String, DatabaseInterface.ChoiceDTO> entry : choicesEntries) {
                            String choiceId = entry.getKey();
                            DatabaseInterface.ChoiceDTO choiceDTO = entry.getValue();
                            String choiceText = GameConfig.getCurrentLanguage().equals("es") ? choiceDTO.text_es : choiceDTO.text_en;

                            LocalNode.LocalChoice choice = new LocalNode.LocalChoice(
                                choiceId,
                                choiceText,
                                choiceDTO.next_node_id,
                                choiceDTO.modifier_values,
                                choiceDTO.trigger_to_set,
                                choiceDTO.trigger_to_remove,
                                choiceDTO.condition_values,
                                choiceDTO.condition_triggers_positive,
                                choiceDTO.condition_triggers_negative
                            );

                            choices.put(choiceId, choice);
                        }

                        String narrativeText = GameConfig.getCurrentLanguage().equals("es") ? nodeDTO.text_es : nodeDTO.text_en;

                        LocalNode node = new LocalNode(
                            nodeId,
                            adventureId,
                            narrativeText,
                            nodeDTO.image,
                            nodeDTO.music,
                            choices
                        );

                        Gdx.app.log("NodeDataManager", "Nodo cargado con éxito: " + nodeId);
                        onSuccess.accept(node);
                    },
                    error -> {
                        Gdx.app.error("NodeDataManager", "Error al cargar opciones del nodo: " + error);
                        onError.accept(error);
                    }
                );
            },
            error -> {
                Gdx.app.error("NodeDataManager", "Error al cargar nodo: " + error);
                onError.accept(error);
            }
        );
    }
}
