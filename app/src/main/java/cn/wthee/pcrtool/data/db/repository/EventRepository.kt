package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EventDao
import javax.inject.Inject

/**
 * 剧情活动 Repository
 *
 * @param eventDao
 */
class EventRepository @Inject constructor(private val eventDao: EventDao) {

    suspend fun getAllEvents(limit: Int) = eventDao.getAllEvents(limit)

    suspend fun getStoryDetails(storyId: Int) = eventDao.getStoryDetails(storyId)

    suspend fun getDropEvent() = eventDao.getDropEvent()

    suspend fun getTowerEvent(limit: Int) = eventDao.getTowerEvent(limit)

    suspend fun getFreeGachaEvent(limit: Int) = eventDao.getFreeGachaEvent(limit)

}