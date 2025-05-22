package com.alejandroacg.choicebound.interfaces;

public interface PlatformBridge {
    void startOneTapSignIn();
    boolean hasInternetConnection();
    boolean isUserAuthenticated();
    void signOut();
    String getCurrentUserId();
    float getKeyboardHeight();
}
