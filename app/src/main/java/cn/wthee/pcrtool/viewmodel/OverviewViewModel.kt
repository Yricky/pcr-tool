package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页纵览
 */
@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val equipmentRepository: EquipmentRepository,
    private val eventRepository: EventRepository,
    private val gachaRepository: GachaRepository,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    /**
     * 获取角色数量
     */
    fun getCharacterCount() = flow {
        try {
            emit(unitRepository.getCount())
        } catch (e: Exception) {

        }
    }

    /**
     * 获取角色列表
     */
    fun getCharacterList() = flow {
        try {
            emit(unitRepository.getInfoAndData(FilterCharacter(), "全部", 10))
        } catch (e: Exception) {

        }
    }

    /**
     * 获取装备数量
     */
    fun getEquipCount() = flow {
        try {
            emit(equipmentRepository.getCount())
        } catch (e: Exception) {

        }
    }

    /**
     * 获取装备列表
     */
    fun getEquipList(limit: Int) = flow {
        try {
            emit(equipmentRepository.getEquipments(FilterEquipment(), "全部", limit))
        } catch (e: Exception) {

        }
    }

    /**
     * 获取卡池列表
     *
     * @param type 0：进行中 1：预告
     */
    fun getGachaList(type: Int) = flow {
        try {
            val regionType = getRegion()
            val today = getToday()
            val data = gachaRepository.getGachaHistory(10)

            if (type == 0) {
                emit(
                    data.filter {
                        isInProgress(today, it.startTime, it.endTime, regionType)
                    }.sortedWith(compareGacha(today))
                )
            } else {
                emit(
                    data.filter {
                        isComingSoon(today, it.startTime, regionType)
                    }.sortedWith(compareGacha(today))
                )
            }
        } catch (e: Exception) {

        }
    }

    /**
     * 获取活动列表
     *
     * @param type 0：进行中 1：预告
     */
    fun getCalendarEventList(type: Int) = flow {
        try {
            val regionType = getRegion()
            val today = getToday()
            val data = eventRepository.getDropEvent() + eventRepository.getTowerEvent(1)

            if (type == 0) {
                emit(
                    data.filter {
                        isInProgress(today, it.startTime, it.endTime, regionType)
                    }.sortedWith(compareEvent(today))
                )
            } else {
                emit(
                    data.filter {
                        isComingSoon(today, it.startTime, regionType)
                    }.sortedWith(compareEvent(today))
                )
            }
        } catch (e: Exception) {

        }

    }

    /**
     * 获取剧情活动列表
     *
     * @param type 0：进行中 1：预告
     */
    fun getStoryEventList(type: Int) = flow {
        try {
            val regionType = getRegion()
            val today = getToday()
            val data = eventRepository.getAllEvents(10)

            if (type == 0) {
                emit(
                    data.filter {
                        isInProgress(today, it.startTime, it.endTime, regionType)
                    }.sortedWith(compareStoryEvent(today))
                )
            } else {
                emit(
                    data.filter {
                        isComingSoon(today, it.startTime, regionType)
                    }.sortedWith(compareStoryEvent(today))
                )
            }
        } catch (e: Exception) {

        }
    }

    /**
     * 获取新闻
     */
    fun getNewsOverview(region: Int) = flow {
        try {
            val data = apiRepository.getNewsOverviewByRegion(region).data
            data?.let {
                emit(it)
            }
        } catch (e: Exception) {

        }
    }

    /**
     * 六星 id 列表
     */
    fun getR6Ids() {
        viewModelScope.launch {
            try {
                navViewModel.r6Ids.postValue(unitRepository.getR6Ids())
            } catch (e: Exception) {

            }
        }
    }


    /**
     * 获取免费十连卡池列表
     *
     * @param type 0：进行中 1：预告
     */
    fun getFreeGachaList(type: Int) = flow {
        try {
            val regionType = getRegion()
            val today = getToday()
            val data = eventRepository.getFreeGachaEvent(1)

            if (type == 0) {
                emit(data.filter {
                    isInProgress(today, it.startTime, it.endTime, regionType)
                })
            } else {
                emit(data.filter {
                    isComingSoon(today, it.startTime, regionType)
                })
            }
        } catch (e: Exception) {

        }
    }
}