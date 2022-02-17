package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.db.view.GachaUnitInfo

data class UnitsInGacha(
    val normal1: List<GachaUnitInfo>,
    val normal2: List<GachaUnitInfo>,
    val normal3: List<GachaUnitInfo>,
    val limit: List<GachaUnitInfo>,
    val fesLimit: List<GachaUnitInfo>,
)

fun List<GachaUnitInfo>.getIds(): ArrayList<Int> {
    val ids = arrayListOf<Int>()
    this.forEach {
        ids.add(it.unitId)
    }
    return ids
}
