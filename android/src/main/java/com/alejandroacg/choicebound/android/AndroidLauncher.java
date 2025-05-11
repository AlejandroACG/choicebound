package com.alejandroacg.choicebound.android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.interfaces.PlatformBridge;
import com.alejandroacg.choicebound.R;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AndroidLauncher extends AndroidApplication implements PlatformBridge {
    private Button googleButton;
    private FirebaseAuth mAuth;
    private View gameView;
    private CredentialManager credentialManager;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;

        // Inicializa Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Inicializa CredentialManager y Executor
        credentialManager = CredentialManager.create(this);
        executor = Executors.newSingleThreadExecutor();

        // Layout raíz
        FrameLayout layout = new FrameLayout(this);
        gameView = initializeForView(new ChoiceboundGame(this), configuration);
        layout.addView(gameView);

        // Botón personalizado para Google Sign-In
        googleButton = new Button(this);
        googleButton.setText("Sign in with Google");
        googleButton.setVisibility(View.GONE);

        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = Gravity.CENTER_HORIZONTAL;
        buttonParams.topMargin = (int) (getResources().getDisplayMetrics().heightPixels * 0.60);
        googleButton.setLayoutParams(buttonParams);

        layout.addView(googleButton);
        setContentView(layout);

        // Configura el clic del botón para iniciar el flujo de One Tap Sign-In
        googleButton.setOnClickListener(v -> startOneTapSignIn());
    }

    private void startOneTapSignIn() {
        // Configura las opciones de Google Sign-In
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // Permite seleccionar cualquier cuenta de Google
            .setServerClientId(getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(true) // Habilita la selección automática si hay una sola cuenta
            .build();

        // Crea la solicitud de credenciales
        GetCredentialRequest request = new GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build();

        // Inicia el flujo de One Tap Sign-In
        credentialManager.getCredentialAsync(
            this,
            request,
            null, // CancellationSignal, no lo usamos aquí
            executor,
            new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                @Override
                public void onResult(GetCredentialResponse result) {
                    handleSignInResult(result);
                }

                @Override
                public void onError(GetCredentialException e) {
                    Log.w("AndroidLauncher", "Error en One Tap Sign-In: " + e.getMessage());
                }
            }
        );
    }

    private void handleSignInResult(GetCredentialResponse result) {
        try {
            GoogleIdTokenCredential credential = GoogleIdTokenCredential.createFrom(
                result.getCredential().getData()
            );
            firebaseAuthWithGoogle(credential);
        } catch (Exception e) {
            Log.w("AndroidLauncher", "Error al procesar la respuesta de One Tap Sign-In: " + e.getMessage());
        }
    }

    private void firebaseAuthWithGoogle(GoogleIdTokenCredential credential) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(credential.getIdToken(), null);
        mAuth.signInWithCredential(authCredential)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d("AndroidLauncher", "Login exitoso: " + user.getDisplayName());
                } else {
                    Log.w("AndroidLauncher", "Fallo en login con Firebase: " + task.getException().getMessage());
                }
            });
    }

    @Override
    public void showGoogleButton() {
        runOnUiThread(() -> {
            if (googleButton != null) {
                googleButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
