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
    initialize: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, "GoogleGameServices", "initialize", []);
    }
};

module.exports = GoogleGameServices;
