package com.alejandroacg.choicebound.interfaces;

public interface PlatformBridge {
    void createGoogleButton();
    void destroyGoogleButton();
    boolean hasInternetConnection();
}
