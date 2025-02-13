package cn.wthee.pcrtool.ui.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.CaptionText
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.common.VerticalGrid
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.VibrateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class ToolMenuData(
    @StringRes val titleId: Int,
    val iconType: MainIconType,
    val regionForNews: Int = 0
)

/**
 * 菜单
 */
@ExperimentalMaterialApi
@Composable
fun ToolMenu(actions: NavActions) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val list = arrayListOf(
        ToolMenuData(R.string.tool_pvp, MainIconType.PVP_SEARCH),
        ToolMenuData(R.string.tool_clan, MainIconType.CLAN),
        ToolMenuData(R.string.tool_leader, MainIconType.LEADER),
        ToolMenuData(R.string.tool_gacha, MainIconType.GACHA),
        ToolMenuData(R.string.tool_event, MainIconType.EVENT),
        ToolMenuData(R.string.tool_guild, MainIconType.GUILD),
        ToolMenuData(R.string.tool_mock_gacha, MainIconType.MOCK_GACHA),
        ToolMenuData(R.string.tool_more, MainIconType.TOOL_MORE),
    )


    VerticalGrid(
        maxColumnWidth = Dimen.toolMenuWidth,
        modifier = Modifier.animateContentSize(defaultSpring())
    ) {
        list.forEach {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Dimen.mediumPadding,
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding,
                        bottom = Dimen.largePadding
                    ),
                contentAlignment = Alignment.Center
            ) {
                MenuItem(coroutineScope, context, actions, it)
            }

        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun MenuItem(
    coroutineScope: CoroutineScope,
    context: Context,
    actions: NavActions,
    it: ToolMenuData
) {
    Column(
        modifier = Modifier
            .clip(Shape.medium)
            .clickable {
                VibrateUtil(context).single()
                getAction(coroutineScope, actions, it).invoke()
            }
            .defaultMinSize(minWidth = Dimen.menuItemSize)
            .padding(Dimen.smallPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconCompose(data = it.iconType, size = Dimen.menuIconSize)
        CaptionText(
            text = stringResource(id = it.titleId),
            modifier = Modifier.padding(top = Dimen.mediumPadding),
            textAlign = TextAlign.Start
        )
    }
}

fun getAction(
    coroutineScope: CoroutineScope,
    actions: NavActions,
    tool: ToolMenuData
): () -> Unit {

    return {
        when (tool.iconType) {
            MainIconType.CHARACTER -> actions.toCharacterList()
            MainIconType.GACHA -> actions.toGacha()
            MainIconType.CLAN -> actions.toClan()
            MainIconType.EVENT -> actions.toEvent()
            MainIconType.GUILD -> actions.toGuild()
            MainIconType.PVP_SEARCH -> actions.toPvp()
            MainIconType.LEADER -> actions.toLeader()
            MainIconType.EQUIP -> actions.toEquipList()
            MainIconType.TWEET -> actions.toTweetList()
            MainIconType.CHANGE_DATA -> navViewModel.openChangeDataDialog.postValue(true)
            MainIconType.COMIC -> actions.toComicList()
            MainIconType.DB_DOWNLOAD -> {
                coroutineScope.launch {
                    DatabaseUpdater.checkDBVersion(0)
                }
            }
            MainIconType.SKILL_LOOP -> actions.toAllSkillList()
            MainIconType.EQUIP_CALC -> actions.toAllEquipList()
            MainIconType.RANDOM_AREA -> actions.toRandomEquipArea(0)
            MainIconType.TOOL_MORE -> actions.toToolMore()
            MainIconType.NEWS -> actions.toNews()
            MainIconType.FREE_GACHA -> actions.toFreeGacha()
            MainIconType.MOCK_GACHA -> actions.toMockGacha()
            else -> {
            }
        }
    }

}