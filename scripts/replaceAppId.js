const fs = require('fs');
const path = require('path');

module.exports = function(context) {
    const stringsPath = path.join(context.opts.projectRoot, 'platforms/android/app/src/main/res/values/strings.xml');
    // const newValue = context.opts.plugin.pluginInfo.getPreferences().GOOGLE_GAMES_PROJECT_ID || 'default-value';
    const newValue = '1056543107496';

    fs.readFile(stringsPath, 'utf8', function(err, data) {
        if (err) {
            console.error('Error reading strings.xml:', err);
            return;
        }

        const regex = /<string name="google_games_app_id">[^<]*<\/string>/;
        let result;

        if (regex.test(data)) {
            // Replace existing entry
            result = data.replace(regex, `<string name="google_games_app_id">${newValue}</string>`);
        } else {
            // Append new entry within the <resources> tag
            result = data.replace(/<\/resources>/, `    <string name="google_games_app_id">${newValue}</string>\n</resources>`);
        }

        fs.writeFile(stringsPath, result, 'utf8', function(writeErr) {
            if (writeErr) {
                console.error('Error writing strings.xml:', writeErr);
            } else {
                console.log("strings.xml successfully updated.");
            }
        });
    });
};
