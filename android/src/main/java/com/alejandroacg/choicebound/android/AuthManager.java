package com.alejandroacg.choicebound.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.R;
import com.alejandroacg.choicebound.screens.SplashScreen;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AuthManager {
    private final AndroidLauncher activity;
    private final ChoiceboundGame game;
    private final FirebaseAuth mAuth;
    private final CredentialManager credentialManager;
    private final Executor executor;

    public AuthManager(AndroidLauncher activity, ChoiceboundGame game) {
        this.activity = activity;
        this.game = game;

        // Inicializa Firebase
        mAuth = FirebaseAuth.getInstance();

        // Inicializa CredentialManager y Executor
        credentialManager = CredentialManager.create(activity);
        executor = Executors.newSingleThreadExecutor();

    }

    public boolean hasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) return false;

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
        return false;
    }

    public void startOneTapSignIn() {
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
