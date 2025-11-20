package id.rezyfr.quiet.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import id.rezyfr.quiet.navigation.TOP_LEVEL_DESTINATIONS
import id.rezyfr.quiet.ui.QuietTabPager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MainBottomPager() {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState() {
        TOP_LEVEL_DESTINATIONS.size
    }
    Scaffold(
        bottomBar = {
            MainBottomNav(coroutineScope, pagerState)
        }
    ) {
        QuietTabPager(
            Modifier
            .fillMaxSize()
            .padding(it),
            pagerState
        )
    }
}

@Composable
fun MainBottomNav(
    coroutineScope: CoroutineScope,
    pagerState: PagerState
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        TOP_LEVEL_DESTINATIONS.forEachIndexed { index, dest ->
            val selected = pagerState.currentPage == index
            NavigationBarItem(
                selected = selected,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                icon = {
                    Icon(
                        imageVector = dest.icon,
                        contentDescription = null
                    )
                },
                label = { Text(dest.route.capitalize(Locale.ROOT)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    }
}