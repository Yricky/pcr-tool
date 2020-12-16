package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import cn.wthee.pcrtool.data.db.view.Attr

//角色星级属性
@Entity(
    tableName = "unit_rarity",
    indices = [Index(
        value = arrayOf("unit_id"),
        unique = false,
        name = "unit_rarity_0_unit_id"
    ), Index(
        value = arrayOf("unit_material_id"),
        unique = false,
        name = "unit_rarity_0_unit_material_id"
    )],
    primaryKeys = ["unit_id", "rarity"]
)
data class CharacterRarity(
    @ColumnInfo(name = "unit_id") val unitid: Int,
    @ColumnInfo(name = "rarity") val rarity: Int,
    @Embedded val attr: Attr,
    @ColumnInfo(name = "hp_growth") val hpGrowth: Double,
    @ColumnInfo(name = "atk_growth") val atkGrowth: Double,
    @ColumnInfo(name = "magic_str_growth") val magicStrGrowth: Double,
    @ColumnInfo(name = "def_growth") val defGrowth: Double,
    @ColumnInfo(name = "magic_def_growth") val magicDefGrowth: Double,
    @ColumnInfo(name = "physical_critical_growth") val physicalCriticalGrowth: Double,
    @ColumnInfo(name = "magic_critical_growth") val magicCriticalGrowth: Double,
    @ColumnInfo(name = "wave_hp_recovery_growth") val waveHpRecoveryGrowth: Double,
    @ColumnInfo(name = "wave_energy_recovery_growth") val waveEnergyRecoveryGrowth: Double,
    @ColumnInfo(name = "dodge_growth") val dodgeGrowth: Double,
    @ColumnInfo(name = "physical_penetrate_growth") val physicalPenetrateGrowth: Double,
    @ColumnInfo(name = "magic_penetrate_growth") val magicPenetrateGrowth: Double,
    @ColumnInfo(name = "life_steal_growth") val lifeStealGrowth: Double,
    @ColumnInfo(name = "hp_recovery_rate_growth") val hpRecoveryRateGrowth: Double,
    @ColumnInfo(name = "energy_recovery_rate_growth") val energyRecoveryRateGrowth: Double,
    @ColumnInfo(name = "energy_reduce_rate_growth") val energyReduceRateGrowth: Double,
    @ColumnInfo(name = "unit_material_id") val unitMaterialId: Int,
    @ColumnInfo(name = "consume_num") val consumeNum: Int,
    @ColumnInfo(name = "consume_gold") val consumeGold: Int,
    @ColumnInfo(name = "accuracy_growth") val accuracyGrowth: Double
)