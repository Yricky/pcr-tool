package cn.wthee.pcrtool.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.defaultTween
import cn.wthee.pcrtool.utils.VibrateUtil
import com.google.accompanist.insets.navigationBarsPadding

/**
 * 通用悬浮按钮
 */
@Composable
fun FabCompose(
    iconType: Any,
    modifier: Modifier = Modifier,
    text: String = "",
    hasNavBarPadding: Boolean = true,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var mModifier = if (hasNavBarPadding) {
        modifier.navigationBarsPadding(bottom = true, start = false, end = false)
    } else {
        modifier
    }

    if (text != "") {
        mModifier = mModifier.padding(start = Dimen.textfabMargin, end = Dimen.textfabMargin)
    }

    SmallFloatingActionButton(
        onClick = {
            VibrateUtil(context).single()
            onClick.invoke()
        },
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        containerColor = MaterialTheme.colorScheme.background,
        modifier = mModifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = if (text != "") {
                Modifier.padding(start = Dimen.largePadding)
            } else {
                Modifier
            }
        ) {
            IconCompose(
                data = iconType,
                size = Dimen.fabIconSize,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = if (text != "") {
                    Modifier.padding(start = Dimen.mediumPadding, end = Dimen.largePadding)
                } else {
                    Modifier
                }.animateContentSize(defaultTween()),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

}

@Preview
@Composable
private fun FabComposePreview() {
    PreviewBox {
        Row {
            FabCompose(iconType = MainIconType.ANIMATION) {

            }
            FabCompose(iconType = MainIconType.ANIMATION, text = "fab") {

            }
        }
    }
}
