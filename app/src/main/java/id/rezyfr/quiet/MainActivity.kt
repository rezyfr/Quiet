package id.rezyfr.quiet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.rezyfr.quiet.navigation.AppComposeNavigator
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val navigator by inject<AppComposeNavigator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent { App(navigator) }
    }
}
