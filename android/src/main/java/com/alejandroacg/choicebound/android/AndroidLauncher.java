package com.alejandroacg.choicebound.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;

public class AndroidLauncher extends AndroidApplication implements PlatformBridge {
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        ChoiceboundGame game = new ChoiceboundGame(this);
        authManager = new AuthManager(this, game);
        initialize(game, config);
    }

    @Override
    public void startOneTapSignIn() {
        authManager.startOneTapSignIn();
    }

    @Override
    public boolean hasInternetConnection() {
        return authManager.hasInternetConnection();
    }

    @Override
    public boolean isUserAuthenticated() {
        return authManager.isUserAuthenticated();
    }

    @Override
    public void signOut() {
        authManager.signOut();
    }

    @Override
    public void onBackPressed() {
        // No hacer nada para deshabilitar el botón de retroceder
    }
}
