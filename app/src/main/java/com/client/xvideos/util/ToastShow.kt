package com.client.xvideos.util

import android.widget.Toast
import com.client.xvideos.App
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun Toast(text: String, long: Boolean = false) {
    GlobalScope.launch(Dispatchers.Main) {
        Toast.makeText(
            App.instance.applicationContext,
            text,
            if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).show()
    }
}

