send("[*] Injected JS");

Java.perform(function(){
    var target = Java.use("com.android.BBUSIRBBUSIR.SplashActivity");
    target.doesSUexist.implementation = function(arg1){
        console.log("doesSUexist");
        return false;
    }
    target.doesSuperuserApkExist.implementation = function(arg1){
        console.log("doesSuperuserApkExist");
        return false;
    }
})