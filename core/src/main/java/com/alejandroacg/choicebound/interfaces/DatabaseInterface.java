package com.alejandroacg.choicebound.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DatabaseInterface {
    void saveUserData(String uid, UserDTO userData, Consumer<Void> onSuccess, Consumer<String> onError);
    void readUserData(String uid, Consumer<UserDTO> onSuccess, Consumer<String> onError);
    void doesUserExist(String uid, Consumer<Boolean> onSuccess, Consumer<String> onError);
    void deleteUserData(String uid, Consumer<Void> onSuccess, Consumer<String> onError);
    void saveUserProgress(String uid, String adventureId, ProgressDTO progressDTO, Consumer<Void> onSuccess, Consumer<String> onError);
    void readUserProgress(String uid, Consumer<List<Map.Entry<String, ProgressDTO>>> onSuccess, Consumer<String> onError);
    void readAllAdventures(Consumer<List<Map.Entry<String, AdventureDTO>>> onSuccess, Consumer<String> onError);
    void readAdventure(String adventureId, Consumer<AdventureDTO> onSuccess, Consumer<String> onError);
    void readNode(String adventureId, String nodeId, Consumer<NodeDTO> onSuccess, Consumer<String> onError);
    void readChoices(String adventureId, String nodeId, Consumer<List<Map.Entry<String, ChoiceDTO>>> onSuccess, Consumer<String> onError);

    class UserDTO {
        public String username;
        public String pref_language;

        public UserDTO() {}

        public UserDTO(String username, String pref_language) {
            this.username = username;
            this.pref_language = pref_language;
        }
    }

    class ProgressDTO {
        public boolean unlocked;
        public String current_node;
        public String last_node;
        public Map<String, Integer> current_values;
        public List<String> triggers;

        public ProgressDTO() {}

        public ProgressDTO(boolean unlocked, String current_node, String last_node, Map<String, Integer> current_values, List<String> triggers) {
            this.unlocked = unlocked;
            this.current_node = current_node;
            this.last_node = last_node;
            this.current_values = current_values != null ? current_values : new HashMap<>();
            this.triggers = triggers != null ? triggers : new ArrayList<>();
        }
    }

    class AdventureDTO {
        public String title_es;
        public String title_en;
        public String cover;
        public Map<String, Integer> initial_values;

        public AdventureDTO() {}
        public AdventureDTO(String title_es, String title_en, String cover, Map<String, Integer> initial_values) {
            this.title_es = title_es;
            this.title_en = title_en;
            this.cover = cover;
            this.initial_values = initial_values != null ? initial_values : new HashMap<>();
        }
    }

    class NodeDTO {
        public String text_en;
        public String text_es;
        public String image;
        public String music;

        public NodeDTO() {}
        public NodeDTO(String text_en, String text_es, String image, String music) {
            this.text_en = text_en;
            this.text_es = text_es;
            this.image = image;
            this.music = music;
        }
    }

    class ChoiceDTO {
        public String text_en;
        public String text_es;
        public String next_node_id;
        public Map<String, Integer> modifier_values;
        public List<String> trigger_to_set;
        public List<String> trigger_to_remove;
        public String condition_values;
        public List<String> condition_triggers_positive;
        public List<String> condition_triggers_negative;

        public ChoiceDTO() {}
        public ChoiceDTO(String text_en, String text_es, String next_node_id) {
            this.text_en = text_en;
            this.text_es = text_es;
            this.next_node_id = next_node_id;
            this.modifier_values = new HashMap<>();
            this.trigger_to_set = new ArrayList<>();
            this.trigger_to_remove = new ArrayList<>();
            this.condition_values = "";
            this.condition_triggers_positive = new ArrayList<>();
            this.condition_triggers_negative = new ArrayList<>();
        }

        public ChoiceDTO(String text_en, String text_es, String next_node_id, Map<String, Integer> modifier_values,
                         List<String> trigger_to_set, List<String> trigger_to_remove, String condition_values,
                         List<String> condition_triggers_positive, List<String> condition_triggers_negative) {
            this.text_en = text_en;
            this.text_es = text_es;
            this.next_node_id = next_node_id;
            this.modifier_values = modifier_values != null ? modifier_values : new HashMap<>();
            this.trigger_to_set = trigger_to_set != null ? trigger_to_set : new ArrayList<>();
            this.trigger_to_remove = trigger_to_remove != null ? trigger_to_remove : new ArrayList<>();
            this.condition_values = condition_values != null ? condition_values : "";
            this.condition_triggers_positive = condition_triggers_positive != null ? condition_triggers_positive : new ArrayList<>();
            this.condition_triggers_negative = condition_triggers_negative != null ? condition_triggers_negative : new ArrayList<>();
        }
    }

    @FunctionalInterface
    interface Consumer<T> {
        void accept(T value);
    }
}
