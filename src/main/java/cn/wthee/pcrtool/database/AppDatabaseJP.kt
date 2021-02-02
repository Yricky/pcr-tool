package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.dao.CharacterDao
import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.db.dao.EventDao
import cn.wthee.pcrtool.data.db.dao.GachaDao
import cn.wthee.pcrtool.data.db.entity.*
import cn.wthee.pcrtool.data.db.entityjp.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.DATABASE_NAME_JP
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File


@Database(
    entities = [
        CharacterProfile::class,
        Character6Star::class,
        CharacterActualData::class,
        CharacterDataJP::class,
        CharacterPromotion::class,
        CharacterPromotionStatus::class,
        CharacterExperience::class,
        CharacterRarity::class,
        CharacterSkillDataJP::class,
        EnemyRewardData::class,
        EquipmentCraft::class,
        EquipmentDataJP::class,
        EquipmentEnhanceRate::class,
        EquipmentEnhanceData::class,
        UniqueEquipmentData::class,
        UniqueEquipmentEnhanceData::class,
        UniqueEquipmentEnhanceRate::class,
        QuestDataJP::class,
        SkillAction::class,
        SkillData::class,
        WaveGroupDataJP::class,
        AttackPattern::class,
        GuildData::class,
        CharacterComments::class,
        GachaDataJP::class,
        GachaExchangeJP::class,
        ItemDataJP::class,
        CharacterStoryStatus::class,
        CharacterType::class,
        EventStoryData::class,
        EventStoryDetail::class,
        EventTopAdvJP::class,
        OddsNameData::class,
        HatsuneScheduleJP::class,
        CampaignScheduleJP::class,
        CharacterRoomComments::class,
        AilmentData::class,
    ],
    version = 65,
    exportSchema = false
)
/**
 * 日服版本数据库
 */
abstract class AppDatabaseJP : RoomDatabase() {

    abstract fun getCharacterDao(): CharacterDao
    abstract fun getEquipmentDao(): EquipmentDao
    abstract fun getGachaDao(): GachaDao
    abstract fun getEventDao(): EventDao

    companion object {

        @Volatile
        private var instance: AppDatabaseJP? = null

        val sp = ActivityHelper.instance.currentActivity!!.getSharedPreferences(
            "main",
            Context.MODE_PRIVATE
        )

        /**
         * 自动获取数据库、远程备份数据
         */
        fun getInstance(): AppDatabaseJP {
            try {
                //尝试打开数据库
                if (File(FileUtil.getDatabasePath(2)).exists()) {
                    buildDatabase(DATABASE_NAME_JP).openHelper.readableDatabase
                }
            } catch (e: Exception) {
                //fixme 上传日志
                UMCrash.generateCustomLog("OpenDatabaseException", "更新国服数据结构！！！")
                //启用远程备份数据库
                sp.edit {
                    putBoolean(Constants.SP_BACKUP_JP, true)
                }
                MainScope().launch {
                    ToastUtil.short(ResourcesUtil.getString(R.string.database_remote_backup))
                }
                //打开远程备份
                return instance ?: synchronized(this) {
                    instance
                        ?: buildDatabase(Constants.DATABASE_BACKUP_NAME_JP)
                            .also { instance = it }
                }
            }
            //正常打开
            sp.edit {
                putBoolean(Constants.SP_BACKUP_JP, false)
            }
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(DATABASE_NAME_JP)
                        .also { instance = it }
            }
        }


        @SuppressLint("UnsafeOptInUsageError")
        private fun buildDatabase(name: String): AppDatabaseJP {
            return Room.databaseBuilder(
                MyApplication.context,
                AppDatabaseJP::class.java,
                name
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}
