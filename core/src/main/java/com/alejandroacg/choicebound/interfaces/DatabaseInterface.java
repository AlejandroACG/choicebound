package com.alejandroacg.choicebound.interfaces;

import java.util.List;

public interface DatabaseInterface {
    void saveUserData(String uid, UserDTO userData, Consumer<Void> onSuccess, Consumer<String> onError);
    void readUserData(String uid, Consumer<UserDTO> onSuccess, Consumer<String> onError);
    void doesUserExist(String uid, Consumer<Boolean> onSuccess, Consumer<String> onError);
    void deleteUserData(String uid, Consumer<Void> onSuccess, Consumer<String> onError);
    void readAllAdventures(Consumer<List<AdventureDTO>> onSuccess, Consumer<String> onError);

    class UserDTO {
        public String username;
        public String pref_language;

        public UserDTO() {}
        public UserDTO(String username, String pref_language) {
            this.username = username;
            this.pref_language = pref_language;
        }
    }

    class AdventureDTO {
        public boolean acquired;
        public String title_es;
        public String title_en;
        public String cover;

        public AdventureDTO() {}
        public AdventureDTO(boolean acquired, String title_es, String title_en, String cover) {
            this.acquired = acquired;
            this.title_es = title_es;
            this.title_en = title_en;
            this.cover = cover;
        }
    }

    @FunctionalInterface
    interface Consumer<T> {
        void accept(T value);
    }
}
