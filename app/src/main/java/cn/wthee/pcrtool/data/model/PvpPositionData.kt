package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import java.io.Serializable

data class PvpPositionData(
    val positionType: Int,
    val list: List<PvpCharacterData>
) : Serializable