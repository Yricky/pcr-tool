package cn.wthee.pcrtool.ui.equip

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel


/**
 * 装备详情
 *
 * @param equipId 装备编号
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun EquipMainInfo(
    equipId: Int,
    toEquipMaterial: (Int) -> Unit, equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val equipMaxData =
        equipmentViewModel.getEquip(equipId).collectAsState(initial = EquipmentMaxData()).value
    //收藏状态
    val filter = navViewModel.filterEquip.observeAsState()
    val loved = remember {
        mutableStateOf(filter.value?.starIds?.contains(equipId) ?: false)
    }
    filter.value?.let { filterValue ->
        filterValue.starIds =
            GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_EQUIP, "")) ?: arrayListOf()
        loved.value = filterValue.starIds.contains(equipId)
    }
    val text = if (loved.value) "" else stringResource(id = R.string.love_equip)

    Box(
        modifier = Modifier
            .padding(top = Dimen.largePadding)
            .fillMaxSize()
    ) {

        Column {
            if (equipMaxData.equipmentId != Constants.UNKNOWN_EQUIP_ID) {
                MainText(
                    text = equipMaxData.equipmentName,
                    color = if (loved.value) MaterialTheme.colors.primary else Color.Unspecified,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    selectable = true
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.largePadding)
                ) {
                    IconCompose(data = getEquipIconUrl(equipId))
                    Subtitle2(
                        text = equipMaxData.getDesc(),
                        modifier = Modifier.padding(start = Dimen.mediuPadding),
                        selectable = true
                    )
                }
                //属性
                AttrList(attrs = equipMaxData.attr.allNotZero())

            }
            SlideAnimation(visible = equipMaxData.equipmentId != Constants.UNKNOWN_EQUIP_ID) {
                //合成素材
                if (filter.value != null) {
                    EquipMaterialList(equipMaxData, filter.value!!, toEquipMaterial)
                }
            }
        }
        //装备收藏
        FabCompose(
            iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
            modifier = Modifier
                .padding(
                    end = Dimen.fabMarginEnd,
                    start = Dimen.fabMargin,
                    top = Dimen.fabMargin,
                    bottom = Dimen.fabMargin,
                )
                .align(Alignment.BottomEnd),
            text = text
        ) {
            filter.value?.addOrRemove(equipId)
            loved.value = !loved.value
        }
    }
}

/**
 * 装备合成素材
 *
 * @param equip 装备信息
 * @param filter 装备过滤
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun EquipMaterialList(
    equip: EquipmentMaxData,
    filter: FilterEquipment,
    toEquipMaterial: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val materialList =
        equipmentViewModel.getEquipInfos(equip).collectAsState(initial = arrayListOf()).value

    Column {
        Spacer(
            modifier = Modifier
                .padding(Dimen.largePadding)
                .width(Dimen.smallIconSize)
                .height(Dimen.lineHeight)
                .background(MaterialTheme.colors.primary)
                .align(Alignment.CenterHorizontally)
        )
        //装备合成素材
        VerticalGrid(maxColumnWidth = Dimen.iconSize * 2) {
            materialList.forEach { material ->
                val loved = filter.starIds.contains(material.id)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimen.largePadding,
                            end = Dimen.largePadding,
                            bottom = Dimen.largePadding
                        ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconCompose(data = getEquipIconUrl(material.id)) {
                        toEquipMaterial(material.id)
                    }
                    SelectText(
                        selected = loved,
                        text = material.count.toString()
                    )
                }
            }
        }
    }
}