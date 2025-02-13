package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.network.MyAPIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 额外装备掉落 ViewModel
 *
 * @param apiRepository
 */
@HiltViewModel
class RandomEquipAreaViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository,
    private val equipmentRepository: EquipmentRepository,
) : ViewModel() {

    /**
     * 获取掉落地信息
     */
    fun getEquipArea(equipId: Int) = flow {
        val data = apiRepository.getEquipArea(equipId).data
        data?.let {
            val maxArea = equipmentRepository.getMaxArea() % 100
            emit(
                it.filter { areaData -> areaData.area <= maxArea }
                    .sortedByDescending { areaData -> areaData.area }
            )
        }
    }

}
