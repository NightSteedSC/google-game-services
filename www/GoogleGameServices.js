    var exec = require('cordova/exec');

    var GoogleGameServices = {
        signIn: function(successCallback, errorCallback) {
            exec(successCallback, errorCallback, "GoogleGameServices", "signIn", []);
        },
        showAchievements: function(successCallback, errorCallback) {
            exec(successCallback, errorCallback, "GoogleGameServices", "showAchievements", []);
        },
        showLeaderboards: function(successCallback, errorCallback) {
            exec(successCallback, errorCallback, "GoogleGameServices", "showLeaderboards", []);
        },
        submitScoreForLeaderboards: function(ID, POINTS, successCallback, errorCallback) {
            exec(successCallback, errorCallback, "GoogleGameServices", "submitScoreForLeaderboards", [ID,POINTS]);
        },
        unlockAchievements: function(AchievementID, type, incrementValue, successCallback, errorCallback) {
            exec(successCallback, errorCallback, "GoogleGameServices", "unlockAchievements", [AchievementID, type, incrementValue]);
        },
        isSignedIn: function(successCallback, errorCallback) {
            exec(successCallback, errorCallback, "GoogleGameServices", "isSignedIn", []);
        },
        onPlayerInfoReceived: function(successCallback, errorCallback) {
            document.addEventListener('onPlayerInfoReceived', function(data_){successCallback(data_);});
        },
        onLoginSuccess: function(successCallback, errorCallback) {
            document.addEventListener('onLoginSuccess', function(){successCallback();});
        },
        onLoginFailed: function(successCallback, errorCallback) {
            document.addEventListener('onLoginFailed', function(){successCallback();});
        }
    };

    module.exports = GoogleGameServices;

