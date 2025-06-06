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
    private final FirebaseAuth firebaseAuth;
    private final CredentialManager credentialManager;
    private final Executor executor;

    public AuthManager(AndroidLauncher activity, ChoiceboundGame game) {
        this.activity = activity;
        this.game = game;
        firebaseAuth = FirebaseAuth.getInstance();
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
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(activity.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(true)
            .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build();

        credentialManager.getCredentialAsync(
            activity,
            request,
            null,
            executor,
            new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                @Override
                public void onResult(GetCredentialResponse result) {
                    handleSignInResult(result);
                }

                @Override
                public void onError(GetCredentialException e) {
                    Log.w("AndroidLauncher", "Error en One Tap Sign-In: " + e.getMessage());
                    notifySignInFailure(e.getMessage());
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
            notifySignInFailure(e.getMessage());
        }
    }

    private void firebaseAuthWithGoogle(GoogleIdTokenCredential credential) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(credential.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential)
            .addOnCompleteListener(activity, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Log.d("AndroidLauncher", "SignIn exitoso: " + user.getDisplayName());
                    String uid = user.getUid();
                    // Usar la versión asíncrona de doesUserExist
                    game.getDatabase().doesUserExist(
                        uid,
                        exists -> {
                            if (exists) {
                                notifySignInSuccess();
                            } else {
                                notifyFirstSignInSuccess();
                            }
                        },
                        error -> {
                            Log.w("AndroidLauncher", "Error al verificar existencia de usuario: " + error);
                            notifySignInFailure(error);
                        }
                    );
                } else {
                    Log.w("AndroidLauncher", "Fallo en SignIn con Firebase: " + task.getException().getMessage());
                    notifySignInFailure(task.getException().getMessage());
                }
            });
    }

    private void notifySignInSuccess() {
        if (game.getCurrentScreen() instanceof SplashScreen) {
            ((SplashScreen) game.getCurrentScreen()).onSignInSuccess();
        }
    }

    private void notifyFirstSignInSuccess() {
        if (game.getCurrentScreen() instanceof SplashScreen) {
            ((SplashScreen) game.getCurrentScreen()).onFirstSignInSuccess();
        }
    }

    private void notifySignInFailure(String errorMessage) {
        if (game.getCurrentScreen() instanceof SplashScreen) {
            ((SplashScreen) game.getCurrentScreen()).onSignInFailure(errorMessage);
        }
    }

    public boolean isUserAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void signOut() {
        firebaseAuth.signOut();
        game.clearLocalUser();
    }

    public String getCurrentUserUid() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
