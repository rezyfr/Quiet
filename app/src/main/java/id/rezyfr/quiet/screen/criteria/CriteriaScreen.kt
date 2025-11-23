package id.rezyfr.quiet.screen.criteria

import android.R.attr.onClick
import android.R.attr.text
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.rezyfr.quiet.R
import id.rezyfr.quiet.component.PrimaryButton
import id.rezyfr.quiet.component.WavyText
import id.rezyfr.quiet.ui.theme.QuietTheme
import id.rezyfr.quiet.ui.theme.spacing
import id.rezyfr.quiet.ui.theme.spacingH
import id.rezyfr.quiet.ui.theme.spacingSmall
import id.rezyfr.quiet.ui.theme.spacingX
import id.rezyfr.quiet.ui.theme.spacingXX
import id.rezyfr.quiet.ui.theme.spacingXXXX
import id.rezyfr.quiet.util.drawable

@Composable
fun CriteriaScreen() {
    CriteriaContent()
}
@Composable
fun CriteriaContent(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            ) {
                Column(Modifier.padding(spacingXX)) {
                    Text(stringResource(R.string.when_notification))
                    Spacer(
                        Modifier.height(
                            spacing
                        )
                    )
                    WavyText(text = stringResource(R.string.criteria_contains_any_of))
                }
            }
        },
        bottomBar = {
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(spacingXX),
            ) {
                Text(
                    text = stringResource(R.string.criteria_apply_filter_info),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(spacingX))
                PrimaryButton(
                    modifier =
                        Modifier.fillMaxWidth(), text = stringResource(R.string.apply_filter)
                ) { }
            }
        }) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            // TITLE BLOCK
            CriteriaGridItem(
                modifier = Modifier
                    .fillMaxWidth(0.3f),
                criteria = CriteriaItem(
                    name = stringResource(R.string.criteria_phrase_title),
                    icon = R.drawable.ic_double_quotes,
                    desc = stringResource(R.string.criteria_phrase_desc)
                ),
                onClick = { })
        }
    }
}
@Composable
fun CriteriaGridItem(modifier: Modifier = Modifier, criteria: CriteriaItem, onClick: () -> Unit) {
    val context = LocalContext.current

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            criteria.desc,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 20.sp
            ),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            textAlign = TextAlign.Center,
        )
        Icon(
            R.drawable.ic_arrow_down.drawable(context),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(spacingXX))
        Surface(
            shape =
                RoundedCornerShape(spacingH),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Column(
                Modifier.padding(vertical = spacingX, horizontal = spacingH),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    R.drawable.ic_double_quotes.drawable(context),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.height(spacingSmall))
                Text(
                    criteria.name,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

data class CriteriaItem(val name: String, @DrawableRes val icon: Int, val desc: String)
@Composable
@Preview(showBackground = true)
fun PreviewCriteriaContent() {
    QuietTheme {
        CriteriaContent()
    }
}
@Composable
@Preview(showBackground = true)
fun PreviewCriteriaItem() {
    QuietTheme {
        CriteriaGridItem(
            Modifier,
            CriteriaItem(
                name = stringResource(R.string.criteria_phrase_title),
                icon = R.drawable.ic_double_quotes,
                desc = stringResource(R.string.criteria_phrase_desc)
            ),
            onClick = {}
        )
    }
}