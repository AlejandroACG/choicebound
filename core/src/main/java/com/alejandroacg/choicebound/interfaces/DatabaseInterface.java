package com.alejandroacg.choicebound.interfaces;

public interface DatabaseInterface {
    void saveUserData(UserDTO userData, Consumer<Void> onSuccess, Consumer<String> onError);
    void readUserData(String uid, Consumer<UserDTO> onSuccess, Consumer<String> onError);
    void doesUserExist(String uid, Consumer<Boolean> onSuccess, Consumer<String> onError);

    class UserDTO {
        public String username;
        public String uid;

        public UserDTO() {}
        public UserDTO(String username, String uid) {
            this.username = username;
            this.uid = uid;
        }
    }

    @FunctionalInterface
    interface Consumer<T> {
        void accept(T value);
    }
}
