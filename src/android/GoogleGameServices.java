package org.apache.cordova.googleGameServices;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.games.GamesSignInClient;

public class GoogleGameServices extends CordovaPlugin {
    private GamesSignInClient signInClient;

    @Override
    public void pluginInitialize() {
        super.pluginInitialize();
        PlayGamesSdk.initialize(cordova.getActivity().getApplicationContext());
        signInClient = PlayGames.getGamesSignInClient(cordova.getActivity());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("signIn".equals(action)) {
            this.signIn(callbackContext);
            return true;
        } else if ("showAchievements".equals(action)) {
            this.showAchievements(callbackContext);
            return true;
        } else if ("showLeaderboards".equals(action)) {
            this.showLeaderboards(callbackContext);
            return true;
        }
        return false;
    }

    private void signIn(CallbackContext callbackContext) {
        Intent signInIntent = signInClient.getSignInIntent();
        cordova.setActivityResultCallback(this);
        cordova.getActivity().startActivityForResult(signInIntent, 9001);
    }

    private void showAchievements(CallbackContext callbackContext) {
        Games.getAchievementsClient(cordova.getActivity(), signInClient.getLastSignedInAccount())
             .getAchievementsIntent()
             .addOnSuccessListener(intent -> cordova.getActivity().startActivityForResult(intent, 9002));
    }

    private void showLeaderboards(CallbackContext callbackContext) {
        Games.getLeaderboardsClient(cordova.getActivity(), signInClient.getLastSignedInAccount())
             .getAllLeaderboardsIntent()
             .addOnSuccessListener(intent -> cordova.getActivity().startActivityForResult(intent, 9003));
    }
}
