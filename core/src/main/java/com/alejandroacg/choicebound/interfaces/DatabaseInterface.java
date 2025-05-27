package com.alejandroacg.choicebound.interfaces;

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
        public int current_lives;
        public int current_hero;
        public int current_coward;
        public int current_killer;

        public ProgressDTO() {}
        public ProgressDTO(boolean unlocked, String current_node, int current_lives, int current_hero, int current_coward, int current_killer) {
            this.unlocked = unlocked;
            this.current_node = current_node;
            this.current_lives = current_lives;
            this.current_hero = current_hero;
            this.current_coward = current_coward;
            this.current_killer = current_killer;
        }
    }

    class AdventureDTO {
        public String title_es;
        public String title_en;
        public String cover;
        public int initial_hero;
        public int initial_coward;
        public int initial_killer;
        public int initial_lives;

        public AdventureDTO() {}
        public AdventureDTO(String title_es, String title_en, String cover, int initial_hero, int initial_coward, int initial_killer, int initial_lives) {
            this.title_es = title_es;
            this.title_en = title_en;
            this.cover = cover;
            this.initial_hero = initial_hero;
            this.initial_coward = initial_coward;
            this.initial_killer = initial_killer;
            this.initial_lives = initial_lives;
        }
    }

    class NodeDTO {
        public String text_en;
        public String text_es;
        public String image;
        public String music; // Nuevo atributo

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
        public Integer modifier_life;
        public Integer modifier_hero;
        public Integer modifier_coward;
        public Integer modifier_killer;

        public ChoiceDTO() {}
        public ChoiceDTO(String text_en, String text_es, String next_node_id, Integer modifier_life, Integer modifier_hero, Integer modifier_coward, Integer modifier_killer) {
            this.text_en = text_en;
            this.text_es = text_es;
            this.next_node_id = next_node_id;
            this.modifier_life = modifier_life;
            this.modifier_hero = modifier_hero;
            this.modifier_coward = modifier_coward;
            this.modifier_killer = modifier_killer;
        }
    }

    @FunctionalInterface
    interface Consumer<T> {
        void accept(T value);
    }
}
