package org.apache.cordova.googleGameServices;

import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import androidx.annotation.NonNull;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;

import com.android.billingclient.api.Purchase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
//import com.google.android.gms.auth.api.credentials.
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.EventsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.GamesClient;
//import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
//import com.google.api.client.json.gson.gsonfactory;
//import com.google.api.client.googleapis
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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

    private PeopleService peopleService;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private GoogleSignInOptions googleSignInOptions;
    private GamesClient gamesClient;
    private static final String APPLICATION_NAME = "Google People API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static String GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID = "";
    private static String GOOGLE_GAMES_SERVICES_WEB_CLIENT_SECRET = "";

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
            GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID = cordova.getActivity().getString(cordova.getActivity().getResources().getIdentifier( "GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID", "string", cordova.getActivity().getPackageName()));
            GOOGLE_GAMES_SERVICES_WEB_CLIENT_SECRET = cordova.getActivity().getString(cordova.getActivity().getResources().getIdentifier( "GOOGLE_GAMES_SERVICES_WEB_CLIENT_SECRET", "string", cordova.getActivity().getPackageName()));
            signInToGooglePlayGames();
        }
        else if (action.equals("initialize")) {Log.d("log","***initialize");
//            initialize();
        }
        else if (action.equals("showAchievements")) {Log.d("log","***showAchievements");
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

//    private void initialize() {
//        signInToGooglePlayGames();
//    }

    private void signInToGooglePlayGames() {
        Log.w(TAG, "***signInToGooglePlayGames" );
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        Log.w(TAG, "*** MAIN initialize");

        Scope birthdayScope = new Scope("https://www.googleapis.com/auth/user.birthday.read");
        Scope profileScope = new Scope("https://www.googleapis.com/auth/userinfo.profile");

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestServerAuthCode(GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID)
                .requestIdToken(GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID)
                .requestScopes(birthdayScope,profileScope)
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

        Games.getLeaderboardsClient(cordova.getActivity(), GoogleSignIn.getLastSignedInAccount(cordova.getContext()))
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
//        if (googleSignInAccount == null){
//            Log.w(TAG, "*** account null ***" + googleSignInAccount);
//            return;
//        }

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
    public void onStart() {
        super.onStart();
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this.cordova.getActivity());
        Log.w(TAG, "*** OnStart: " + googleSignInAccount);
        //updateUI(account);
    }

    private void getBirthday(GoogleSignInAccount googleSignInAccount){
        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                            httpTransport,
                            jsonFactory,
                            GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID,
                            GOOGLE_GAMES_SERVICES_WEB_CLIENT_SECRET,
                            googleSignInAccount.getServerAuthCode(),
                            redirectUrl
                    ).execute();

                    Log.w(TAG, "*** GoogleTokenResponse GOOD: " + tokenResponse );

                    GoogleCredential credential = new GoogleCredential.Builder()
                            .setClientSecrets(GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID, GOOGLE_GAMES_SERVICES_WEB_CLIENT_SECRET)
                            .setTransport(httpTransport)
                            .setJsonFactory(jsonFactory)
                            .build();

                    credential.setFromTokenResponse(tokenResponse);

                    peopleService = new PeopleService.Builder(httpTransport, JSON_FACTORY, credential)
                            .setApplicationName(APPLICATION_NAME)
                            .build();

                    Person profile = peopleService.people().get("people/me")
                            .setPersonFields("birthdays")
                            .execute();

                    List<Birthday> birthdays = profile.getBirthdays();
                    Log.w(TAG, "*** profile birthdays: " + birthdays );

                    if (birthdays != null && birthdays.size() > 0) {

                        for (Birthday b : birthdays) {

                            Date bdate = b.getDate();

                            if (bdate != null) {
                                String bday, bmonth, byear;

                                if (bdate.getYear() == null) {
                                    continue;
                                } else {
                                    byear = bdate.getYear().toString();
                                }

                                if (bdate.getDay() != null)
                                    bday = bdate.getDay().toString();
                                else bday = "";
                                if (bdate.getMonth() != null)
                                    bmonth = bdate.getMonth().toString();
                                else bmonth = "";

                                Log.w(TAG, "*** profile.getBirthdays bday: " + bday);
                                Log.w(TAG, "*** profile.getBirthdays bmonth: " + bmonth);
                                Log.w(TAG, "*** profile.getBirthdays byear: " + byear);

                                birthday.add(bday);
                                birthday.add(bmonth);
                                birthday.add(byear);

                            } else {
                                Log.w(TAG, "*** profile.getBirthdays bdate NULL: ");
                                birthday.add("1");
                                birthday.add("1");
                                birthday.add("1");
                            }
                        }
                    }
                } catch (IOException ex){
                    Log.w(TAG, "*** GoogleTokenResponse ERR ex: " + ex.getMessage() );
                    birthday.add("1");
                    birthday.add("1");
                    birthday.add("1");
                }
            }
        });

        thread.start();
//        return birthday;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w(TAG, "*** result requestCode: " + requestCode );
        Log.w(TAG, "*** result resultCode: " + resultCode );
        Log.w(TAG, "*** result data: " + data );

        if (data != null)
        {
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.w(TAG, "*** result: " + result );
                Log.w(TAG, "*** result.isSuccess(): " + result.isSuccess() );

                if (result.isSuccess()) {
                    googleSignInAccount = result.getSignInAccount();
                    getBirthday(googleSignInAccount);

                    gamesClient = Games.getGamesClient(cordova.getContext(), googleSignInAccount);
                    gamesClient.setViewForPopups(webView.getView());
                    goToUrl("javascript:cordova.fireDocumentEvent('onLoginSuccess', {'day': '" + birthday.get(0) + "', 'month': '" + birthday.get(1) + "', 'year': '" + birthday.get(2) + "'})");
                    onConnected();
                } else {
                    goToUrl("javascript:cordova.fireDocumentEvent('onLoginFailed', {'a': 'a'})");

                    String message = result.getStatus().getStatusMessage();
                    if (message == null || message.isEmpty()) {
                        Log.w(TAG, "*** SIGN IN FAILED: result.getStatus().getStatusMessage(): " + result.getStatus().getStatusMessage());
                        Log.w(TAG, "*** SIGN IN FAILED result.getStatus(): " + result.getStatus());
                        Log.w(TAG, "*** SIGN IN FAILED: " + message);
                    }
                }
            }
        }
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

                    goToUrl("javascript:cordova.fireDocumentEvent('onLoginSuccess', {'day': '" + birthday.get(0) + "', 'month': '" + birthday.get(1) + "', 'year': '" + birthday.get(2) + "'})");
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
//        mAchievementsClient = null;
//        mLeaderboardsClient = null;
//        mPlayersClient = null;
    }

    private boolean isSignedIn() {
        return googleSignInAccount != null;
    }

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
