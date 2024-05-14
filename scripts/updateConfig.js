const fs = require('fs');
const path = require('path');

module.exports = function(context) {
    const gradlePath = path.join(context.opts.projectRoot, 'platforms/android/build.gradle');

    fs.readFile(gradlePath, 'utf8', function(err, data) {
        if (err) {
            throw new Error('Failed to read build.gradle: ' + err.message);
        }

        if (data.indexOf('mavenCentral()') === -1) {
            // Modify the buildscript repositories
            data = data.replace(/repositories \{/g, 'repositories {\n        mavenCentral()\n        google()');

            // Modify the allprojects repositories
            data = data.replace(/allprojects \{\s+repositories \{/g, 'allprojects {\n    repositories {\n        mavenCentral()\n        google()');

            fs.writeFile(gradlePath, data, 'utf8', function(err) {
                if (err) {
                    throw new Error('Failed to write build.gradle: ' + err.message);
                }
            });
        }
    });
};
