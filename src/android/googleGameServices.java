package org.apache.cordova.googleGameServices;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONException;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.EventsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

////////////////////////////////////////////////////
public class googleGameServices extends CordovaPlugin  {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_ACHIEVEMENT_UI = 9003;
    private GoogleSignInClient mGoogleSignInClient;

    ////////////////////////////////////

    private AchievementsClient mAchievementsClient;
    private LeaderboardsClient mLeaderboardsClient;
    private EventsClient mEventsClient;
    private PlayersClient mPlayersClient;
    private static final int RC_UNUSED = 5001;
    private String mDisplayName = "";
    private GoogleSignInAccount account;
    private GoogleSignInOptions gso;
    private GamesClient gamesClient;

    /////////////////////////////////////////////////////////////////////////////
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.w(TAG, "*** MAIN initialize");

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this.cordova.getActivity(), gso);
        account = GoogleSignIn.getLastSignedInAccount(this.cordova.getActivity());
        Log.w(TAG, "*** initialize account: " + account );
        signInSilently();
    }
    /////////////////////////////////////////////////////////////////////////////
    @Override//funkcja która łączy się z JS
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("signInToGooglePlayGames")) {Log.d("log","***signInToGooglePlayGames");
            signInToGooglePlayGames();
        } else if (action.equals("showAchievements")) {Log.d("log","***showAchievements");
            showAchievements();
        } else if (action.equals("submitScoreForLeaderboards")) {Log.d("log","***submitScoreForLeaderboards");
            submitScoreForLeaderboards(callbackContext, args);
        } else if (action.equals("showLeaderboards")) {Log.d("log","***showLeaderboards");
            showLeaderboards(callbackContext, args);
        }else if (action.equals("unlockAchievements")) {Log.d("log","***unlockAchievements");
            unlockAchievements(callbackContext,args);
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    private void signInToGooglePlayGames() {
        Log.w(TAG, "***signInToGooglePlayGames" );
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        cordova.setActivityResultCallback(this);
        cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
//        mGoogleSignInClient = GoogleSignIn.getClient(this.cordova.getActivity(), gso);
//        Log.w(TAG, "***signInToGooglePlayGames4" );
    }

    private void showAchievements() {
        Log.w(TAG, "*** showAchi: "+ account);
        if (account == null){return ;}
        account = GoogleSignIn.getLastSignedInAccount(this.cordova.getActivity());
        Log.w(TAG, "*** showAchi: "+ account);
        Games.getAchievementsClient(this.cordova.getActivity(),account )
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        Log.w(TAG, "*** success: "  );
                        cordova.getActivity().startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                        Log.w(TAG, "*** posukcesie: " );
                    }
                });Log.w(TAG, "*** POshowAchi: " );
    }

    private void showLeaderboards(final CallbackContext callbackContext, final JSONArray data) throws JSONException{
        if (account == null){return ;}

        String leaderboardiId = data.getString(0);
        Log.w(TAG, "*** showLeaderboards: id: " + leaderboardiId);

        Games.getLeaderboardsClient(cordova.getActivity(), GoogleSignIn.getLastSignedInAccount(cordova.getContext()))
                .getLeaderboardIntent(leaderboardiId)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        cordova.getActivity().startActivityForResult(intent, 9004);
                    }
                });
    }

    private void unlockAchievements(final CallbackContext callbackContext, final JSONArray data) throws JSONException{
        if (account == null){Log.w(TAG, "*** account null ***" + account );return ;}
        String id = data.getString(0);
        Games.getAchievementsClient(this.cordova.getActivity(), account)
                .unlock(id);
        Log.w(TAG, "*** unlocked: " );
    }

    private void submitScoreForLeaderboards(final CallbackContext callbackContext, final JSONArray data) throws JSONException {
        if (account == null){return ;}
        String leaderboardID = data.getString(0);
        Log.w(TAG, "*** submitScoreForLeaderboards: leaderboardID: "  + leaderboardID);

        int points = data.getInt(1);
        Log.w(TAG, "*** submitScoreForLeaderboards: points: " + points);

        mLeaderboardsClient.submitScore(leaderboardID,points);
    }

    @Override
    public void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this.cordova.getActivity());
        Log.w(TAG, "*** OnStart: " + account );
        //updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.w(TAG, "*** result.isSuccess(): " + result.isSuccess() );

            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                account = result.getSignInAccount();
                Log.w(TAG, "*** SIGN IN account: " + account );
                Log.w(TAG, "*** SIGN IN account account.getEmail(): " + account.getEmail() );


                gamesClient = Games.getGamesClient(cordova.getContext(), account);
                gamesClient.setViewForPopups(webView.getView());
                onConnected(account);
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
//                    message = getString(R.string.signin_other_error);

                    Log.w(TAG, "*** SIGN IN FAILED: result.getStatus().getStatusMessage(): " + result.getStatus().getStatusMessage());
                    Log.w(TAG, "*** SIGN IN FAILED result: " + result);
                    Log.w(TAG, "*** SIGN IN FAILED: " + message);
                }
                new AlertDialog.Builder(cordova.getActivity()).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.w(TAG, "***handleSignInRestult, ale puste" );
    }

    private void signInSilently() {
        Log.d(TAG, "*** signInSilently()");
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(cordova.getActivity(),
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "*** signInSilently(): success");
                            gamesClient = Games.getGamesClient(cordova.getContext(), account);
                            gamesClient.setViewForPopups(webView.getView());
                            onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "*** signInSilently(): failure", task.getException());
                            onDisconnected();
                        }
                    }
                });
    }



    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        webView.loadUrl("javascript:cordova.fireDocumentEvent('OnLoginSuccess');");
        Log.d(TAG, "***onConnected(): connected to Google APIs");

        mAchievementsClient = Games.getAchievementsClient(cordova.getContext(), account);
        Log.d(TAG, "***onConnected(): mAchievementsClient: " + mAchievementsClient);
        mLeaderboardsClient = Games.getLeaderboardsClient(cordova.getContext(), googleSignInAccount);
        Log.d(TAG, "***onConnected(): mLeaderboardsClient" + mLeaderboardsClient);
        mEventsClient = Games.getEventsClient(cordova.getContext(), googleSignInAccount);
        Log.d(TAG, "***onConnected(): mEventsClient" + mEventsClient);
        mPlayersClient = Games.getPlayersClient(cordova.getContext(), googleSignInAccount);
        Log.d(TAG, "***onConnected(): mPlayersClient" + mPlayersClient);
        mGoogleSignInClient = GoogleSignIn.getClient(this.cordova.getActivity(), gso);
        Log.d(TAG, "***onConnected():mGoogleSignInClient");

        // Set the greeting appropriately on main menu
//        mPlayersClient.getCurrentPlayer()
//                .addOnCompleteListener(new OnCompleteListener<Player>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Player> task) {
//                        String displayName;
//                        if (task.isSuccessful()) {
//                            displayName = task.getResult().getDisplayName();
//                        } else {
//                            Exception e = task.getException();
//                        }
//
//                    }
//                });
    }

    private void signOut() {
        Log.d(TAG, "signOut()");

        if (!isSignedIn()) {
            Log.w(TAG, "signOut() called, but was not signed in!");
            return;
        }

        mGoogleSignInClient.signOut().addOnCompleteListener(cordova.getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        boolean successful = task.isSuccessful();
                        Log.d(TAG, "signOut(): " + (successful ? "success" : "failed"));

                        onDisconnected();
                    }
                });
    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");

        mAchievementsClient = null;
        mLeaderboardsClient = null;
        mPlayersClient = null;
    }
    private boolean isSignedIn() {
        return account != null;
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);

        Log.d(TAG, "onResume()");
//        signInSilently();
    }



//    private void signIn() {
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this.cordova.getActivity(), gso);
//
//        Log.w(TAG, "***signIn1" );
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        Log.w(TAG, "***signIn2" );
//        cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
//        Log.w(TAG, "***signIn3" );
//        mGoogleSignInClient = GoogleSignIn.getClient(this.cordova.getActivity(), gso);
//        Log.w(TAG, "***signIn4" );
//    }
}
