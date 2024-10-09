package com.client.xvideos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.client.xvideos.net.readHtmlFromURL
import com.client.xvideos.parcer.parserListVideo
import com.client.xvideos.ui.theme.XvideosTheme
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        plant(DebugTree())
        Timber.i("!!! Hello")

        runBlocking {
            val s = readHtmlFromURL()
            s

            parserListVideo(s)

        }

        setContent {
            XvideosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    XvideosTheme {
        Greeting("Android")
    }
}