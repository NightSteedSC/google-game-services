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
    }
};

module.exports = GoogleGameServices;
