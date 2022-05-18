package com.housenkui.studykotlin

import com.google.gson.internal.LinkedTreeMap

interface Handler {
    fun handler(map: LinkedTreeMap<*, *>?, callback: Callback)
}