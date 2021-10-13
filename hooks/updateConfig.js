module.exports = function(ctx) {
    var fs = require('fs-extra'),
        path = require('path'),
        rootdir = ctx.opts.projectRoot,
        android_dir = path.join(ctx.opts.projectRoot, 'platforms/android'),
        build_gradle_dir = path.join(ctx.opts.projectRoot, 'platforms/android/app/build.gradle'),
        strings_dir = path.join(ctx.opts.projectRoot, 'platforms/android/app/src/main/res/values/strings.xml'),
        __googlegameservicesappid_file = rootdir + '/www/__googlegameservicesappid.txt';

    console.log("ctx: ", ctx);
    var GOOGLE_GAMES_SERVICES_APP_ID = '';

    // // MANIFEST EDIT

    addConfigToBuildGradleApp(()=>{

    })

    getGooglegameservicesAPP_IDFromFile(__googlegameservicesappid_file, ()=>{
        console.error('get Google Games APP_ID From txt file DONE: ', GOOGLE_GAMES_SERVICES_APP_ID);
        addAdmobIDToStringsXML(()=>{
        });
    });



    function addConfigToBuildGradleApp(callback) {
        var data = fs.readFileSync(build_gradle_dir, 'utf-8');
        var configNewData = "configurations {all*.exclude group: 'com.google.guava', module: 'listenablefuture'} for (def func : cdvPluginPostBuildExtras) {";

        var currentStringDataToUse = "for (def func : cdvPluginPostBuildExtras) {"

        if(!data.includes(configNewData)){

            var newData = data.replace(currentStringDataToUse, configNewData);

            fs.writeFile(build_gradle_dir, newData, 'utf-8', function(err) {
                if (err) throw err;
                console.log('Done BUILD GRADLE!');

                var dataChanged = fs.readFileSync(build_gradle_dir, 'utf-8');

                console.log('BUILD GRADLE dataChanged:');
                console.log(dataChanged);


                if(!dataChanged.includes(configNewData)){
                    console.log('DOES NOT HAVE CURRENT BUILD GRADLE dataChanged:: ', GOOGLE_GAMES_SERVICES_APP_ID);

                    fs.writeFile(build_gradle_dir, newData, 'utf-8', function(err) {
                        callback();
                    });
                } else {
                    callback();
                }
            });
        } else {
            callback();
        }
        console.log('-----------------------');
    }

    function addAdmobIDToStringsXML(callback) {
        var data = fs.readFileSync(strings_dir, 'utf-8');
        var configNewData = '<string name="google_game_services_app_id">"' + GOOGLE_GAMES_SERVICES_APP_ID + '"</string></resources>';

        if(!data.includes(GOOGLE_GAMES_SERVICES_APP_ID)){
            console.log('DOES NOT HAVE CURRENT GAMES APPLICATION_ID: ', GOOGLE_GAMES_SERVICES_APP_ID);

            var newData = data.replace(/<\/resources>/gim, configNewData);

            fs.writeFile(strings_dir, newData, 'utf-8', function(err) {
                if (err) throw err;
                console.log('Done google_game_services_app_id To Strings XML!');

                var dataChanged = fs.readFileSync(strings_dir, 'utf-8');

                console.log('google_game_services_app_id dataChanged:');
                console.log(dataChanged);


                if(!dataChanged.includes(GOOGLE_GAMES_SERVICES_APP_ID)){
                    console.log('DOES NOT HAVE CURRENT GAMES  APPLICATION_ID 2: ', GOOGLE_GAMES_SERVICES_APP_ID);

                    fs.writeFile(strings_dir, newData, 'utf-8', function(err) {
                        callback();
                    });
                } else {
                    callback();
                }
            });
        } else {
            callback();
        }
        console.log('-----------------------');
    };

    function getGooglegameservicesAPP_IDFromFile(googlegameservicesAPP_ID, callback) {
        // MANIFEST EDIT
        fs.readFile(googlegameservicesAPP_ID, 'utf8' , (err, data) => {
            if (err) {
                console.error(err);
                return
            }
            console.log('data file: ', data);

            GOOGLE_GAMES_SERVICES_APP_ID = data;
            callback(GOOGLE_GAMES_SERVICES_APP_ID);
        });
    }
};