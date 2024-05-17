package org.apache.cordova.googleGameServices;

import android.content.Intent;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.Games;

public class GoogleGameServices extends CordovaPlugin {

    private GamesSignInClient gamesSignInClient = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override//funkcja która łączy się z JS
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("signIn")) {Log.d("log","***signIn");
            Log.d("BART", "*** signIn");
            signIn(callbackContext);
        }
        else if (action.equals("getPlayerInfo")) {Log.d("log","***getPlayerInfo");
            getPlayerInfo(callbackContext);
        }
        else if (action.equals("showAchievements")) {Log.d("log","***showAchievements");
//            showAchievements();
        } else if (action.equals("submitScoreForLeaderboards")) {Log.d("log","***submitScoreForLeaderboards");
//            submitScoreForLeaderboards(callbackContext, args);
        } else if (action.equals("showLeaderboards")) {Log.d("log","***showLeaderboards");
//            showLeaderboards(callbackContext, args);
        }else if (action.equals("unlockAchievements")) {Log.d("log","***unlockAchievements");
//            unlockAchievements(callbackContext,args);
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }


    @Override
    public void onStart() {
        super.onStart();
        PlayGamesSdk.initialize(this.cordova.getActivity());
    }

    private void signIn(CallbackContext callbackContext) {


        gamesSignInClient = PlayGames.getGamesSignInClient(cordova.getActivity());

        gamesSignInClient.isAuthenticated().addOnCompleteListener(isAuthenticatedTask -> {
            boolean isAuthenticated =
                    (isAuthenticatedTask.isSuccessful() &&
                            isAuthenticatedTask.getResult().isAuthenticated());

            if (isAuthenticated) {
                // Continue with Play Games Services
                Log.d("BART", "Is Authenticated");
//                gamesSignInClient.signIn();
                goToUrl("javascript:cordova.fireDocumentEvent('onLoginSuccess')");
                getPlayerInfo(callbackContext);
            } else {
                Log.d("BART", "Not Authenticated");
                // Disable your integration with Play Games Services or show a
                // login button to ask  players to sign-in. Clicking it should
                // call GamesSignInClient.signIn().

            }
        });


//        Intent signInIntent = signInClient.getSignInIntent();
//        cordova.setActivityResultCallback(this);
//        cordova.getActivity().startActivityForResult(signInIntent, 9001);

    }

        private void getPlayerInfo(CallbackContext callbackContext) {

            Log.d("BART", "getPlayerInfo Player ID: ");

            PlayersClient playersClient = PlayGames.getPlayersClient(cordova.getActivity());

            playersClient.getCurrentPlayer().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Successfully retrieved the Player ID
                    String playerId = task.getResult().getPlayerId();
                    Log.d("BART", "Player ID: " + playerId);

                    goToUrl("javascript:cordova.fireDocumentEvent('onPlayerInfoReceived', {'playerId': '" + playerId + "'})");
                } else {
                    // Handle failure
                    Log.e("BART", "Failed to get current player", task.getException());
                }
            });

    }

//    private void showAchievements(CallbackContext callbackContext) {
//        Games.getAchievementsClient(cordova.getActivity(), signInClient.getLastSignedInAccount())
//             .getAchievementsIntent()
//             .addOnSuccessListener(intent -> cordova.getActivity().startActivityForResult(intent, 9002));
//    }
//
//    private void showLeaderboards(CallbackContext callbackContext) {
//        Games.getLeaderboardsClient(cordova.getActivity(), signInClient.getLastSignedInAccount())
//             .getAllLeaderboardsIntent()
//             .addOnSuccessListener(intent -> cordova.getActivity().startActivityForResult(intent, 9003));
//    }

    public void goToUrl(String url){
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
    }
}
