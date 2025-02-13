package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.model.UnitsInGacha
import cn.wthee.pcrtool.data.model.getIds
import kotlin.random.Random

/**
 * 模拟抽卡
 * fixme 调整权重
 */
class MockGachaHelper(
    pickUpType: Int,
    pickUpList: List<GachaUnitInfo>,
    unitListData: UnitsInGacha
) {
    private var gachaBoxList: MutableList<GachaWeightInfo> = ArrayList()
    private var tenthGachaBoxList: MutableList<GachaWeightInfo> = ArrayList()
    private val totalWeight = 1000

    //默认三星 up 总概率 3%
    private val star3Weight = 3 * totalWeight

    //默认 up 概率 0.7%
    private val pickUpWeight = (0.7 * totalWeight).toInt()

    init {
        //初始权重
        when (pickUpType) {
            //自选
            0 -> {
                //基本
                gachaBoxList.add(GachaWeightInfo(unitListData.normal1, 79 * totalWeight))
                gachaBoxList.add(GachaWeightInfo(unitListData.normal2, 18 * totalWeight))
                gachaBoxList.add(GachaWeightInfo(unitListData.normal3, star3Weight - pickUpWeight))
                gachaBoxList.add(GachaWeightInfo(pickUpList, pickUpWeight))
                //第十发
                tenthGachaBoxList.add(GachaWeightInfo(unitListData.normal2, 97 * totalWeight))
                tenthGachaBoxList.add(
                    GachaWeightInfo(
                        unitListData.normal3,
                        star3Weight - pickUpWeight
                    )
                )
                tenthGachaBoxList.add(GachaWeightInfo(pickUpList, pickUpWeight))
            }
            //Fes 三星概率翻倍
            else -> {
                //基本
                gachaBoxList.add(GachaWeightInfo(unitListData.normal1, 77 * totalWeight))
                gachaBoxList.add(GachaWeightInfo(unitListData.normal2, 17 * totalWeight))
                gachaBoxList.add(
                    GachaWeightInfo(
                        unitListData.normal3,
                        (star3Weight - pickUpWeight) * 2
                    )
                )
                val notUpFesList = arrayListOf<GachaUnitInfo>()
                val pickUpIds = pickUpList.getIds()
                unitListData.fesLimit.forEach {
                    if (!pickUpIds.contains(it.unitId)) {
                        notUpFesList.add(it)
                    }
                }
                gachaBoxList.add(GachaWeightInfo(notUpFesList, pickUpWeight))
                gachaBoxList.add(GachaWeightInfo(pickUpList, pickUpWeight))
                //第十发
                tenthGachaBoxList.add(GachaWeightInfo(unitListData.normal2, 94 * totalWeight))
                tenthGachaBoxList.add(
                    GachaWeightInfo(
                        unitListData.normal3,
                        (star3Weight - pickUpWeight) * 2
                    )
                )
                tenthGachaBoxList.add(GachaWeightInfo(notUpFesList, pickUpWeight))
                tenthGachaBoxList.add(GachaWeightInfo(pickUpList, pickUpWeight))
            }
        }
    }

    fun giveMe1500Gems(): ArrayList<GachaUnitInfo> {
        val resultList = arrayListOf<GachaUnitInfo>()
        for (i in 0 until 9) {
            resultList.add(getSingleResult(false))
        }
        //第十次
        resultList.add(getSingleResult(true))

        return resultList
    }

    /**
     * 随机返回
     */
    private fun getSingleResult(isTenth: Boolean): GachaUnitInfo {
        val boxList = if (isTenth) {
            //排除一星角色
            tenthGachaBoxList
        } else {
            gachaBoxList
        }

        var weightSum = 0
        for (wc in boxList) {
            weightSum += wc.weight
        }
        val randomNum = Random.nextInt(weightSum)
        var m = 0
        for (wc in boxList) {
            if (m <= randomNum && randomNum < m + wc.weight) {
                return wc.unitBox.random()
            }
            m += wc.weight
        }
        return getSingleResult(isTenth)
    }

    data class GachaWeightInfo(var unitBox: List<GachaUnitInfo>, var weight: Int)

}