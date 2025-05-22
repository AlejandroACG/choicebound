package com.alejandroacg.choicebound.android;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.FirebaseApp;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;

public class AndroidLauncher extends AndroidApplication implements PlatformBridge {
    private AuthManager authManager;
    private View rootView;
    private int keyboardHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Inicializa Firebase
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        ChoiceboundGame game = new ChoiceboundGame(this, new FirestoreDatabase());
        authManager = new AuthManager(this, game);
        initialize(game, config);

        rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            keyboardHeight = keypadHeight > screenHeight * 0.15 ? keypadHeight : 0;
        });
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

    @Override
    public String getCurrentUserId() {
        return authManager.getCurrentUserUid();
    }

    @Override
    public float getKeyboardHeight() {
        return keyboardHeight;
    }
}
