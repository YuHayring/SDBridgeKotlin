package com.housenkui.sdbridgekotlin

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import java.lang.reflect.InvocationTargetException

class MainActivity : AppCompatActivity() , View.OnClickListener{
    private var mWebView: WebView? = null
    private var bridge: WebViewJavascriptBridge? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
    }
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
                println("Next line is javascript console.log->>>")
                println(string)
            }
        }
        bridge?.register("DeviceLoadJavascriptSuccess",object : Handler {
            override fun handler(map: HashMap<String, Any>?, callback: Callback) {
                println("Next line is javascript data->>>")
                println(map)
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

//      index.html use SDBridge.js. This js file was create by webpack.
//      mWebView!!.loadUrl("file:///android_asset/index.html")


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

    //Allow Cross Domain
    private fun setAllowUniversalAccessFromFileURLs(webView: WebView) {
        try {
            val clazz: Class<*> = webView.settings.javaClass
            val method = clazz.getMethod(
                "setAllowUniversalAccessFromFileURLs", Boolean::class.javaPrimitiveType
            )
            method.invoke(webView.settings, true)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}