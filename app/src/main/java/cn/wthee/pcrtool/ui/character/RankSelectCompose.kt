package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.common.ChipGroup
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.theme.Dimen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * RANK 选择页面
 * @param rank0 当前rank
 * @param rank1 目标rank
 * @param maxRank rank最大值
 * @param type 0：默认，1：限制（当前[rank0] <= 目标[rank1]）
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun RankSelectCompose(
    rank0: MutableState<Int>,
    rank1: MutableState<Int>,
    maxRank: Int,
    coroutineScope: CoroutineScope,
    sheetState: ModalBottomSheetState,
    navViewModel: NavViewModel,
    type: Int = 0
) {
    val rankList = arrayListOf<Int>()
    for (i in maxRank downTo 1) {
        rankList.add(i)
    }
    val ok = navViewModel.fabOKCilck.observeAsState().value ?: false
    //选择
    val selectIndex0 = remember {
        mutableStateOf(maxRank - rank0.value)
    }
    val selectIndex1 = remember {
        mutableStateOf(maxRank - rank1.value)
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(
                bottom = Dimen.sheetMarginBottom,
                top = Dimen.largePadding,
                start = Dimen.mediumPadding,
            )
    ) {
        //RANK 选择
        if (ok) {
            coroutineScope.launch {
                sheetState.hide()
            }
            navViewModel.fabOKCilck.postValue(false)
            navViewModel.fabMainIcon.postValue(MainIconType.BACK)
            rank0.value = maxRank - selectIndex0.value
            rank1.value = maxRank - selectIndex1.value
            if (type == 0) {
                navViewModel.curRank.postValue(maxRank - selectIndex0.value)
                navViewModel.targetRank.postValue(maxRank - selectIndex1.value)
            } else {
                navViewModel.curRank1.postValue(maxRank - selectIndex0.value)
                navViewModel.targetRank1.postValue(maxRank - selectIndex1.value)
            }

        }
        MainText(text = stringResource(id = R.string.cur_rank))
        RankSelectItem(selectIndex = selectIndex0, rankList = rankList)
        MainText(text = stringResource(id = R.string.target_rank))
        if (type == 1) {
            //只可选择比当前大的值
            val newRankList = arrayListOf<Int>()
            for (i in maxRank downTo maxRank - selectIndex0.value) {
                newRankList.add(i)
            }
            RankSelectItem(selectIndex = selectIndex1, rankList = newRankList)
        } else {
            RankSelectItem(selectIndex = selectIndex1, rankList = rankList)
        }
    }
}


/**
 * RANK 选择器
 */
@ExperimentalFoundationApi
@Composable
fun RankSelectItem(selectIndex: MutableState<Int>, rankList: List<Int>) {
    Box {
        val chipData = arrayListOf<ChipData>()
        rankList.forEachIndexed { index, i ->
            chipData.add(ChipData(index, rankFillBlank(i)))
        }
        ChipGroup(chipData, selectIndex, type = 1)
    }
}

/**
 * 填充空格
 */
private fun rankFillBlank(rank: Int): String {
    return when (rank) {
        in 0..9 -> "0$rank"
        else -> "$rank"

    }
}
