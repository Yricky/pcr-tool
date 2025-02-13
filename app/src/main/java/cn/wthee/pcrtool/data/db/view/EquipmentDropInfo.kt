package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
import java.io.Serializable


/**
 * 最终掉落信息视图
 */
data class EquipmentDropInfo(
    @ColumnInfo(name = "equip_id") val equipId: Int,
    @ColumnInfo(name = "quest_id") val questId: Int,
    @ColumnInfo(name = "quest_name") val questName: String,
    @ColumnInfo(name = "rewards") val rewards: String,
    @ColumnInfo(name = "odds") val odds: String
) {
    fun getNum() = questName.split(" ")[1]

    fun getOddOfEquip(eid: String): String {
        val list1 = rewards.split('-')
        val list2 = odds.split('-')
        return list2[list1.indexOf(eid)]
    }

    fun getAllOdd(): List<EquipmentIdWithOdd> {
        val list1 = rewards.split('-') as MutableList
        val list2 = odds.split('-') as MutableList
        val result = arrayListOf<EquipmentIdWithOdd>()
        list1.forEachIndexed { index, s ->
            if (s != "0") {
                result.add(
                    EquipmentIdWithOdd(
                        s.toInt(),
                        list2[index].toInt()
                    )
                )
            }
        }
        return result.sortedWith(equipCompare())
    }
}

/**
 * 排序
 */
fun equipCompare() = Comparator<EquipmentIdWithOdd> { o1, o2 ->
    if (o1.odd > o2.odd) {
        -1
    } else if (o1.odd < o2.odd) {
        1
    } else {
        if (o1.eid / 100 % 100 < o2.eid / 100 % 100) 1 else -1
    }
}


data class EquipmentIdWithOdd(
    val eid: Int = UNKNOWN_EQUIP_ID,
    val odd: Int = 0
)

/**
 * 装备合成信息
 */
data class EquipmentMaterial(
    var id: Int = UNKNOWN_EQUIP_ID,
    var name: String = "???",
    var count: Int = 0
) : Serializable

