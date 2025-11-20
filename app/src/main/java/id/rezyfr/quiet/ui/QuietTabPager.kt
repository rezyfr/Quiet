package id.rezyfr.quiet.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.rezyfr.quiet.navigation.QuietPagerContent
import id.rezyfr.quiet.ui.component.QuietBackground

@Composable
fun QuietTabPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {
    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            QuietPagerContent(
                page = page
            )
        }
    }
}