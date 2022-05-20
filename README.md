![](Resource/SDBridgeKotlin.png)
![language](https://img.shields.io/badge/Language-Kotlin-green)
![language](https://img.shields.io/badge/support-Javascript/Async/Await-green)
![language](https://img.shields.io/badge/support-Jitpack-green)
![language](https://img.shields.io/badge/support-bilibili_video-green)


[SDBridgeJava](https://github.com/SDBridge/SDBridgeJava) is [here](https://github.com/SDBridge/SDBridgeJava).

If your h5 partner confused about how to deal with iOS and Android.
[This Demo maybe help](https://github.com/SDBridge/TypeScriptDemo).

[bilibili video introduction is here](https://search.bilibili.com/all?vt=53806197&keyword=SDBridgeKotlin&from_source=webtop_search&spm_id_from=333.788).

Usage
-----

## JitPack.io

I strongly recommend https://jitpack.io
```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.SDBridge:SDBridgeKotlin:1.0.0'
}
```

1) Instantiate bridge with a WebView in Kotlin:
```Kotlin
  @SuppressLint("SetJavaScriptEnabled")
    private fun setupView(){
        val buttonSync = findViewById<Button>(R.id.buttonSync)
        val buttonAsync = findViewById<Button>(R.id.buttonAsync)
        mWebView = findViewById(R.id.webView)
        setAllowUniversalAccessFromFileURLs(mWebView!!)
        buttonSync.setOnClickListener(this)
        buttonAsync.setOnClickListener(this)
        bridge = WebViewJavascriptBridge(_context = this,_webView = mWebView )

        bridge?.consolePipe = object : ConsolePipe {
            override fun post(string : String){
                println("33333")
                println(string)
            }
        }
        bridge?.register("DeviceLoadJavascriptSuccess",object : Handler {
            override fun handler(map: HashMap<String, Any>?, callback: Callback) {
                println("Next line is javascript data->>>")
//                println(map)
                val result = HashMap<String, Any>()
                result["result"] = "Android"
                callback.call(result)
            }
        })
        mWebView!!.webViewClient = webClient
        // Loading html in local ，This way maybe meet cross domain. So You should not forget to set
        // /*...setAllowUniversalAccessFromFileURLs... */
        // If you loading remote web server,That can be ignored.
        mWebView!!.loadUrl("file:///android_asset/Demo.html")

    }

    private val webClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            println("shouldOverrideUrlLoading")
            return false
        }
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            println("onPageStarted")
            bridge?.injectJavascript()
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            println("onPageFinished")
        }
    }
```
2) In Kotlin, and call a Javascript Sync/Async function:
```Kotlin
  override fun onClick(v: View?){
        when(v?.id){
            R.id.buttonSync -> {
                val data = java.util.HashMap<String, Any>()
                data["AndroidKey00"] = "AndroidValue00"
                //call js Sync function
                bridge?.call("GetToken", data, object : Callback {
                    override fun call(map: HashMap<String, Any>?){
                        println("Next line is javascript data->>>")
                        println(map)
                    }
                })
            }
            R.id.buttonAsync ->{
                val data = java.util.HashMap<String, Any>()
                data["AndroidKey01"] = "AndroidValue01"
                //call js Async function
                bridge?.call("AsyncCall", data, object : Callback {
                    override fun call(map: HashMap<String, Any>?){
                        println("Next line is javascript data->>>")
                        println(map)
                    }
                })
            }
        }
    }
```
3) In javascript file or typescript and html file like :
	
```javascript
<div id="SDBridge"> web content </div>
<script>
    // Give webview 1.5s to load other javascript files.
    setTimeout(()=>{
        console.log("Javascript: Hello World.");
        const bridge = window.WebViewJavascriptBridge;
        // JS tries to call the native method to judge whether it has been loaded successfully and let itself know whether its user is in android app or IOS app
        bridge.callHandler('DeviceLoadJavascriptSuccess', {key: 'JSValue'}, function(response) {
            let result = response.result
            if (result === "iOS") {
                console.log("Javascript was loaded by IOS and successfully loaded.");
                document.getElementById("SDBridge").innerText = "Javascript was loaded by IOS and successfully loaded.";
                window.iOSLoadJSSuccess = true;
            } else if (result === "Android") {
                console.log("Javascript was loaded by Android and successfully loaded.");
                document.getElementById("SDBridge").innerText = "Javascript was loaded by Android and successfully loaded.";
                window.AndroidLoadJSSuccess = true;
        }
        });
        // JS register method is called by native
        bridge.registerHandler('GetToken', function(data, responseCallback) {
            console.log(data);
            document.getElementById("SDBridge").innerText = "JS get native data:" + JSON.stringify(data);
            let result = {token: "I am javascript's token"}
            //JS gets the data and returns it to the native
            responseCallback(result)
        });
        bridge.registerHandler('AsyncCall', function(data, responseCallback) {
            console.log(data);
            document.getElementById("SDBridge").innerText = "JS get native data:" + JSON.stringify(data);
            // Call await function must with  (async () => {})();
            (async () => {
                const callback = await generatorLogNumber(1);
                let result = {token: callback};
                responseCallback(result);
            })();
        });
        
        function generatorLogNumber(n){
            return new Promise(res => {
                setTimeout(() => {
                    res("Javascript async/await callback Ok");
                    }, 1000);
        })
    }
},1500);

</script>
```
# Global support for free
WhatsApp:
[SDBridgeKotlin Support](https://chat.whatsapp.com/CAh3TGcz6VdCUvnTAYURte)

Telegram:
[SDBridgeKotlin Support](https://t.me/+aB5MmX8f6gw0MmRl)

WhatsApp:
[SDBridgeKotlin Support](https://chat.whatsapp.com/CAh3TGcz6VdCUvnTAYURte)

WeChat Group:
![](Resource/SDBridgeKotlinSupport.png)

- Email: housenkui@gmail.com

## License

SDBridgeSwift is released under the MIT license. [See LICENSE](https://github.com/SDBridge/SDBridgeKotlin/blob/main/LICENSE) for details.
