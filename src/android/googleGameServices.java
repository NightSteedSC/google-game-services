package org.apache.cordova.googleGameServices;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import androidx.annotation.NonNull;
import android.app.Application;

import org.apache.cordova.NightAds.NightAds;
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
import java.util.Arrays;
import java.util.List;




////////////////////////////////////////////////////
public class googleGameServices extends CordovaPlugin  {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_ACHIEVEMENT_UI = 9003;


    ////////////////////////////////////

    private AchievementsClient mAchievementsClient;
    private LeaderboardsClient mLeaderboardsClient;
    private EventsClient mEventsClient;
    private PlayersClient mPlayersClient;
    private static final int RC_UNUSED = 5001;
    private String mDisplayName = "";
    private String serverAuthCode = "";

    private Person person;
    private PeopleService peopleService;
    private Credential credential;


    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private GoogleSignInOptions googleSignInOptions;
    private GamesClient gamesClient;
//    private HttpTransport httpTransport;
//    private NetHttpTransport netHttpTransport;



    private static final String APPLICATION_NAME = "Google People API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Arrays.asList(PeopleServiceScopes.CONTACTS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    private static final String WEB_CLIENT_ID = "212521125204-kf81r8kb4vajfi50rnsg6jis4tdnhgpm.apps.googleusercontent.com";
    private static final String WEB_CLIENT_SECRET = "yBAagUw7BCjrtI1C8lPhW5XC";
    private static final String ANDROID_DEBUG_CLIENT_ID = "212521125204-op6h43m2bj8rd56567omjg5lkvbpt44u.apps.googleusercontent.com";
    private static final String ANDROID_PROD_CLIENT_ID = "212521125204-8o7qknbdhnkufciu061pi3ve6pn7q1ul.apps.googleusercontent.com";
    private Application application = new Application();

    private NightAds nightAds;

    /////////////////////////////////////////////////////////////////////////////
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView){
        super.initialize(cordova, webView);

//        if(googleSignInAccount == null){
//            Log.w(TAG, "*** MAIN initialize signInSilently(): " + googleSignInAccount);
////            signInSilently();
//        } else {
//            Log.w(TAG, "*** MAIN initialize googleSignInAccount: " + googleSignInAccount);
//            Log.w(TAG, "*** MAIN initialize googleSignInAccount Display name: " + googleSignInAccount.getDisplayName());
//            Log.w(TAG, "*** MAIN initialize googleSignInAccount email: " + googleSignInAccount.getEmail());
//            Log.w(TAG, "*** MAIN initialize googleSignInAccount getServerAuthCode: " + googleSignInAccount.getServerAuthCode());
//            Log.w(TAG, "*** MAIN initialize googleSignInAccount getGrantedScopes: " + googleSignInAccount.getGrantedScopes());
//
//
//            Log.w(TAG, "*** SIGN IN account: " + googleSignInAccount);
////        Log.w(TAG, "*** SIGN IN account account.getEmail(): " + googleSignInAccount.getEmail() );
////        Log.w(TAG, "*** SIGN IN account getIdToken: " + googleSignInAccount.getIdToken());
//            Log.w(TAG, "*** SIGN IN account getServerAuthCode: " + googleSignInAccount.getServerAuthCode());
//
////            credential = new GoogleCredential().setAccessToken(googleSignInAccount.getServerAuthCode());
////            httpTransport = new com.google.api.client.http.javanet.NetHttpTransport();
////            peopleService = new PeopleService.Builder(httpTransport, JSON_FACTORY, credential)
////                    .setApplicationName(APPLICATION_NAME)
////                    .build();
////            try{
////                person = peopleService.people().get("people/me?key=AIzaSyDTe2g_Si78isswNBw6sLnzfS81a353lm8")
////                        .setRequestMaskIncludeField("birthdays")
////                        .execute();
////            } catch (IOException ex){
////                Log.w(TAG, "*** MAIN initialize person ex: ");
////                Log.w(TAG, "*** MAIN initialize person ex: " + ex.getMessage());
////            }
//
////        Log.w(TAG, "*** MAIN initialize googleSignInAccount profile: " + person);
////        Log.w(TAG, "*** MAIN initialize googleSignInAccount profile profile.getAgeRange(): " + person.getAgeRange());
//
//
//        }
    }

    /////////////////////////////////////////////////////////////////////////////
    @Override//funkcja która łączy się z JS
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("signInToGooglePlayGames")) {Log.d("log","***signInToGooglePlayGames");
            signInToGooglePlayGames();
        }
        else if (action.equals("initialize")) {Log.d("log","***initialize");
            initialize();
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

    private void initialize() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.w(TAG, "*** MAIN initialize");

        Scope birthdayScope = new Scope("https://www.googleapis.com/auth/user.birthday.read");
        Scope ageRangeScope = new Scope("https://www.googleapis.com/auth/profile.agerange.read");
        Scope profileScope = new Scope("https://www.googleapis.com/auth/userinfo.profile");

//        googleSignInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestServerAuthCode(WEB_CLIENT_ID)
                .requestIdToken(WEB_CLIENT_ID)
                .requestScopes(birthdayScope,profileScope)
                .build();

        googleSignInClient = GoogleSignIn.getClient(cordova.getActivity(), googleSignInOptions);

        Intent signInIntent = googleSignInClient.getSignInIntent();
        cordova.setActivityResultCallback(this);
        cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInToGooglePlayGames() {
        Log.w(TAG, "***signInToGooglePlayGames" );
        Intent signInIntent = googleSignInClient.getSignInIntent();
        cordova.setActivityResultCallback(this);
        cordova.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
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


//        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this.cordova.getActivity());
//        Log.w(TAG, "*** showAchi: "+ googleSignInAccount);

        mAchievementsClient.getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        Log.w(TAG, "*** mAchievementsClient.getAchievementsIntent: "  );
//                        cordova.setActivityResultCallback(this);
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


//        Games.getAchievementsClient(this.cordova.getActivity(), googleSignInAccount)
//                .unlock(id);


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
//                Auth.GoogleSignInApi.revokeAccess(Auth.GoogleSignInApi);

                Log.w(TAG, "*** result: " + result );
                Log.w(TAG, "*** result.isSuccess(): " + result.isSuccess() );

                if (result.isSuccess()) {
                    // The signed in account is stored in the result.
                    googleSignInAccount = result.getSignInAccount();
                    serverAuthCode = googleSignInAccount.getServerAuthCode();

                    Log.w(TAG, "*** profile getEmail: " + googleSignInAccount.getEmail());

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
                                        WEB_CLIENT_ID,
                                        WEB_CLIENT_SECRET,
                                        googleSignInAccount.getServerAuthCode(),
                                        redirectUrl
                                ).execute();

                                Log.w(TAG, "*** GoogleTokenResponse GOOD: " + tokenResponse );

                                GoogleCredential credential = new GoogleCredential.Builder()
                                        .setClientSecrets(WEB_CLIENT_ID, WEB_CLIENT_SECRET)
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

                                            goToUrl("javascript:cordova.fireDocumentEvent('onLoginSuccess', {'day': '" + bday + "', 'month': '" + bmonth + "', 'year': '" + byear + "'})");

                                            Log.w(TAG, "*** MAIN initialize googleSignInAccount getServerAuthCode 2: " + serverAuthCode);
                                            gamesClient = Games.getGamesClient(cordova.getContext(), googleSignInAccount);
                                            gamesClient.setViewForPopups(webView.getView());
                                            onConnected();

                                        } else {
                                            Log.w(TAG, "*** profile.getBirthdays bdate NULL: ");
                                        }
                                    }
                                }
                            } catch (IOException ex){
                                Log.w(TAG, "*** GoogleTokenResponse ERR ex: " + ex.getMessage() );
                            }
                        }
                    });

                    thread.start();
                } else {

                    goToUrl("javascript:cordova.fireDocumentEvent('onLoginFailed', {'': ''})");

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

    private void initNightAds(Boolean hasBirthday, Date dateOfBirth) {
//        NightAds nightAds = new NightAds();
        nightAds.initFromGoogleGameServices(hasBirthday, dateOfBirth);
    }

    private void signInSilently() {
        Log.d(TAG, "*** signInSilently()");
        googleSignInClient.silentSignIn().addOnCompleteListener(cordova.getActivity(),
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "*** signInSilently(): success");
                            googleSignInAccount = task.getResult();
                            gamesClient = Games.getGamesClient(cordova.getContext(), googleSignInAccount);
                            gamesClient.setViewForPopups(webView.getView());
                            onConnected();
                        } else {
                            Log.d(TAG, "*** signInSilently(): failure", task.getException());
                            onDisconnected();
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
        // fragment using getActivity ()
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
    }

//
//    public void sendAuthCode(String authCode){
//        HttpPost httpPost = new HttpPost("https://nsg-v.herokuapp.com/authcode");
//
//        String url = "https://nsg-v.herokuapp.com/authcode";
//        GenericUrl uri = new GenericUrl(url);
//
//        HttpRequestFactory httpRequestFactory = new NetHttpTransport().createRequestFactory();
//        String requestBody = "{'name': 'newIndia','columns': [{'name': 'Species','type': 'STRING'}],'description': 'Insect Tracking Information.','isExportable': true}";
//        HttpContent content = new JsonHttpContent(JSON_FACTORY,authCode);;
//
//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try  {
//                    try {
//                        Log.w(TAG, "*** MAIN initialize HttpRequest new GenericUrl(url):  " + new GenericUrl(url));
//
//                        HttpRequest request =      httpRequestFactory.buildPostRequest(
//                                new GenericUrl(url), content);
////                        HttpRequest request = httpRequestFactory.buildPostRequest(uri, content);
//                        request.getHeaders().setAccept("application/json");
//                        request.getHeaders().setContentType("application/json;charset=UTF-8");
//                        request.execute();
//
//                    }
//                    catch (IOException ex){
//                        Log.w(TAG, "*** MAIN initialize HttpRequest: " + ex.getMessage());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();
//    }
}
