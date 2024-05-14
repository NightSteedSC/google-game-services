const fs = require('fs');
const path = require('path');

module.exports = function(context) {
    const stringsPath = path.join(context.opts.projectRoot, 'platforms/android/app/src/main/res/values/strings.xml');
    fs.readFile(stringsPath, 'utf8', function(err, data) {
        if (err) {
            throw new Error('Unable to find strings.xml: ' + err);
        }

        var newValue = context.opts.plugin.pluginInfo.getPreferences().GOOGLE_GAMES_SERVICES_WEB_CLIENT_ID;
        if (newValue) {
            var result = data.replace(/<string name="google_games_app_id">[^<]*<\/string>/, `<string name="google_games_app_id">${newValue}</string>`);
            fs.writeFile(stringsPath, result, 'utf8', function(err) {
                if (err) {
                    throw new Error('Failed to write strings.xml: ' + err);
                }
            });
        }
    });
};
