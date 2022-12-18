package com.housenkui.sdbridgekotlin

import android.annotation.SuppressLint
import android.content.Context
import android.telecom.Call
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class WebViewJavascriptBridge(_context: Context?, _webView: WebView?) {
    private var context: Context? = _context
    private var webView: WebView? = _webView
    var consolePipe: ConsolePipe? = null
    private var responseCallbacks: MutableMap<String, Callback<*>> = java.util.HashMap()
    private var messageHandlers: MutableMap<String, Handler<*,*>> = java.util.HashMap()
    private var uniqueId = 0


    val deserializer = ResponseMessageDeserializer(responseCallbacks, messageHandlers)


    /**
     * google object serializer
     */
    private var gson: Gson = GsonBuilder()
        .registerTypeAdapter(CallMessage::class.java, CallMessageSerializer)
        .registerTypeAdapter(ResponseMessage::class.java, deserializer)
        .create()

    
    companion object {
        val charsToReplace = mapOf<Char, String>(
            '\\' to "\\\\",
        '\"' to "\\\"",
        '\'' to "\\\'",
        '\n' to "\\n",
        '\r' to "\\r",
        '\u000C' to "\\u000C",
        '\u2028' to "\\u2028",
        '\u2029' to "\\u2029"
        )
    }




    init {
        setupBridge()
    }
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    fun setupBridge() {
        println("setupBridge")
        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true
        // 开启js支持
        webView!!.addJavascriptInterface(this, "normalPipe")
        webView!!.addJavascriptInterface(this, "consolePipe")
    }
    @JavascriptInterface
    fun postMessage(data: String?) {
        flush(data)
    }
    @JavascriptInterface
    fun receiveConsole(data: String?) {
        if (consolePipe != null) {
            consolePipe!!.post(data!!)
        }
    }
    fun injectJavascript() {
        val script = getFromAssets(context!!, "bridge.js")
        webView!!.loadUrl("javascript:$script")
        val script1 = getFromAssets(context!!, "hookConsole.js")
        webView!!.loadUrl("javascript:$script1")
    }
    fun register(handlerName: String?, handler: Handler<*,*>) {
        messageHandlers[handlerName!!] = handler
    }
    fun remove(handlerName: String?) {
        messageHandlers.remove(handlerName!!)
    }

    fun call(
        handlerName: String,
        callback: Callback<*>? = null,
        sync: Boolean = false
    ) {

        val message = CallMessage(
            handlerName,
            null,
            callback?.let {
                uniqueId += 1
                val callbackId = "native_cb_$uniqueId"
                responseCallbacks[callbackId] = it
                callbackId
            }
        )
        dispatch(message, sync)
    }

    fun <P> call(
        handlerName: String,
        data: P? = null,
        callback: Callback<*>? = null,
        sync: Boolean = false
    ) {

        val message = CallMessage(
            handlerName,
            data,
            callback?.let {
                uniqueId += 1
                val callbackId = "native_cb_$uniqueId"
                responseCallbacks[callbackId] = it
                callbackId
            }
        )
        dispatch(message, sync)
    }



    private fun flush(messageString: String?) {
        if (messageString == null) {
            println("Javascript give data is null")
            return
        }
        val message = gson.fromJson(
            messageString,
            ResponseMessage::class.java
        )
        val responseId = message.responseId
        if (responseId != null) {
            val callback: Callback<*> = responseCallbacks[responseId]!!
            callback.invoke(message.responseData?:Unit)
            responseCallbacks.remove(responseId)
        } else {
            val handlerName = message.handlerName
            val handler = messageHandlers[handlerName]
            if (handler == null) {
                val error = String.format(
                    "NoHandlerException, No handler for message from JS:%s",
                    handlerName
                )
                println(error)
                return
            }
            val responseData = handler.invoke(message.data?:Unit)
            if (responseData !is Unit) {
                message.callbackId?.let {
                    dispatch(
                        ResponseMessage(
                            it,
                            responseData,
                            null,
                            null,
                            null,
                        )
                    )
                }
            }
        }
    }

    private fun dispatch(message: Any, sync: Boolean = false) {
        val messageString = StringBuilder()
        val json = gson.toJson(message)
        json.forEach {
            //replace all special character
            messageString.append(charsToReplace[it]?:it)
        }
        messageString.insert(0, "WebViewJavascriptBridge.handleMessageFromNative('")
        messageString.append("');")
        val runnable = Runnable { webView!!.evaluateJavascript(messageString.toString(), null) }
        if (sync) {
            runnable.run()
        } else {
            webView!!.post(runnable)
        }
    }

    private fun getFromAssets(context: Context, fileName: String): String? {
        try {
            val inputReader = InputStreamReader(context.resources.assets.open(fileName))
            val bufReader = BufferedReader(inputReader)
            var line: String?
            var result: String? = ""
            while (bufReader.readLine().also { line = it } != null) result += line
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}