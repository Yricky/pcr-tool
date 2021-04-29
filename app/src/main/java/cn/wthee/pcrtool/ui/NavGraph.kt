package cn.wthee.pcrtool.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.character.CharacterBasicInfo
import cn.wthee.pcrtool.ui.character.CharacterInfo
import cn.wthee.pcrtool.ui.character.RankCompare
import cn.wthee.pcrtool.ui.character.RankEquipList
import cn.wthee.pcrtool.ui.equip.EquipList
import cn.wthee.pcrtool.ui.equip.EquipMainInfo
import cn.wthee.pcrtool.ui.home.CharacterList
import cn.wthee.pcrtool.ui.tool.LeaderboardList
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

object Navigation {
    const val CHARACTER_LIST = "characterList"
    const val CHARACTER_DETAIL = "characterDetail"
    const val CHARACTER_BASIC_INFO = "characterBasicInfo"
    const val UNIT_ID = "unitId"
    const val UNIT_SIX_ID = "r6Id"
    const val EQUIP_LIST = "equipList"
    const val EQUIP_ID = "equipId"
    const val EQUIP_DETAIL = "equipDetail"
    const val RANK_EQUIP = "rankEquip"
    const val RANK_COMPARE = "rankCompare"
    const val MAX_RANK = "maxRank"
    const val SELECT_DATA = "selectData"
    const val TOOL_LEADER = "tool_leader"
}

@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun NavGraph(navController: NavHostController, viewModel: NavViewModel, actions: NavActions) {

    NavHost(navController, startDestination = Navigation.CHARACTER_LIST) {
        //首页
        composable(Navigation.CHARACTER_LIST) {
            CharacterList(
                actions.toCharacterDetail,
                viewModel
            )
        }

        //角色属性详情
        composable(
            "${Navigation.CHARACTER_DETAIL}/{${Navigation.UNIT_ID}}/{${Navigation.UNIT_SIX_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }, navArgument(Navigation.UNIT_SIX_ID) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            viewModel.pageLevel.postValue(1)
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            CharacterInfo(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                r6Id = arguments.getInt(Navigation.UNIT_SIX_ID),
                actions.toEquipDetail,
                actions.toCharacterBasicInfo,
                actions.toCharacteRankEquip,
                actions.toCharacteRankCompare,
                viewModel
            )
        }

        //角色资料
        composable(
            "${Navigation.CHARACTER_BASIC_INFO}/{${Navigation.UNIT_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            viewModel.pageLevel.postValue(2)
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            CharacterBasicInfo(
                unitId = arguments.getInt(Navigation.UNIT_ID)
            )
        }

        //装备列表
        composable(Navigation.EQUIP_LIST) {
            viewModel.pageLevel.postValue(1)
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            EquipList(viewModel, toEquipDetail = actions.toEquipDetail)
        }

        //装备详情
        composable(
            "${Navigation.EQUIP_DETAIL}/{${Navigation.EQUIP_ID}}",
            arguments = listOf(navArgument(Navigation.EQUIP_ID) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            viewModel.pageLevel.postValue(2)
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            EquipMainInfo(arguments.getInt(Navigation.EQUIP_ID))
        }

        //角色 RANK 装备
        composable(
            "${Navigation.RANK_EQUIP}/{${Navigation.UNIT_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            viewModel.pageLevel.postValue(2)
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            RankEquipList(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                toEquipDetail = actions.toEquipDetail,
                navViewModel = viewModel
            )
        }

        //角色 RANK 对比
        composable(
            "${Navigation.RANK_COMPARE}/{${Navigation.UNIT_ID}}/{${Navigation.MAX_RANK}}/{${Navigation.SELECT_DATA}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }, navArgument(Navigation.MAX_RANK) {
                type = NavType.IntType
            }, navArgument(Navigation.SELECT_DATA) {
                type = NavType.StringType
            })
        ) {
            val arguments = requireNotNull(it.arguments)
            viewModel.pageLevel.postValue(2)
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            RankCompare(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                maxRank = arguments.getInt(Navigation.MAX_RANK),
                selectedInfo = arguments.getString(Navigation.SELECT_DATA) ?: "",
                navViewModel = viewModel
            )
        }

        //角色排行
        composable(Navigation.TOOL_LEADER) {
            viewModel.pageLevel.postValue(1)
            viewModel.fabMainIcon.postValue(R.drawable.ic_back)
            LeaderboardList()
        }
    }
}

/**
 * 导航
 */
class NavActions(navController: NavHostController) {
    /**
     * 角色详情
     */
    val toCharacterDetail: (Int, Int) -> Unit = { unitId: Int, r6Id: Int ->
        navController.navigate("${Navigation.CHARACTER_DETAIL}/${unitId}/${r6Id}")
    }

    /**
     * 装备列表
     */
    val toEquipList: () -> Unit = {
        navController.navigate(Navigation.EQUIP_LIST)
    }

    /**
     * 装备详情
     */
    val toEquipDetail: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${Navigation.EQUIP_DETAIL}/${equipId}")
    }

    /**
     * 角色资料
     */
    val toCharacterBasicInfo: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${Navigation.CHARACTER_BASIC_INFO}/${unitId}")
    }

    /**
     * 角色 RANK 装备
     */
    val toCharacteRankEquip: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${Navigation.RANK_EQUIP}/${unitId}")
    }

    /**
     * 角色 RANK 对比
     */
    val toCharacteRankCompare: (Int, Int, String) -> Unit =
        { unitId: Int, maxRank: Int, select: String ->
            navController.navigate("${Navigation.RANK_COMPARE}/${unitId}/${maxRank}/${select}")
        }

    /**
     * 角色排行
     */
    val toLeaderboard = {
        navController.navigate("${Navigation.TOOL_LEADER}")
    }
}

@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {
    /**
     * 页面等级
     */
    val pageLevel = MutableLiveData(0)

    /**
     * fab 图标显示
     */
    val fabMainIcon = MutableLiveData(R.drawable.ic_function)

    /**
     * 确认
     */
    val fabOK = MutableLiveData(false)

    /**
     * 关闭
     */
    val fabClose = MutableLiveData(false)

    /**
     * 选择的 RANK
     */
    val selectRank = MutableLiveData(2)


    /**
     * 下载状态
     * -2: 隐藏
     * -1: 显示加载中
     * >0: 进度
     */
    val downloadProgress = MutableLiveData(-2)

    fun goback(navController: NavHostController?) {
        val currentPageLevel = pageLevel.value ?: 0
        if (currentPageLevel > 0) {
            pageLevel.postValue(currentPageLevel - 1)
            navController?.navigateUp()
        } else {
            //打开或关闭菜单
            val menuState = if (currentPageLevel == 0) -1 else 0
            pageLevel.postValue(menuState)
        }
    }
}
