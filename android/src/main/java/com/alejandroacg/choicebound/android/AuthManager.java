package com.alejandroacg.choicebound.android;

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
import com.alejandroacg.choicebound.R;
import com.alejandroacg.choicebound.SplashScreen;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AuthManager {
    private final AndroidLauncher activity;
    private final ChoiceboundGame game;
    private Button googleButton;
    private final FirebaseAuth mAuth;
    private final CredentialManager credentialManager;
    private final Executor executor;
    private final FrameLayout layout;

    public AuthManager(AndroidLauncher activity, ChoiceboundGame game, FrameLayout layout) {
        this.activity = activity;
        this.game = game;
        this.layout = layout;

        // Inicializa Firebase
        mAuth = FirebaseAuth.getInstance();

        // Inicializa CredentialManager y Executor
        credentialManager = CredentialManager.create(activity);
        executor = Executors.newSingleThreadExecutor();

        // No creamos el botón aquí, lo haremos dinámicamente en createGoogleButton
        googleButton = null;
    }

    public void createGoogleButton() {
        activity.runOnUiThread(() -> {
            // Solo creamos el botón si no existe
            if (googleButton == null) {
                googleButton = new Button(activity);
                googleButton.setText("Sign in with Google");

                FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                );
                buttonParams.gravity = Gravity.CENTER_HORIZONTAL;
                buttonParams.topMargin = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.60);
                googleButton.setLayoutParams(buttonParams);

                layout.addView(googleButton);

                // Configura el clic del botón para iniciar el flujo de One Tap Sign-In
                googleButton.setOnClickListener(v -> startOneTapSignIn());
            }
        });
    }

    public void destroyGoogleButton() {
        activity.runOnUiThread(() -> {
            if (googleButton != null) {
                layout.removeView(googleButton);
                googleButton = null;
            }
        });
    }

    private void startOneTapSignIn() {
        // Configura las opciones de Google Sign-In
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // Permite seleccionar cualquier cuenta de Google
            .setServerClientId(activity.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(true) // Habilita la selección automática si hay una sola cuenta
            .build();

        // Crea la solicitud de credenciales
        GetCredentialRequest request = new GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build();

        // Inicia el flujo de One Tap Sign-In
        credentialManager.getCredentialAsync(
            activity,
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
                    notifyLoginFailure(e.getMessage());
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
            notifyLoginFailure(e.getMessage());
        }
    }

    private void firebaseAuthWithGoogle(GoogleIdTokenCredential credential) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(credential.getIdToken(), null);
        mAuth.signInWithCredential(authCredential)
            .addOnCompleteListener(activity, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d("AndroidLauncher", "Login exitoso: " + user.getDisplayName());
                    notifyLoginSuccess();
                } else {
                    Log.w("AndroidLauncher", "Fallo en login con Firebase: " + task.getException().getMessage());
                    notifyLoginFailure(task.getException().getMessage());
                }
            });
    }

    private void notifyLoginSuccess() {
        if (game.getCurrentScreen() instanceof SplashScreen) {
            ((SplashScreen) game.getCurrentScreen()).onLoginSuccess();
        }
    }

    private void notifyLoginFailure(String errorMessage) {
        if (game.getCurrentScreen() instanceof SplashScreen) {
            ((SplashScreen) game.getCurrentScreen()).onLoginFailure(errorMessage);
        }
    }
}
