package id.rezyfr.quiet

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
import id.rezyfr.quiet.navigation.AppComposeNavigator
import id.rezyfr.quiet.ui.theme.QuietTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val navigator by inject<AppComposeNavigator>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(navigator)
        }
    }
}