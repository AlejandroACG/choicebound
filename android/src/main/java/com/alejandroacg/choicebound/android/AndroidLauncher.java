package com.alejandroacg.choicebound.android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.google.android.gms.common.SignInButton;

public class AndroidLauncher extends AndroidApplication implements PlatformBridge {
    private SignInButton googleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;

        FrameLayout layout = new FrameLayout(this);
        View gameView = initializeForView(new ChoiceboundGame(this), configuration);
        layout.addView(gameView);

        googleButton = new SignInButton(this);
        googleButton.setSize(SignInButton.SIZE_WIDE);
        googleButton.setVisibility(View.GONE); // ← oculto al inicio

        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = Gravity.CENTER_HORIZONTAL;
        buttonParams.topMargin = (int) (getResources().getDisplayMetrics().heightPixels * 0.60);
        googleButton.setLayoutParams(buttonParams);

        layout.addView(googleButton);
        setContentView(layout);

        googleButton.setOnClickListener(view ->
            Log.i("AndroidLauncher", "Botón de Google pulsado (aquí irá el login real)")
        );
    }

    public void showGoogleButton() {
        runOnUiThread(() -> {
            if (googleButton != null) googleButton.setVisibility(View.VISIBLE);
        });
    }
}
