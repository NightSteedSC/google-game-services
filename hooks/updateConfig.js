module.exports = function(ctx) {
    var fs = require('fs-extra'),
        path = require('path'),
        rootdir = ctx.opts.projectRoot,
        // mainConfigDir = rootdir + '/config.xml',
        android_dir = path.join(ctx.opts.projectRoot, 'platforms/android'),
        strings_dir = path.join(ctx.opts.projectRoot, 'platforms/android/app/src/main/res/values/strings.xml'),
        // pluginGradleFile = ctx.opts.plugin.dir + '/config/gradle.properties',
        // dest_gradle_file = android_dir + '/gradle.properties',
        C2_data_file = rootdir + '/www/data.js';

    // D:\Projects\cordova\MiniGiants\minigiants\platforms\android\app\src\main\res\values\strings.xml

    console.log("ctx: ", ctx);
    var myValue = "GoogleGamesAPPID";
    var APPLICATION_ID = '';

    // COPY GRADLE.PROPERTIES

    // console.log('Copy ' + pluginGradleFile + ' to ' + android_dir);
    // fs.createReadStream(pluginGradleFile).pipe(fs.createWriteStream(dest_gradle_file));

    // MAIN CONFIG EDIT

    // fs.readFile(mainConfigDir, 'utf-8', function(err, data) {
    //     if (err) throw err;
    //
    //     var previousConfig = '<platform name="android">'
    //         + '<resource-file src="www/google-services.json" target="app/www/google-services.json" />'
    //         + '</platform>'
    //         + '<custom-preference name="android-manifest/application/@android:usesCleartextTraffic" value="true" />'
    //         + '<custom-preference name="android-manifest/application/@android:networkSecurityConfig" value="@xml/network_security_config" /></widget>';
    //
    //     console.log(data);
    //
    //     if(!data.includes(previousConfig)){
    //         console.log('NO');
    //         var widgetString = data.replace(/<\/widget>/gim, previousConfig);
    //
    //         fs.writeFile(mainConfigDir, widgetString, 'utf-8', function(err, data) {
    //             if (err) throw err;
    //             console.log('ADD NEW CONFIG Done!');
    //         });
    //     }
    //     console.log('-----------------------');
    //
    //     // ADD NEW CONFIG
    // });

    // // MANIFEST EDIT
    //

    getAPP_IDFromDataJs(C2_data_file, ()=>{

        console.error('getAPP_IDFromDataJs DONE: ', APPLICATION_ID);

        addAPP_IDToStringsXML(()=>{

        });
    });

    function addAPP_IDToStringsXML(callback) {
        var data = fs.readFileSync(strings_dir, 'utf-8');

        // if (err) throw err;
        // console.log('APPLICATION_ID: manifest!', APPLICATION_ID);
        // console.log('addAPP_IDToManifest data:', data);

        var configNewData = '<string name="app_id"></string>"' + APPLICATION_ID + '"</resources>';

        // console.log('configNewData:', configNewData);
        // console.log('typeof data:', typeof data);

        if(!data.includes(APPLICATION_ID)){
            console.log('DOES NOT HAVE CURRENT APPLICATION_ID: ', APPLICATION_ID);


            var newData = data.replace(/<\/resources>/gim, configNewData);
            console.log('addAPP_IDToManifest newData:', newData);

            fs.writeFile(strings_dir, newData, 'utf-8', function(err) {
                if (err) throw err;
                console.log('Done addAPP_ID To Strings XML!');
                callback();
            });
        }
        console.log('-----------------------');
    };

    function getAPP_IDFromDataJs(C2_data_file, callback) {
        // MANIFEST EDIT
        fs.readJson(C2_data_file, 'utf8', (err, packageObj) => {
            if (err) {
                console.error('readJson: ', err);
            } else {
                printAllVals(packageObj.project);
                callback(APPLICATION_ID);
            }
        });
    }

    function printAllVals(obj) {
        for (let k in obj) {
            if (typeof obj[k] === "object") {
                printAllVals(obj[k])
            } else {
                if(typeof obj[k] === 'string'){
                    var temp = obj[k];
                    if(temp.includes(myValue) && k == 3){
                        console.log('yes');
                        console.log(k);
                        console.log(temp);
                        APPLICATION_ID = temp;
                    }
                }
            }
        }
    }

};