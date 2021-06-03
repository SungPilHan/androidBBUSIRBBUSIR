send("[*] Injected JS");

Java.perform(function(){
    var con_provider = Java.use("com.android.BBUSIRBBUSIR.TrackUserContentProvider");
    var Uri = Java.use("android.net.Uri");
    var DbUtils = Java.use("android.database.DatabaseUtils");

    var uri = Uri.parse(con_provider.URL.value);
    var cxt = getContext();
    if (cxt) {
        var resolver = cxt.getContentResolver();
        var query = resolver.query.overload('android.net.Uri', '[Ljava.lang.String;', 'java.lang.String', '[Ljava.lang.String;', 'java.lang.String');
        var cursor = query.call(resolver, uri, null, null, null, null);
        console.log(DbUtils.dumpCursorToString(cursor));
    }

});

function getContext() {
return Java.use('android.app.ActivityThread').currentApplication().getApplicationContext();
}