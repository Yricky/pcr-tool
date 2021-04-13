package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.PvpDao
import javax.inject.Inject

/**
 * 竞技场收藏 Repository
 *
 * 数据来源 [PvpDao]
 */
class PvpRepository @Inject constructor(private val pvpDao: PvpDao) {

    suspend fun getLiked(region: Int) = pvpDao.getAll(region)

    companion object {

        fun getInstance(pvpDao: PvpDao) = PvpRepository(pvpDao)
    }
}