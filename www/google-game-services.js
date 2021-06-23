cordova.define("google-game-services.google-game-services", function(require, exports, module) {
    var exec = require('cordova/exec');

    exports.initialize = function (success, error) {
        exec(success, error, 'google-game-services','initialize');
    };
    exports.signInToGooglePlayGames = function (success, error) {
        exec(success, error, 'google-game-services','signInToGooglePlayGames');
    };
    exports.showAchievements = function (success, error) {
        exec(success, error, 'google-game-services','showAchievements');
    };
    exports.submitScoreForLeaderboards = function (ID, POINTS, success, error) {
        exec(success, error, 'google-game-services','submitScoreForLeaderboards',[ID,POINTS]);
    };
    exports.showLeaderboards = function ( LeaderboardID, success, error) {
        exec(success, error, 'google-game-services','showLeaderboards',[LeaderboardID]);
    };
    exports.unlockAchievment = function (AchievmentID, success, error) {
        exec(success, error, 'google-game-services','unlockAchievment',[AchievmentID]);
    }
});

