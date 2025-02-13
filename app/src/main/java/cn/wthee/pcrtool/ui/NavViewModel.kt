package cn.wthee.pcrtool.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.FilterEquipment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * 导航 ViewModel
 */
@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {

    /**
     * fab 图标显示
     */
    val fabMainIcon = MutableLiveData(MainIconType.MAIN)

    /**
     * 确认
     */
    val fabOKCilck = MutableLiveData(false)

    /**
     * 关闭
     */
    val fabCloseClick = MutableLiveData(false)

    /**
     * 下载状态
     * -2: 隐藏
     * -1: 显示加载中
     * >0: 进度
     */
    val downloadProgress = MutableLiveData(-1)

    /**
     * 加载中
     */
    val loading = MutableLiveData(false)

    /**
     * 已六星的角色ID
     */
    val r6Ids = MutableLiveData(listOf<Int>())

    /**
     * 重置
     */
    val resetClick = MutableLiveData(false)

    /**
     * 角色筛选
     */
    var filterCharacter = MutableLiveData(FilterCharacter())

    /**
     * 装备筛选
     */
    var filterEquip = MutableLiveData(FilterEquipment())

    /**
     * 竞技场查询角色
     */
    val selectedPvpData = MutableLiveData(
        arrayListOf(
            PvpCharacterData(),
            PvpCharacterData(),
            PvpCharacterData(),
            PvpCharacterData(),
            PvpCharacterData()
        )
    )

    /**
     * rank 选择，当前
     */
    var curRank = MutableLiveData(0)

    /**
     * rank 选择，目标
     */
    var targetRank = MutableLiveData(0)

    /**
     * rank 选择，当前
     */
    var curRank1 = MutableLiveData(0)

    /**
     * rank 选择，目标
     */
    var targetRank1 = MutableLiveData(0)

    /**
     * 悬浮服务
     */
    val floatServiceRun = MutableLiveData(true)

    /**
     * 悬浮闯最小化
     */
    val floatSearchMin = MutableLiveData(false)

    /**
     * pvp 查询结果显示
     */
    val showResult = MutableLiveData(false)

    /**
     * 数据切换弹窗显示
     */
    val openChangeDataDialog = MutableLiveData(false)

    /**
     * 当前选择属性
     */
    val currentValue = MutableLiveData<CharacterProperty>()

    /**
     * 模拟卡池结果显示
     */
    val showMockGachaResult = MutableLiveData(false)

    /**
     * 模拟卡池数据
     */
    val gachaId = MutableLiveData<String>()

    /**
     * 模拟卡池类型
     * 0：自选角色 1：fes角色
     */
    val gachaType = MutableLiveData<Int>()

    /**
     * 模拟卡池 pickUp 角色
     */
    val pickUpList = MutableLiveData<List<GachaUnitInfo>>()
}
