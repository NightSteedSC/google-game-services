// module.exports = function(ctx) {
//     var fs = require('fs-extra'),
//         path = require('path'),
//         rootdir = ctx.opts.projectRoot,
//         android_dir = path.join(ctx.opts.projectRoot, 'platforms/android'),
//         build_gradle_dir = path.join(ctx.opts.projectRoot, 'platforms/android/app/build.gradle'),
//         strings_dir = path.join(ctx.opts.projectRoot, 'platforms/android/app/src/main/res/values/strings.xml'),
//         __googlegameservicesappid_file = rootdir + '/www/__googlegameservicesappid.txt';
//
//     var GOOGLE_GAMES_SERVICES_APP_ID = '';
//
//     getGooglegameservicesAPP_IDFromFile(__googlegameservicesappid_file, ()=>{
//         console.error('get Google Games APP_ID From txt file DONE: ', GOOGLE_GAMES_SERVICES_APP_ID);
//         var configNewData = '<string name="google_game_services_app_id">"' + GOOGLE_GAMES_SERVICES_APP_ID + '"</string></resources>';
//         replaceDataInFile(strings_dir, configNewData, '</resources>', GOOGLE_GAMES_SERVICES_APP_ID);
//     });
//
//     var configNewData1 = "configurations {\nall*.exclude group: 'com.google.guava', module: 'listenablefuture'\n}\n for (def func : cdvPluginPostBuildExtras)";
//     var currentStringDataToUse1 = "for (def func : cdvPluginPostBuildExtras)"
//     replaceDataInFile(build_gradle_dir, configNewData1, currentStringDataToUse1, configNewData1);
//
//     function getGooglegameservicesAPP_IDFromFile(googlegameservicesAPP_ID, callback) {
//         // MANIFEST EDIT
//         fs.readFile(googlegameservicesAPP_ID, 'utf8' , (err, data) => {
//             if (err) {
//                 console.error(err);
//                 return
//             }
//             console.log('data file: ', data);
//
//             GOOGLE_GAMES_SERVICES_APP_ID = data;
//             callback(GOOGLE_GAMES_SERVICES_APP_ID);
//         });
//     }
//
//     function replaceDataInFile(filePath, newData, replaceData, ifIncludesDontReplace) {
//         fs.readFile(filePath, 'utf8', function (err,data) {
//             if (err) {
//                 console.log('replaceDataInFile err, file: ' + filePath)
//                 return console.log(err);
//             }
//             // console.log(data);
//
//             if(!data.includes(ifIncludesDontReplace)){
//                 fs.writeFile(filePath, data.replace(replaceData, newData), err => {
//                     if (err) {
//                         console.error(err)
//                         return
//                     }
//                     console.log('replaceDataInFile write File successfull: ', filePath);
//                 })
//             }
//         });
//     };
// };