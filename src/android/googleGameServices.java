package org.apache.cordova.googleGameServices;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;

// Google Play Games specific imports
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.PlayGames;

import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.LeaderboardsClient;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.EventsClient;

////////////////////////////////////////////////////
public class googleGameServices extends CordovaPlugin  {

   private static final String TAG = "SignInActivity";
   private static final int RC_SIGN_IN = 9001;
   private static final int RC_ACHIEVEMENT_UI = 9003;
   private AchievementsClient mAchievementsClient;
   private LeaderboardsClient mLeaderboardsClient;
   private EventsClient mEventsClient;
   private PlayersClient mPlayersClient;
   private static final int RC_UNUSED = 5001;
   private String mDisplayName = "";
   private String serverAuthCode = "";

   ArrayList<String> birthday = new ArrayList<String>();

    private GamesSignInClient gamesSignInClient = PlayGames.getGamesSignInClient(this.cordova.getActivity());


   private GoogleSignInClient googleSignInClient;
   private GoogleSignInAccount googleSignInAccount;
   private GoogleSignInOptions googleSignInOptions;
   private GamesClient gamesClient;
   private static final String APPLICATION_NAME = "Google People API Java Quickstart";
   private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static String GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID = "";

    /////////////////////////////////////////////////////////////////////////////
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView){
        super.initialize(cordova, webView);

    }

    /////////////////////////////////////////////////////////////////////////////
    @Override//funkcja która łączy się z JS
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("signInToGooglePlayGames")) {Log.d("log","***signInToGooglePlayGames");
            Log.w(TAG, "*** MAIN initialize 0");
            GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID = webView.getPreferences().getString("GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID", "");
            signInToGooglePlayGames();
        }
        else if (action.equals("initialize")) {Log.d("log","***initialize");
//            initialize();
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

//        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this.cordova.getActivity());
//        Log.w(TAG, "*** OnStart: " + googleSignInAccount);
        //updateUI(account);
    }


//    gamesSignInClient.isAuthenticated().addOnCompleteListener(isAuthenticatedTask -> {
//        boolean isAuthenticated =
//                (isAuthenticatedTask.isSuccessful() &&
//                        isAuthenticatedTask.getResult().isAuthenticated());
//
//        if (isAuthenticated) {
//            // Continue with Play Games Services
//        } else {
//            // Disable your integration with Play Games Services or show a
//            // login button to ask  players to sign-in. Clicking it should
//            // call GamesSignInClient.signIn().
//        }
//    });


//    private void initialize() {
//        signInToGooglePlayGames();
//    }

    private void signInToGooglePlayGames() {

       Log.w(TAG, "***signInToGooglePlayGames" );
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

       Log.w(TAG, "*** MAIN initialize");

       googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
//                .requestServerAuthCode(GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID)
//                .requestIdToken(GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID)
               .requestScopes()
               .build();

       googleSignInClient = GoogleSignIn.getClient(cordova.getActivity(), googleSignInOptions);

       Log.w(TAG, "*** MAIN initialize googleSignInClient: " + googleSignInClient);

       if(googleSignInClient != null){
           signInSilently();
       } else {
           Intent signInIntent = googleSignInClient.getSignInIntent();
           cordova.setActivityResultCallback(this);
           cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
       }
    }

    private void showLeaderboards(final CallbackContext callbackContext, final JSONArray data) throws JSONException{
       if (googleSignInAccount == null){return ;}

       String leaderboardiId = data.getString(0);
       Log.w(TAG, "*** showLeaderboards: id: " + leaderboardiId);

       Games.getLeaderboardsClient(cordova.getActivity(), googleSignInAccount)
               .getLeaderboardIntent(leaderboardiId)
               .addOnSuccessListener(new OnSuccessListener<Intent>() {
                   @Override
                   public void onSuccess(Intent intent) {
                       cordova.getActivity().startActivityForResult(intent, 9004);
                   }
               });
    }

    private void showAchievements() {
       if (googleSignInAccount == null){
           return ;
       }

       mAchievementsClient.getAchievementsIntent()
           .addOnSuccessListener(new OnSuccessListener<Intent>() {
               @Override
               public void onSuccess(Intent intent) {
                   Log.w(TAG, "*** mAchievementsClient.getAchievementsIntent: "  );
                   cordova.getActivity().startActivityForResult(intent, RC_ACHIEVEMENT_UI);
               }
           });
    }

    private void unlockAchievements(final CallbackContext callbackContext, final JSONArray data) throws JSONException{
       if (googleSignInAccount == null){
           Log.w(TAG, "*** account null ***" + googleSignInAccount);
           return;
       }

       String id = data.getString(0);
       String type = data.getString(1);
       int incrementValue = Integer.parseInt(data.getString(2));

       Log.w(TAG, "*** unlocked id: " + id);
       Log.w(TAG, "*** unlocked type: " + type);
       Log.w(TAG, "*** unlocked incrementValue: " + incrementValue);

       // Type 1 -> Incremental
       // Type 0 -> Standard

       if (type.equals("0")) {
           mAchievementsClient.unlock(id);
           Log.w(TAG, "*** unlocked id: " + id);
       } else if(type.equals("1")){
           mAchievementsClient.increment(id, incrementValue);
           Log.w(TAG, "*** unlocked id: " + id);
       } else {
           Log.w(TAG, "*** unlocked no type: " + type);
           return;
       }
    }

    private void submitScoreForLeaderboards(final CallbackContext callbackContext, final JSONArray data) throws JSONException {
       if (googleSignInAccount == null){return ;}
       String leaderboardID = data.getString(0);
       Log.w(TAG, "*** submitScoreForLeaderboards: leaderboardID: "  + leaderboardID);

       int points = data.getInt(1);
       Log.w(TAG, "*** submitScoreForLeaderboards: points: " + points);

       mLeaderboardsClient.submitScore(leaderboardID,points);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//
//        Log.w(TAG, "*** result requestCode: " + requestCode );
//        Log.w(TAG, "*** result resultCode: " + resultCode );
//        Log.w(TAG, "*** result data: " + data );
//
//        if (data != null)
//        {
//            if (requestCode == RC_SIGN_IN) {
//                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//                Log.w(TAG, "*** result: " + result );
//                Log.w(TAG, "*** result.isSuccess(): " + result.isSuccess() );
//
//                if (result.isSuccess()) {
//                    googleSignInAccount = result.getSignInAccount();
//                    getBirthday(googleSignInAccount);
//
//                    gamesClient = Games.getGamesClient(cordova.getContext(), googleSignInAccount);
//                    gamesClient.setViewForPopups(webView.getView());
////                    goToUrl("javascript:cordova.fireDocumentEvent('onLoginSuccess', {'day': '" + birthday.get(0) + "', 'month': '" + birthday.get(1) + "', 'year': '" + birthday.get(2) + "'})");
//                    onConnected();
//                } else {
//                    goToUrl("javascript:cordova.fireDocumentEvent('onLoginFailed', {'a': 'a'})");
//
//                    String message = result.getStatus().getStatusMessage();
//                    if (message == null || message.isEmpty()) {
//                        Log.w(TAG, "*** SIGN IN FAILED: result.getStatus().getStatusMessage(): " + result.getStatus().getStatusMessage());
//                        Log.w(TAG, "*** SIGN IN FAILED result.getStatus(): " + result.getStatus());
//                        Log.w(TAG, "*** SIGN IN FAILED: " + message);
//                    }
//                }
//            }
//        }
    }

    private void signInSilently() {
        Log.d(TAG, "*** signInSilently()");

       googleSignInClient.silentSignIn().addOnCompleteListener(cordova.getActivity(),
       new OnCompleteListener<GoogleSignInAccount>() {
           @Override
           public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
               try {
                   Log.d(TAG, "*** signInSilently(): success");
                   googleSignInAccount = task.getResult(ApiException.class);
                   getBirthday(googleSignInAccount);

                   gamesClient = Games.getGamesClient(cordova.getContext(), googleSignInAccount);
                   gamesClient.setViewForPopups(webView.getView());

                   onConnected();

               } catch (ApiException apiException) {
                   Throwable cause = apiException.getCause();
                   Log.d(TAG, "*** signInSilently(): failure cause" + cause);
                   goToUrl("javascript:cordova.fireDocumentEvent('onLoginFailed', {'a': 'a'})");
               }

           }
       });
    }

    private void onConnected() {
       Log.d(TAG, "***onConnected(): connected to Google APIs");
       mAchievementsClient = Games.getAchievementsClient(cordova.getContext(), googleSignInAccount);
       Log.d(TAG, "***onConnected(): mAchievementsClient: " + mAchievementsClient);
       mLeaderboardsClient = Games.getLeaderboardsClient(cordova.getContext(), googleSignInAccount);
       Log.d(TAG, "***onConnected(): mLeaderboardsClient" + mLeaderboardsClient);
       mEventsClient = Games.getEventsClient(cordova.getContext(), googleSignInAccount);
       Log.d(TAG, "***onConnected(): mEventsClient" + mEventsClient);
       mPlayersClient = Games.getPlayersClient(cordova.getContext(), googleSignInAccount);
       Log.d(TAG, "***onConnected(): mPlayersClient" + mPlayersClient);
    }

    private void signOut() {
       Log.d(TAG, "signOut()");

       if (!isSignedIn()) {
           Log.w(TAG, "signOut() called, but was not signed in!");
           return;
       }

       googleSignInClient.signOut().addOnCompleteListener(cordova.getActivity(),
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

//    private boolean isSignedIn() {
//        return googleSignInAccount != null;
//    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        Log.d(TAG, "onResume()");
//        signInSilently();
    }

    public void goToUrl(String url){
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
    }
}
