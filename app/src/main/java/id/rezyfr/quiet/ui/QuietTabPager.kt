package id.rezyfr.quiet.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import id.rezyfr.quiet.navigation.QuietPagerContent
import id.rezyfr.quiet.navigation.TOP_LEVEL_DESTINATIONS
import id.rezyfr.quiet.ui.component.QuietBackground

@Composable
fun QuietTabPager(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState() {
        TOP_LEVEL_DESTINATIONS.size
    }

    QuietBackground {
        Column(modifier = modifier.fillMaxSize()) {

            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState
            ) { page ->

                QuietPagerContent(
                    page = page
                )
            }
        }
    }
}