package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.db.view.Attr

/**
 * 角色 Rank 对比数据
 */
data class RankCompareData(
    val title: String = "???",
    val attr0: Double = 0.0,
    val attr1: Double = 0.0,
    val attrCompare: Double = 0.0
)


/**
 * 角色属性 [Attr] ，转 [RankCompareData] 角色 Rank 对比列表
 */
fun getRankCompareList(attr0: Attr, attr1: Attr): List<RankCompareData> {
    val datas = arrayListOf<RankCompareData>()
    val list0 = attr0.all()
    val list1 = attr1.all()
    val list2 = attr1.compare(attr0)
    list0.forEachIndexed { index, attrValue ->
        datas.add(
            RankCompareData(
                attrValue.title,
                attrValue.value,
                list1[index].value,
                list2[index].value
            )
        )
    }
    return datas
}
