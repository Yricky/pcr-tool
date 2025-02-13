package cn.wthee.pcrtool.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.utils.VibrateUtil
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter

const val RATIO = 1.78f

// 741 * 1200
const val RATIO_COMIC = 0.6175f
const val RATIO_COMMON = 371 / 208f
const val RATIO_BANNER = 1024 / 682f


@Composable
fun ImageCompose(
    modifier: Modifier = Modifier,
    data: Any,
    ratio: Float,
    @DrawableRes loadingId: Int? = null,
    @DrawableRes errorId: Int? = null,
    contentScale: ContentScale = ContentScale.FillWidth,
    onSuccess: () -> Unit = {}
) {
    var mModifier = modifier
    if (ratio > 0) {
        mModifier = modifier
            .aspectRatio(ratio)
    }

    AsyncImage(
        model = data,
        contentDescription = null,
        contentScale = contentScale,
        placeholder = loadingId?.let { rememberAsyncImagePainter(it, contentScale = contentScale) },
        error = errorId?.let { rememberAsyncImagePainter(it, contentScale = contentScale) },
        onSuccess = {
            onSuccess.invoke()
        },
        modifier = mModifier
    )
}


@Composable
fun StoryImageCompose(
    data: Any,
    @DrawableRes loadingId: Int? = null,
    @DrawableRes errorId: Int? = null,
    contentScale: ContentScale = ContentScale.FillWidth
) {
    SubcomposeAsyncImage(
        model = data,
        contentDescription = null,
        contentScale = contentScale,
        loading = {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = loadingId,
                    contentDescription = null,
                    modifier = Modifier.aspectRatio(RATIO_COMMON)
                )
            }
        },
        error = {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = errorId,
                    contentDescription = null,
                    modifier = Modifier.aspectRatio(RATIO_COMMON)
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * 角色位置图标
 */
@Composable
fun PositionIcon(modifier: Modifier = Modifier, position: Int, size: Dp = Dimen.smallIconSize) {
    val positionIconId = when (position) {
        in 0..299 -> R.drawable.ic_position_0
        in 300..599 -> R.drawable.ic_position_1
        in 600..9999 -> R.drawable.ic_position_2
        else -> R.drawable.ic_position_2
    }
    IconCompose(
        data = positionIconId,
        size = size,
        modifier = modifier
    )
}

/**
 * 图标
 */
@Composable
fun IconCompose(
    modifier: Modifier = Modifier,
    data: Any,
    size: Dp = Dimen.iconSize,
    tint: Color = MaterialTheme.colorScheme.primary,
    colorFilter: ColorFilter? = null,
    wrapSize: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var mModifier = if (onClick != null) {
        modifier
            .clip(Shape.small)
            .clickable(onClick = {
                VibrateUtil(context).single()
                onClick.invoke()
            })
    } else {
        modifier.clip(Shape.small)
    }
    if (!wrapSize) {
        mModifier = mModifier.size(size)

    }


    if (data is MainIconType) {
        Icon(
            imageVector = data.icon,
            contentDescription = null,
            tint = tint,
            modifier = mModifier
        )
    } else {
        val contentScale = ContentScale.Crop
        AsyncImage(
            model = data,
            colorFilter = colorFilter,
            contentDescription = null,
            contentScale = contentScale,
            placeholder = rememberAsyncImagePainter(
                R.drawable.unknown_gray,
                contentScale = contentScale
            ),
            error = rememberAsyncImagePainter(R.drawable.unknown_item, contentScale = contentScale),
            modifier = mModifier.aspectRatio(1f)
        )
    }
}
