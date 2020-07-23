package cn.wthee.pcrtool.data


//角色数据Repository

class CharacterRepository(private val characterDao: CharacterDao) {

    //获取角色个人资料
    suspend fun getInfoAndData(name: String, filter: Map<String, Int>) =
        characterDao.getInfoAndData(name)

    //获取角色Rank所需装备id
    suspend fun getEquipmentIds(unitId: Int, rank: Int) =
        characterDao.getRankEquipment(unitId, rank)

    //角色Rank属性状态
    suspend fun getRankStutas(unitId: Int, rank: Int) = characterDao.getRankStatus(unitId, rank)

    //角色星级信息
    suspend fun getRarity(unitId: Int, rarity: Int) = characterDao.getRarity(unitId, rarity)

    //角色Rank最大值
    suspend fun getMaxRank(id: Int) = characterDao.getMaxRank(id)

    //角色星级最大值
    suspend fun getMaxRarity(id: Int) = characterDao.getMaxRarity(id)

    //角色技能
    suspend fun getCharacterSkill(id: Int) = characterDao.getCharacterSkill(id)

    //技能数据
    suspend fun getSkillData(sid: Int) = characterDao.getSkillData(sid)

    //角色技能详情
    suspend fun getSkillActions(aids: List<Int>) = characterDao.getSkillActions(aids)

    //角色最大等级
    suspend fun getMaxLevel() = characterDao.getMaxLevel()

    companion object {

        @Volatile
        private var instance: CharacterRepository? = null

        fun getInstance(characterDao: CharacterDao) =
            instance ?: synchronized(this) {
                instance ?: CharacterRepository(characterDao).also { instance = it }
            }
    }
}