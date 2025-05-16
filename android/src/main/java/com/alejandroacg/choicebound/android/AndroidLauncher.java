package com.alejandroacg.choicebound.android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.FirebaseApp;

public class AndroidLauncher extends AndroidApplication implements PlatformBridge {
    private AuthManager authManager;
    private View gameView;
    private ChoiceboundGame choiceboundGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;

        // Inicializa Firebase
        FirebaseApp.initializeApp(this);

        // Layout raíz
        FrameLayout layout = new FrameLayout(this);
        choiceboundGame = new ChoiceboundGame(this);
        gameView = initializeForView(choiceboundGame, configuration);
        layout.addView(gameView);

        // Inicializa AuthManager
        authManager = new AuthManager(this, choiceboundGame);

        setContentView(layout);
    }

    @Override
    public void startOneTapSignIn() {
        authManager.startOneTapSignIn();
    }

    @Override
    public boolean hasInternetConnection() {
        return authManager.hasInternetConnection();
    }
}
