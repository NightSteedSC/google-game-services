module.exports = function(ctx) {
    var fs = require('fs-extra'),
        path = require('path'),
        rootdir = ctx.opts.projectRoot,
        android_dir = path.join(ctx.opts.projectRoot, 'platforms/android'),
        strings_dir = path.join(ctx.opts.projectRoot, 'platforms/android/app/src/main/res/values/strings.xml'),
        C2_data_file = rootdir + '/www/data.js';

    console.log("ctx: ", ctx);
    var myValue = "GoogleGamesAPPID";
    var APPLICATION_ID = '';

    getAPP_IDFromDataJs(C2_data_file, (APPLICATION_ID)=>{
        console.log('getAPP_IDFromDataJs DONE: ', APPLICATION_ID);
        addAPP_IDToStringsXML(()=>{
        });
    });


    function getAPP_IDFromDataJs(C2_data_file, callback) {
        // MANIFEST EDIT
        fs.readJson(C2_data_file, 'utf8', (err, packageObj) => {
            if (err) {
                console.log('getAPP_IDFromDataJs readJson ERROR: ', err);
                // console.error('readJson: ', err);
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

                    // console.log('printAllVals temp: ', temp);
                    // console.log('printAllVals k: ', k);

                    if(temp.includes(myValue)){
                        console.log('yes');
                        console.log(k);
                        console.log(temp);
                        APPLICATION_ID = temp;
                    }
                }
            }
        }
    }

    function addAPP_IDToStringsXML(callback) {
        var data = fs.readFileSync(strings_dir, 'utf-8');

        // if (err) throw err;
        // console.log('APPLICATION_ID: manifest!', APPLICATION_ID);
        // console.log('addAPP_IDToManifest data:', data);

        APPLICATION_ID = APPLICATION_ID.split("_");
        APPLICATION_ID = APPLICATION_ID[1];

        var configNewData = '<string name="app_id">"' + APPLICATION_ID + '"</string></resources>';

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
};