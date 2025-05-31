package com.alejandroacg.choicebound.interfaces;

import java.util.ArrayList;
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
    void readChoices(String adventureId, String nodeId, Consumer<List<ChoiceDTO>> onSuccess, Consumer<String> onError);

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
        public int current_hero;
        public int current_coward;
        public int current_killer;
        public List<String> triggers;

        public ProgressDTO() {}
        public ProgressDTO(boolean unlocked, String current_node, int current_hero, int current_coward, int current_killer) {
            this.unlocked = unlocked;
            this.current_node = current_node;
            this.current_hero = current_hero;
            this.current_coward = current_coward;
            this.current_killer = current_killer;
            this.triggers = new ArrayList<>();
        }

        public ProgressDTO(boolean unlocked, String current_node, int current_hero, int current_coward, int current_killer, List<String> triggers) {
            this.unlocked = unlocked;
            this.current_node = current_node;
            this.current_hero = current_hero;
            this.current_coward = current_coward;
            this.current_killer = current_killer;
            this.triggers = triggers != null ? triggers : new ArrayList<>();
        }
    }

    class AdventureDTO {
        public String title_es;
        public String title_en;
        public String cover;
        public int initial_hero;
        public int initial_coward;
        public int initial_killer;

        public AdventureDTO() {}
        public AdventureDTO(String title_es, String title_en, String cover, int initial_hero, int initial_coward, int initial_killer) {
            this.title_es = title_es;
            this.title_en = title_en;
            this.cover = cover;
            this.initial_hero = initial_hero;
            this.initial_coward = initial_coward;
            this.initial_killer = initial_killer;
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
        public Integer modifier_hero;
        public Integer modifier_coward;
        public Integer modifier_killer;
        public List<String> trigger_to_set;
        public List<String> trigger_to_remove;
        public Integer condition_hero;
        public Integer condition_coward;
        public Integer condition_killer;
        public List<String> condition_triggers_positive;
        public List<String> condition_triggers_negative;

        public ChoiceDTO() {}
        public ChoiceDTO(String text_en, String text_es, String next_node_id, Integer modifier_hero, Integer modifier_coward, Integer modifier_killer) {
            this.text_en = text_en;
            this.text_es = text_es;
            this.next_node_id = next_node_id;
            this.modifier_hero = modifier_hero;
            this.modifier_coward = modifier_coward;
            this.modifier_killer = modifier_killer;
            this.trigger_to_set = new ArrayList<>();
            this.trigger_to_remove = new ArrayList<>();
            this.condition_triggers_positive = new ArrayList<>();
            this.condition_triggers_negative = new ArrayList<>();
        }

        public ChoiceDTO(String text_en, String text_es, String next_node_id, Integer modifier_hero, Integer modifier_coward, Integer modifier_killer,
                         List<String> trigger_to_set, List<String> trigger_to_remove, Integer condition_hero, Integer condition_coward, Integer condition_killer,
                         List<String> condition_triggers_positive, List<String> condition_triggers_negative) {
            this.text_en = text_en;
            this.text_es = text_es;
            this.next_node_id = next_node_id;
            this.modifier_hero = modifier_hero;
            this.modifier_coward = modifier_coward;
            this.modifier_killer = modifier_killer;
            this.trigger_to_set = trigger_to_set != null ? trigger_to_set : new ArrayList<>();
            this.trigger_to_remove = trigger_to_remove != null ? trigger_to_remove : new ArrayList<>();
            this.condition_hero = condition_hero;
            this.condition_coward = condition_coward;
            this.condition_killer = condition_killer;
            this.condition_triggers_positive = condition_triggers_positive != null ? condition_triggers_positive : new ArrayList<>();
            this.condition_triggers_negative = condition_triggers_negative != null ? condition_triggers_negative : new ArrayList<>();
        }
    }

    @FunctionalInterface
    interface Consumer<T> {
        void accept(T value);
    }
}
