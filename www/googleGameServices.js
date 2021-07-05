cordova.define("googleGameServices.googleGameServices", function(require, exports, module) {
    var exec = require('cordova/exec');

    exports.initialize = function (success, error) {
        exec(success, error, 'googleGameServices','initialize');
    };
    exports.signInToGooglePlayGames = function (success, error) {
        exec(success, error, 'googleGameServices','signInToGooglePlayGames');
    };
    exports.showAchievements = function (success, error) {
        exec(success, error, 'googleGameServices','showAchievements');
    };
    exports.submitScoreForLeaderboards = function (ID, POINTS, success, error) {
        exec(success, error, 'googleGameServices','submitScoreForLeaderboards',[ID,POINTS]);
    };
    exports.showLeaderboards = function ( LeaderboardID, success, error) {
        exec(success, error, 'googleGameServices','showLeaderboards',[LeaderboardID]);
    };
    exports.unlockAchievements = function (AchievementID, success, error) {
        exec(success, error, 'googleGameServices','unlockAchievements',[AchievementID]);
    }
    exports.OnLoginSuccess = function (callback, success, error){
        document.addEventListener('OnLoginSuccess', function(){callback();});
    }
});

