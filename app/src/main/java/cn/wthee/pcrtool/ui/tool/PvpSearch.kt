package cn.wthee.pcrtool.ui.tool

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.db.view.getIdStr
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.PvpResultData
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.service.PvpService
import cn.wthee.pcrtool.service.getFloatWindowHeight
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.gson.JsonArray
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


/**
 * 竞技场查询
 */
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun PvpSearchCompose(
    floatWindow: Boolean = false,
    toCharacter: (Int) -> Unit,
    characterViewModel: CharacterViewModel = hiltViewModel(),
    pvpViewModel: PvpViewModel = hiltViewModel()
) {
    //获取数据
    val data = characterViewModel.getAllCharacter().collectAsState(initial = arrayListOf()).value
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val mediuPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediuPadding
    val serviceIntent = Intent(context, PvpService::class.java)
    val tip = stringResource(id = R.string.tip_select_5)


    //显示类型
    val showResult = navViewModel.showResult.observeAsState().value ?: false

    //已选择的id
    val selectedIds = navViewModel.selectedPvpData.observeAsState().value ?: arrayListOf()

    val research = remember {
        mutableStateOf(true)
    }
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false

    if (showResult) {
        navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
    } else {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
    }

    val url = stringResource(id = R.string.pcrdfans_url)
    val urlTip = stringResource(id = R.string.pcrdfans_com)
    val pagerState = rememberPagerState(pageCount = 2)
    val tabs = arrayListOf(
        stringResource(id = R.string.character),
        stringResource(id = R.string.title_love)
    )

    //返回选择
    if (close) {
        navViewModel.showResult.postValue(false)
        research.value = false
        navViewModel.fabCloseClick.postValue(false)
        pvpViewModel.requesting = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            //标题
            if (!floatWindow) {
                MainTitleText(
                    text = stringResource(id = R.string.pcrdfans),
                    modifier = Modifier
                        .padding(start = Dimen.largePadding, top = Dimen.largePadding)
                        .clickable {
                            openWebView(context, url, urlTip)
                        }
                )
            }
            //已选择列表
            Row(
                modifier = Modifier
                    .padding(
                        top = if (floatWindow) Dimen.smallPadding else Dimen.largePadding,
                        start = if (floatWindow) Dimen.smallPadding else Dimen.largePadding,
                        end = if (floatWindow) Dimen.smallPadding else Dimen.largePadding,
                        bottom = Dimen.smallPadding
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                selectedIds.forEach {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        PvpIconItem(
                            selectedIds = selectedIds,
                            it = it,
                            floatWindow = floatWindow,
                            selectedEffect = false
                        )
                    }
                }
            }
            //供选择列表
            if (showResult) {
                if (!research.value && selectedIds.contains(PvpCharacterData())) {
                    ToastUtil.short(tip)
                    navViewModel.showResult.postValue(false)
                } else {
                    PvpSearchResult(selectedIds, floatWindow)
                }
            } else {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                        )
                    },
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.618f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    tabs.forEachIndexed { index, s ->
                        Tab(selected = pagerState.currentPage == index, onClick = {
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        }) {
                            Subtitle2(text = s, modifier = Modifier.padding(Dimen.smallPadding))
                        }
                    }
                }
                SlideAnimation(visible = data.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.padding(top = mediuPadding)
                    ) { pageIndex ->
                        when (pageIndex) {
                            0 -> PvpCharacterSelectPage(
                                selectedIds = selectedIds,
                                floatWindow = floatWindow,
                                data = data,
                            )
                            else -> {
                                PvpFavorites(
                                    scrollState = rememberLazyListState(),
                                    toCharacter = toCharacter,
                                    pvpViewModel = pvpViewModel,
                                    floatWindow = floatWindow
                                )
                            }
                        }
                    }
                }
            }
        }
        //底部悬浮按钮
        if (!showResult && !floatWindow) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = if (floatWindow) Dimen.fabMargin else Dimen.fabMarginEnd,
                        bottom = Dimen.fabMargin
                    )
            ) {
                //悬浮窗
                FabCompose(
                    iconType = MainIconType.FLOAT,
                    modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                ) {
                    val homeIntent = Intent(Intent.ACTION_MAIN)
                    homeIntent.addCategory(Intent.CATEGORY_HOME)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.canDrawOverlays(context)) {
                            //启动悬浮服务
                            navViewModel.floatServiceRun.postValue(true)
                            context.startService(serviceIntent)
                            context.startActivity(homeIntent)
                        } else {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${MyApplication.context.packageName}")
                            )
                            context.startActivity(intent)
                        }
                    } else {
                        navViewModel.floatServiceRun.postValue(true)
                        context.startService(serviceIntent)
                        context.startActivity(homeIntent)
                    }

                }
                //跳转
                FabCompose(
                    iconType = MainIconType.FRIEND_LINK,
                    modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                ) {
                    //打开网页
                    openWebView(context, url, urlTip)
                }
                //添加信息
                val addUrl = stringResource(id = R.string.pcrdfans_upload_url)
                val addTip = stringResource(id = R.string.pvp_info_add_tip)
                FabCompose(
                    iconType = MainIconType.PVP_ADD,
                    modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                ) {
                    //打开网页
                    openWebView(context, addUrl, addTip)
                }
                //查询
                FabCompose(
                    iconType = MainIconType.PVP_SEARCH,
                    text = if (floatWindow) "" else stringResource(id = R.string.pvp_search)
                ) {
                    //查询
                    scope.launch {
                        if (selectedIds.contains(PvpCharacterData())) {
                            ToastUtil.short(tip)
                        } else {
                            pvpViewModel.pvpResult.postValue(null)
                            navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                            navViewModel.showResult.postValue(true)
                        }
                    }
                }
            }
        }
    }

}

/**
 * 角色选择
 */
@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun PvpCharacterSelectPage(
    selectedIds: ArrayList<PvpCharacterData>,
    floatWindow: Boolean,
    data: List<PvpCharacterData>
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    //选择页面
    val character0 = data.filter {
        it.position in 0..299
    }
    val character1 = data.filter {
        it.position in 300..599
    }
    val character2 = data.filter {
        it.position in 600..9999
    }
    val spanCount = 5
    //站位图标在列表中的位置
    val positions = arrayListOf(0, 0, 0)
    val padding = (Dimen.smallPadding * 2).value.dp2px
    val itemHeight = if (!floatWindow) {
        ScreenUtil.getWidth() / 5
    } else {
        ((getFloatWindowHeight() * 0.618f / 5 - padding) / 0.618f + padding).toInt()
    }
    val lines = arrayListOf(0, 0, 0)
    lines[0] = getLine(character0, spanCount) + 1
    lines[1] = getLine(character1, spanCount) + 1
    lines[2] = getLine(character2, spanCount) + 1
    //中卫以上填充数
    positions[1] = lines[0] * itemHeight
    //后卫以上填充数
    positions[0] = (lines[0] + lines[1]) * itemHeight

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            PvpPositionIcon(R.drawable.ic_position_0, itemHeight)
            VerticalGrid(spanCount = spanCount) {
                character0.forEach {
                    PvpIconItem(selectedIds, it, floatWindow)
                }
            }
            PvpPositionIcon(R.drawable.ic_position_1, itemHeight)
            VerticalGrid(spanCount = spanCount) {
                character1.forEach {
                    PvpIconItem(selectedIds, it, floatWindow)
                }
            }
            PvpPositionIcon(R.drawable.ic_position_2, itemHeight)
            VerticalGrid(spanCount = spanCount) {
                character2.forEach {
                    PvpIconItem(selectedIds, it, floatWindow)
                }
            }
        }
        //指示器
        val modifier = if (floatWindow) {
            Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(bottom = Dimen.fabMargin, end = Dimen.smallPadding)
        } else {
            Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(
                    bottom = Dimen.fabSize + Dimen.fabMargin + Dimen.mediuPadding,
                    start = Dimen.mediuPadding,
                    end = Dimen.fabMargin
                )
        }
        Row(modifier = modifier) {
            val icons = arrayListOf(
                R.drawable.ic_position_2,
                R.drawable.ic_position_1,
                R.drawable.ic_position_0,
            )
            icons.forEachIndexed { index, it ->
                Image(painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(Dimen.smallPadding)
                        .clip(CircleShape)
                        .size(Dimen.fabIconSize)
                        .clickable {
                            VibrateUtil(context).single()
                            scope.launch {
                                scrollState.animateScrollTo(positions[index])
                            }
                        })
            }
        }
    }
}

/**
 * 获取行数
 */
private fun getLine(
    list: List<PvpCharacterData>,
    spanCount: Int
) = if (list.size % spanCount == 0) {
    list.size / spanCount
} else {
    list.size / spanCount + 1
}

/**
 * 位置图标
 */
@Composable
private fun PvpPositionIcon(iconId: Int, height: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.px2dp.dp),
        contentAlignment = Alignment.Center
    ) {
        IconCompose(
            data = iconId,
            size = Dimen.fabIconSize,
        )
    }
}

/**
 * 角色图标
 */
@Composable
fun PvpIconItem(
    selectedIds: ArrayList<PvpCharacterData>,
    it: PvpCharacterData,
    floatWindow: Boolean,
    selectedEffect: Boolean = true
) {
    val tipSelectLimit = stringResource(id = R.string.tip_select_limit)
    val selected = selectedIds.contains(it)
    val newList = arrayListOf<PvpCharacterData>()
    selectedIds.forEach {
        newList.add(it)
    }
    val icon = if (it.unitId == 0) {
        R.drawable.unknown_gray
    } else {
        CharacterIdUtil.getMaxIconUrl(
            it.unitId,
            MainActivity.r6Ids.contains(it.unitId)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(Dimen.smallPadding)
            .fillMaxWidth()
            .aspectRatio(if (floatWindow) 0.618f else 1f)
    ) {
        //图标
        IconCompose(data = icon, wrapSize = floatWindow) {
            //点击选择或取消选择
            if (selected) {
                var cancelSelectIndex = 0
                newList.forEachIndexed { index, sel ->
                    if (it.position == sel.position) {
                        cancelSelectIndex = index
                    }
                }
                newList[cancelSelectIndex] = PvpCharacterData()
            } else {
                val unSelected = newList.find { it.position == 999 }
                if (unSelected == null) {
                    //选完了
                    ToastUtil.short(tipSelectLimit)
                } else {
                    //可以选择
                    newList[0] = it
                }
            }
            newList.sortByDescending { it.position }
            navViewModel.selectedPvpData.postValue(newList)
        }
        //位置
        val text =
            if (it != PvpCharacterData()) it.position.toString() else stringResource(id = R.string.unselect)
        SelectText(
            selected = selected && selectedEffect,
            text = text,
            padding = if (floatWindow) Dimen.divLineHeight else Dimen.smallPadding
        )
    }
}

/**
 * 查询结果页面
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun PvpSearchResult(
    selectedIds: List<PvpCharacterData>,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel = hiltViewModel()
) {
    val defIds = selectedIds.subList(0, 5).getIdStr()
    //展示搜索结果
    val idArray = JsonArray()
    for (sel in selectedIds.subList(0, 5)) {
        idArray.add(sel.unitId)
    }
    pvpViewModel.getPVPData(idArray)
    val result = pvpViewModel.pvpResult.observeAsState().value
    val placeholder = result == null
    val region = getRegion()
    pvpViewModel.getFavoritesList(defIds, region)
    //结果
    //收藏信息
    val favorites = pvpViewModel.favorites.observeAsState()
    val favoritesList = arrayListOf<String>()

    val context = LocalContext.current
    val vibrated = remember {
        mutableStateOf(false)
    }
    val mediuPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediuPadding

    if (favorites.value != null) {
        SideEffect {
            favorites.value?.let {
                it.forEach { data ->
                    favoritesList.add(data.atks)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (!placeholder) {
                if (result!!.message == "success") {
                    //振动提醒
                    if (!vibrated.value) {
                        vibrated.value = true
                        VibrateUtil(context).done()
                    }
                    val hasData = result.data!!.isNotEmpty()
                    if (hasData) {
                        //查询成功
                        val list = result.data!!.sortedByDescending { it.up }
                        FadeAnimation(visible = hasData) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                //展示查询结果
                                LazyColumn(
                                    contentPadding = PaddingValues(
                                        start = mediuPadding,
                                        end = mediuPadding
                                    )
                                ) {
                                    itemsIndexed(items = list) { index, item ->
                                        PvpResultItem(
                                            favoritesList,
                                            index + 1,
                                            item,
                                            region,
                                            floatWindow,
                                            pvpViewModel
                                        )
                                    }
                                    item {
                                        CommonSpacer()
                                    }
                                }
                            }
                        }
                    } else {
                        MainText(
                            text = stringResource(id = R.string.pvp_no_data),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = Dimen.largePadding)
                    ) {
                        MainText(
                            text = stringResource(id = R.string.data_get_error)
                        )
                        SubButton(
                            text = "重新查询",
                            modifier = Modifier.padding(top = Dimen.mediuPadding)
                        ) {
                            pvpViewModel.pvpResult.postValue(null)
                            pvpViewModel.getPVPData(idArray)
                        }
                    }

                }
            } else {
                //占位图
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    //展示查询结果
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = mediuPadding,
                            end = mediuPadding
                        )
                    ) {
                        items(10) {
                            PvpResultItem(
                                favoritesList,
                                0,
                                PvpResultData(),
                                region,
                                floatWindow,
                                pvpViewModel
                            )
                        }
                        item {
                            CommonSpacer()
                        }
                    }
                }
            }

        }
    }

}

/**
 * 查询结果 Item
 */
@ExperimentalMaterialApi
@Composable
private fun PvpResultItem(
    favoritesList: List<String>,
    i: Int,
    item: PvpResultData,
    region: Int,
    floatWindow: Boolean,
    viewModel: PvpViewModel
) {
    val placeholder = item.id == ""
    val scope = rememberCoroutineScope()
    val favorites = remember {
        mutableStateOf(favoritesList.contains(item.atk))
    }

    val mediuPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediuPadding

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(mediuPadding)
    ) {
        Row {
            MainTitleText(
                text = stringResource(id = R.string.team_no, i.toString().fillZero()),
                modifier = Modifier
                    .padding(bottom = mediuPadding)
                    .placeholder(visible = placeholder, highlight = PlaceholderHighlight.shimmer())
            )
            Spacer(modifier = Modifier.weight(1f))
            //收藏
            if (!placeholder) {
                IconCompose(
                    data = if (favorites.value) MainIconType.LOVE_FILL.icon else MainIconType.LOVE_LINE.icon,
                    size = Dimen.fabIconSize
                ) {
                    if (!placeholder) {
                        scope.launch {
                            if (favorites.value) {
                                //已收藏，取消收藏
                                viewModel.delete(item.atk, item.def, region)
                            } else {
                                //未收藏，添加收藏
                                val simpleDateFormat =
                                    SimpleDateFormat(
                                        "yyyy/MM/dd HH:mm:ss.SSS",
                                        Locale.CHINESE
                                    )
                                val date = Date(System.currentTimeMillis())
                                viewModel.insert(
                                    PvpFavoriteData(
                                        item.id,
                                        item.atk,
                                        item.def,
                                        simpleDateFormat.format(date),
                                        region
                                    )
                                )
                            }
                            favorites.value = !favorites.value
                        }
                    }
                }
            }
        }

        MainCard(
            modifier = Modifier.placeholder(
                visible = placeholder,
                highlight = PlaceholderHighlight.shimmer()
            )
        ) {
            val upRatio = if (item.up == 0) 0 else {
                round(item.up * 1.0 / (item.up + item.down) * 100).toInt()
            }
            Column(
                modifier = Modifier.padding(
                    start = mediuPadding,
                    end = mediuPadding
                )
            ) {
                //点赞信息
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainContentText(
                        text = "${upRatio}%",
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(0.3f)
                    )
                    MainContentText(
                        text = item.up.toString(),
                        color = colorResource(id = R.color.color_rank_21),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(0.3f)
                    )
                    MainContentText(
                        text = item.down.toString(),
                        color = colorResource(id = R.color.color_rank_18_20),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                }
                //队伍角色图标
                //进攻
                Row(
                    modifier = Modifier
                        .padding(bottom = mediuPadding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val modifier = if (!floatWindow) {
                        Modifier
                            .weight(1f)
                            .padding(Dimen.smallPadding)
                    } else {
                        Modifier
                            .weight(1f)
                            .padding(
                                start = Dimen.smallPadding,
                                end = Dimen.smallPadding,
                            )
                    }
                    item.getIdList(0).forEachIndexed { _, it ->
                        Box(
                            modifier = modifier,
                            contentAlignment = Alignment.Center
                        ) {
                            IconCompose(
                                data = CharacterIdUtil.getMaxIconUrl(
                                    it,
                                    MainActivity.r6Ids.contains(it)
                                )
                            )
                        }
                    }
                }
            }
        }

    }
}

/**
 * 已收藏数据
 *
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun PvpFavorites(
    scrollState: LazyListState,
    toCharacter: (Int) -> Unit,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel
) {
    val region = getRegion()
    pvpViewModel.getAllFavorites(region)
    val list = pvpViewModel.allFavorites.observeAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (list.value != null && list.value!!.isNotEmpty()) {
            LazyColumn(state = scrollState, contentPadding = PaddingValues(Dimen.mediuPadding)) {
                items(list.value!!) { data ->
                    PvpFavoriteItem(
                        toCharacter,
                        region,
                        data,
                        floatWindow,
                        pvpViewModel
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        } else {
            MainText(
                text = stringResource(id = R.string.pvp_no_favorites),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun PvpFavoriteItem(
    toCharacter: (Int) -> Unit,
    region: Int,
    itemData: PvpFavoriteData,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel,
    characterViewModel: CharacterViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val mediuPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediuPadding

    Row(
        modifier = Modifier
            .padding(start = mediuPadding, end = mediuPadding)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //搜索
        TextButton(onClick = {
            //重置页面
            scope.launch {
                pvpViewModel.pvpResult.postValue(null)
                val selectedData =
                    characterViewModel.getPvpCharacterByIds(itemData.defs.intArrayList)
                val selectedIds = selectedData as ArrayList<PvpCharacterData>?
                selectedIds?.sortByDescending { it.position }
                navViewModel.selectedPvpData.postValue(selectedIds)
                navViewModel.showResult.postValue(true)
            }
            VibrateUtil(context).single()
        }) {
            IconCompose(
                data = MainIconType.PVP_SEARCH.icon,
                size = Dimen.fabIconSize
            )
            MainContentText(text = stringResource(id = R.string.pvp_research))
        }
        Spacer(modifier = Modifier.weight(1f))
        //取消收藏
        IconCompose(
            data = MainIconType.LOVE_FILL.icon,
            Dimen.fabIconSize
        ) {
            //点击取消收藏
            scope.launch {
                pvpViewModel.delete(itemData.atks, itemData.defs, region)
            }
        }
    }

    MainCard(modifier = Modifier.padding((mediuPadding))) {
        //队伍角色图标
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = mediuPadding, bottom = mediuPadding)
        ) {
            //进攻
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val modifier = if (!floatWindow) {
                    Modifier
                        .weight(1f)
                        .padding(Dimen.smallPadding)
                } else {
                    Modifier
                        .weight(1f)
                        .padding(
                            start = Dimen.smallPadding,
                            end = Dimen.smallPadding,
                        )
                }
                itemData.getAtkIds().forEachIndexed { _, it ->
                    Box(
                        modifier = modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        IconCompose(
                            data = CharacterIdUtil.getMaxIconUrl(
                                it,
                                MainActivity.r6Ids.contains(it)
                            )
                        ) {
                            toCharacter(it)
                        }
                    }
                }
            }
            //防守
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val modifier = if (!floatWindow) {
                    Modifier
                        .weight(1f)
                        .padding(Dimen.smallPadding)
                } else {
                    Modifier
                        .weight(1f)
                        .padding(start = Dimen.smallPadding, end = Dimen.smallPadding)
                }
                itemData.getDefIds().forEachIndexed { _, it ->
                    Box(
                        modifier = modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        IconCompose(
                            data = CharacterIdUtil.getMaxIconUrl(
                                it,
                                MainActivity.r6Ids.contains(it)
                            )
                        ) {
                            toCharacter(it)
                        }
                    }
                }
            }
        }
    }

}