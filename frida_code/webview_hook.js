send("[*] Injected JS");

Java.perform(function() {
  
    var WebView = Java.use("android.webkit.WebView");
    
    WebView.loadUrl.overload('java.lang.String').implementation = function(url) {
      console.log("[*] Loaded URL into WebView: " + url);
      this.loadUrl.overload('java.lang.String').call(this, url);
     };
     
    WebView.addJavascriptInterface.overload('java.lang.Object','java.lang.String').implementation = function(object, name) {
      console.log("[*] Java object's methods is accessed from JavaScript: " + name);
      this.addJavascriptInterface.overload('java.lang.Object','java.lang.String').call(this, object, name);
     };

     var jsinterface = Java.use("com.android.BBUSIRBBUSIR.SignupActivity$AndroidBridge");

     jsinterface.setMessage.implementation = function(arg){
        console.log("message is : "+ arg);
        this.setMessage.call(this, arg);
     }
  });