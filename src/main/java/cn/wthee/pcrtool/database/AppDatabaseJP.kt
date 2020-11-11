package cn.wthee.pcrtool.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.CharacterDao
import cn.wthee.pcrtool.data.EnemyDao
import cn.wthee.pcrtool.data.EquipmentDao
import cn.wthee.pcrtool.data.GachaDao
import cn.wthee.pcrtool.data.entity.*
import cn.wthee.pcrtool.data.entityjp.*
import cn.wthee.pcrtool.utils.Constants.DATABASE_Name_JP


@Database(
    entities = [
        CharacterProfile::class,
        Character6Star::class,
        CharacterActualData::class,
        CharacterDataJP::class,
        CharacterPromotion::class,
        CharacterPromotionStatus::class,
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
        WaveGroupData::class,
        EnemyDataJP::class,
        CharacterExperience::class,
        AttackPattern::class,
        GuildData::class,
        CharacterExperienceTeam::class,
        CharacterComments::class,
        GachaDataJP::class,
        GachaExchangeJP::class,
        ItemDataJP::class
    ],
    version = 16,
    exportSchema = false
)
abstract class AppDatabaseJP : RoomDatabase() {

    abstract fun getCharacterDao(): CharacterDao
    abstract fun getEquipmentDao(): EquipmentDao
    abstract fun getEnemyDao(): EnemyDao
    abstract fun getGachaDao(): GachaDao

    companion object {

        @Volatile
        private var instance: AppDatabaseJP? = null

        fun getInstance(): AppDatabaseJP {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase()
                        .also { instance = it }
            }
        }


        private fun buildDatabase(): AppDatabaseJP {
            return Room.databaseBuilder(
                MyApplication.context,
                AppDatabaseJP::class.java,
                DATABASE_Name_JP
            ).fallbackToDestructiveMigration().build()
        }
    }

}
