package cn.wthee.pcrtool.ui.character

import android.Manifest
import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.noShape
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max

/**
 * 角色信息
 *
 * @param unitId 角色编号
 */
@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun CharacterDetail(
    scrollState: ScrollState,
    unitId: Int,
    actions: NavActions,
    navViewModel: NavViewModel,
    attrViewModel: CharacterAttrViewModel = hiltViewModel(),
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    //最大值
    val maxValue = attrViewModel.getMaxRankAndRarity(unitId)
        .collectAsState(initial = CharacterProperty()).value
    val currentValueState = attrViewModel.currentValue.observeAsState()
    //选择的 RANK
    val selectRank = navViewModel.selectRank.observeAsState().value ?: 0
    //数值信息
    if (currentValueState.value == null && maxValue.isInit()) {
        attrViewModel.currentValue.postValue(maxValue)
        if (selectRank == 0) {
            navViewModel.selectRank.postValue(maxValue.rank)
        }
    }
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    if (!state.isVisible && !state.isAnimationRunning) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabCloseClick.postValue(false)
    }
    //关闭监听
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    //收藏状态
    val filter = navViewModel.filterCharacter.observeAsState()
    val loved = remember {
        mutableStateOf(filter.value?.starIds?.contains(unitId) ?: false)
    }
    //技能循环

    val loopData =
        skillViewModel.getCharacterSkillLoops(unitId).collectAsState(initial = arrayListOf()).value
    val iconTypes = skillViewModel.iconTypes.observeAsState().value ?: hashMapOf()
    //角色属性
    val allData = attrViewModel.getCharacterInfo(unitId, currentValueState.value)
        .collectAsState(initial = AllAttrData()).value


    //加载数据并显示
    currentValueState.value?.let { currentValue ->
        val unknown = maxValue.level == -1
        //角色等级滑动条
        val sliderLevel = remember {
            mutableStateOf(currentValue.level)
        }
        //专武等级滑动条
        val sliderUniqueEquipLevel = remember {
            mutableStateOf(currentValue.uniqueEquipmentLevel)
        }
        //Rank 选择
        if (selectRank != 0 && selectRank != currentValue.rank) {
            attrViewModel.currentValue.postValue(currentValue.update(rank = selectRank))
        }

        //页面
        ModalBottomSheetLayout(
            sheetState = state,
            scrimColor = colorResource(id = if (MaterialTheme.colors.isLight) R.color.alpha_white else R.color.alpha_black),
            sheetElevation = Dimen.sheetElevation,
            sheetShape = if (state.offset.value == 0f) {
                noShape
            } else {
                MaterialTheme.shapes.large
            },
            sheetContent = {
                SkillLoopList(
                    loopData, iconTypes, Modifier.padding(
                        top = Dimen.largePadding,
                        start = Dimen.mediuPadding,
                        end = Dimen.mediuPadding,
                    )
                )
            }
        ) {
            //关闭
            if (close) {
                coroutineScope.launch {
                    state.hide()
                }
                navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                navViewModel.fabCloseClick.postValue(false)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //角色卡面
                    FadeAnimation(visible = maxValue.isInit() || unknown) {
                        CardImage(unitId)
                    }

                    //数据加载后，展示页面
                    val visible = allData.sumAttr.hp > 1 && allData.equips.isNotEmpty()
                    SlideAnimation(visible = visible) {
                        if (visible) {
                            //页面
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colors.background),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                //星级
                                StarSelect(
                                    currentValue = currentValue,
                                    max = maxValue.rarity,
                                    modifier = Modifier.padding(top = Dimen.mediuPadding),
                                    attrViewModel = attrViewModel
                                )
                                AttrLists(
                                    currentValue,
                                    sliderLevel,
                                    maxValue,
                                    allData
                                )
                                //RANK 装备
                                CharacterEquip(
                                    unitId = unitId,
                                    rank = currentValue.rank,
                                    maxRank = maxValue.rank,
                                    equips = allData.equips,
                                    toEquipDetail = actions.toEquipDetail,
                                    toRankEquip = actions.toCharacteRankEquip,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                //显示专武
                                if (allData.uniqueEquip.equipmentId != Constants.UNKNOWN_EQUIP_ID) {
                                    UniqueEquip(
                                        currentValue = currentValue,
                                        uniqueEquipLevelMax = maxValue.uniqueEquipmentLevel,
                                        sliderState = sliderUniqueEquipLevel,
                                        uniqueEquipmentMaxData = allData.uniqueEquip
                                    )
                                }
                                //技能
                                CharacterSkill(
                                    unitId = unitId,
                                    level = currentValue.level,
                                    atk = max(
                                        allData.sumAttr.atk.int,
                                        allData.sumAttr.magicStr.int
                                    )
                                )
                            }
                        }
                    }
                    SlideAnimation(visible = unknown) {
                        //未知角色占位页面
                        Text(
                            text = stringResource(R.string.unknown_character),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(Dimen.largePadding)
                        )
                    }
                }
                //悬浮按钮
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.padding(
                            bottom = if (unknown) Dimen.fabMargin else Dimen.fabSmallMarginEnd,
                            end = Dimen.fabMargin
                        )
                    ) {
                        //收藏
                        FabCompose(
                            iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
                            modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd),
                            defaultPadding = unknown
                        ) {
                            filter.value?.addOrRemove(unitId)
                            loved.value = !loved.value
                        }
                        //跳转至角色资料
                        FabCompose(
                            iconType = MainIconType.CHARACTER_INTRO,
                            modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd),
                            defaultPadding = unknown
                        ) {
                            actions.toCharacterBasicInfo(unitId)
                        }
                        //技能循环
                        FabCompose(
                            iconType = MainIconType.SKILL_LOOP,
                            defaultPadding = unknown,
                            modifier = Modifier.alpha(if (unknown) 0f else 1f)
                        ) {
                            coroutineScope.launch {
                                if (state.isVisible) {
                                    navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                                    state.hide()
                                } else {
                                    navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                                    state.show()
                                }
                            }
                        }
                    }
                    if (!unknown) {
                        Row(
                            modifier = Modifier
                                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                        ) {
                            //跳转至 RANK 对比页面
                            FabCompose(
                                iconType = MainIconType.RANK_COMPARE,
                                text = stringResource(id = R.string.compare),
                                modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                            ) {
                                actions.toCharacteRankCompare(
                                    unitId,
                                    maxValue.rank,
                                    currentValue.level,
                                    currentValue.rarity,
                                    currentValue.uniqueEquipmentLevel,
                                )
                            }
                            //跳转至装备统计页面
                            FabCompose(
                                iconType = MainIconType.EQUIP_CALC,
                                text = stringResource(id = R.string.count),
                            ) {
                                actions.toCharacteEquipCount(unitId, maxValue.rank)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 属性
 */
@ExperimentalAnimationApi
@Composable
private fun AttrLists(
    currentValue: CharacterProperty,
    sliderLevel: MutableState<Int>,
    maxValue: CharacterProperty,
    allData: AllAttrData,
    attrViewModel: CharacterAttrViewModel = hiltViewModel()
) {
    //等级
    Text(
        text = sliderLevel.value.toString(),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.h6
    )
    Slider(
        value = sliderLevel.value.toFloat(),
        onValueChange = { sliderLevel.value = it.toInt() },
        onValueChangeFinished = {
            if (sliderLevel.value != 0) {
                attrViewModel.currentValue.postValue(currentValue.update(level = sliderLevel.value))
            }
        },
        valueRange = 1f..maxValue.level.toFloat(),
        modifier = Modifier
            .fillMaxWidth(0.618f)
    )
    //属性
    AttrList(attrs = allData.sumAttr.all())
    //剧情属性
    MainText(
        text = stringResource(id = R.string.title_story_attr),
        modifier = Modifier.Companion
            .padding(
                top = Dimen.largePadding,
                bottom = Dimen.smallPadding
            )
    )
    AttrList(attrs = allData.stroyAttr.allNotZero())
    //Rank 奖励
    val hasBonus = allData.bonus.attr.allNotZero().isNotEmpty()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (hasBonus) {
            MainText(
                text = stringResource(id = R.string.title_rank_bonus),
                modifier = Modifier.Companion
                    .padding(
                        top = Dimen.largePadding,
                        bottom = Dimen.smallPadding
                    ),
                textAlign = TextAlign.Center
            )
            AttrList(attrs = allData.bonus.attr.allNotZero())
        }
    }
}

/**
 * 角色卡面图片
 */
@ExperimentalCoilApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
private fun CardImage(unitId: Int) {
    val context = LocalContext.current
    val picUrls = CharacterIdUtil.getAllPicUrl(unitId, MainActivity.r6Ids.contains(unitId))
    val loaded = arrayListOf<Boolean>()
    val drawables = arrayListOf<Drawable?>()
    picUrls.forEach { _ ->
        loaded.add(false)
        drawables.add(null)
    }
    val pagerState = rememberPagerState(
        pageCount = picUrls.size,
        infiniteLoop = true,
        initialOffscreenLimit = picUrls.size - 1
    )
    val coroutineScope = rememberCoroutineScope()

    //权限
    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val unLoadToast = stringResource(id = R.string.wait_pic_load)

    Box {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { index ->
            val request = ImageRequest.Builder(context)
                .data(picUrls[index])
                .build()
            coroutineScope.launch {
                val image = Coil.imageLoader(context).execute(request).drawable
                drawables[index] = image
            }
            val infiniteLoopIndex =
                if (index == pagerState.pageCount - 1 && pagerState.currentPage == 0) {
                    //从首个滚动到最后一个
                    pagerState.currentPage - 1
                } else if (index == 0 && pagerState.currentPage == pagerState.pageCount - 1) {
                    //从最后一个滚动的首个
                    pagerState.pageCount
                } else {
                    index
                }
            Card(
                modifier = Modifier
                    .padding(top = Dimen.largePadding, bottom = Dimen.largePadding)
                    .fillMaxWidth(0.8f)
                    .graphicsLayer {
                        val pageOffset =
                            calculateCurrentOffsetForPage(infiniteLoopIndex).absoluteValue
                        lerp(
                            start = ScaleFactor(0.9f, 0.9f),
                            stop = ScaleFactor(1f, 1f),
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale.scaleY
                            scaleY = scale.scaleY
                        }
                    },
                onClick = {
                    VibrateUtil(context).single()
                    //下载
                    if (index == pagerState.currentPage) {
                        if (loaded[index]) {
                            //权限校验
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !hasPermissions(
                                    context,
                                    permissions
                                )
                            ) {
                                ActivityCompat.requestPermissions(
                                    context as Activity,
                                    permissions,
                                    1
                                )
                            } else {
                                drawables[index]?.let {
                                    ImageDownloadHelper(context).saveBitmap(
                                        bitmap = (it as BitmapDrawable).bitmap,
                                        displayName = "${unitId}_${index}.jpg"
                                    )
                                    VibrateUtil(context).done()
                                }
                            }
                        } else {
                            ToastUtil.short(unLoadToast)
                        }
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(infiniteLoopIndex)
                        }
                    }
                },
                shape = MaterialTheme.shapes.large,
            ) {
                //图片
                CharacterCardImage(url = picUrls[index]) {
                    loaded[index] = true
                }
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.BottomCenter),
            activeColor = MaterialTheme.colors.primary
        )
    }

}

/**
 * 角色 RANK 装备
 * @param unitId 角色编号
 * @param rank 当前rank
 * @param equips 装备列表
 */
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
private fun CharacterEquip(
    unitId: Int,
    rank: Int,
    maxRank: Int,
    equips: List<EquipmentMaxData>,
    toEquipDetail: (Int) -> Unit,
    toRankEquip: (Int) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth(0.8f)) {
        //装备 6、 3
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.largePadding)
        ) {
            val id6 = equips[0].equipmentId
            val id3 = equips[1].equipmentId
            IconCompose(data = getEquipIconUrl(id6)) {
                if (id6 != Constants.UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id6)
                }
            }
            IconCompose(data = getEquipIconUrl(id3)) {
                toEquipDetail(id3)
            }
        }
        //装备 5、 2
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.mediuPadding)
        ) {
            val id5 = equips[2].equipmentId
            IconCompose(data = getEquipIconUrl(id5)) {
                toEquipDetail(id5)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = MainIconType.BACK.icon,
                    contentDescription = null,
                    tint = if (rank < maxRank) {
                        getRankColor(rank = rank + 1)
                    } else {
                        MaterialTheme.colors.background
                    },
                    modifier = Modifier
                        .size(Dimen.starIconSize)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(enabled = rank < maxRank) {
                            VibrateUtil(context).single()
                            navViewModel.selectRank.postValue(rank + 1)
                        }
                )
                //跳转至所有 RANK 装备列表
                SubButton(
                    text = getFormatText(rank),
                    color = getRankColor(rank),
                    modifier = Modifier.padding(
                        top = Dimen.largePadding * 2,
                        bottom = Dimen.largePadding * 2,
                    )
                ) {
                    toRankEquip(unitId)
                }
                Icon(
                    imageVector = MainIconType.MORE.icon,
                    contentDescription = null,
                    tint = if (rank > 1) {
                        getRankColor(rank = rank - 1)
                    } else {
                        MaterialTheme.colors.background
                    },
                    modifier = Modifier
                        .size(Dimen.starIconSize)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(enabled = rank > 1) {
                            VibrateUtil(context).single()
                            navViewModel.selectRank.postValue(rank - 1)
                        }
                )
            }
            val id2 = equips[3].equipmentId
            IconCompose(data = getEquipIconUrl(id2)) {
                toEquipDetail(id2)
            }

        }
        //装备 4、 1
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.mediuPadding)
        ) {
            val id4 = equips[4].equipmentId
            val id1 = equips[5].equipmentId
            IconCompose(data = getEquipIconUrl(id4)) {
                toEquipDetail(id4)
            }
            IconCompose(data = getEquipIconUrl(id1)) {
                toEquipDetail(id1)
            }
        }
    }
}

/**
 * 专武信息
 * @param uniqueEquipLevelMax 最大等级
 * @param sliderState 等级滑动条状态
 * @param uniqueEquipmentMaxData 专武数值信息
 */
@ExperimentalCoilApi
@Composable
private fun UniqueEquip(
    currentValue: CharacterProperty,
    uniqueEquipLevelMax: Int,
    sliderState: MutableState<Int>,
    uniqueEquipmentMaxData: UniqueEquipmentMaxData?,
) {
    val attrViewModel: CharacterAttrViewModel = hiltViewModel()
    uniqueEquipmentMaxData?.let {
        Column(
            modifier = Modifier.padding(top = Dimen.largePadding)
        ) {
            //名称
            MainText(
                text = it.equipmentName,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                selectable = true
            )
            //专武等级
            Subtitle1(
                text = sliderState.value.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colors.primary
            )
            //专武等级选择
            Slider(
                value = sliderState.value.toFloat(),
                onValueChange = { sliderState.value = it.toInt() },
                onValueChangeFinished = {
                    attrViewModel.currentValue.postValue(currentValue.update(uniqueEquipmentLevel = sliderState.value))
                },
                valueRange = 0f..uniqueEquipLevelMax.toFloat(),
                modifier = Modifier
                    .fillMaxWidth(0.618f)
                    .height(Dimen.slideHeight)
                    .align(Alignment.CenterHorizontally)
            )
            //图标描述
            Row(
                modifier = Modifier
                    .padding(Dimen.largePadding)
                    .fillMaxWidth()
            ) {
                IconCompose(getEquipIconUrl(it.equipmentId))
                Subtitle2(
                    text = it.getDesc(),
                    modifier = Modifier.padding(start = Dimen.mediuPadding),
                    selectable = true
                )
            }
            //属性
            AttrList(attrs = it.attr.allNotZero())
        }
    }

}

/**
 * 星级选择
 * @param max 最大值
 */
@Composable
private fun StarSelect(
    currentValue: CharacterProperty,
    max: Int,
    modifier: Modifier = Modifier,
    attrViewModel: CharacterAttrViewModel
) {
    val context = LocalContext.current

    Row(modifier) {
        for (i in 1..max) {
            val iconId = when {
                i > currentValue.rarity -> R.drawable.ic_star_dark
                i == 6 -> R.drawable.ic_star_pink
                else -> R.drawable.ic_star
            }
            Image(
                painter = rememberImagePainter(data = iconId),
                contentDescription = null,
                modifier = Modifier
                    .padding(Dimen.divLineHeight)
                    .size(Dimen.starIconSize)
                    .clip(CircleShape)
                    .clickable {
                        VibrateUtil(context).single()
                        attrViewModel.currentValue.postValue(currentValue.update(rarity = i))
                    }
                    .padding(Dimen.smallPadding)
            )
        }
    }
}