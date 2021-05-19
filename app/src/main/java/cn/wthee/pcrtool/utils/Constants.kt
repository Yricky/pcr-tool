package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.BuildConfig

/**
 * 常量
 */
object Constants {

    //图片格式
    const val WEBP = ".webp"
    const val RATIO = 1.78f
    const val MOVE_SPEED_RATIO = 0.5

    //图片资源地址
    private const val RESOURCE_URL = "https://wthee.xyz/resource/"

    //数据库资源地址
    const val DATABASE_URL = "https://wthee.xyz/db/"

    //接口正式地址
    const val API_URL = "https://wthee.xyz:8848/api/"

    const val JETPACK_COMPOSE_LOGO = RESOURCE_URL + "jetpack_compose.png"

    //日服数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_JP = "redive_jp.db.br"
    const val DATABASE_VERSION_URL_JP = "last_version_jp.json"
    const val DATABASE_NAME_JP = "redive_jp.db"
    const val DATABASE_WAL_JP = "redive_jp.db-wal"
    const val DATABASE_SHM_JP = "redive_jp.db-shm"

    //日服备份数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_BACKUP_JP =
        BuildConfig.VERSION_CODE.toString() + DATABASE_DOWNLOAD_FILE_NAME_JP
    const val DATABASE_BACKUP_NAME_JP = BuildConfig.VERSION_CODE.toString() + DATABASE_NAME_JP
    const val DATABASE_WAL_JP_BACKUP = BuildConfig.VERSION_CODE.toString() + DATABASE_WAL_JP


    //国服数据库
    const val DATABASE_VERSION_URL = "last_version_cn.json"
    const val DATABASE_DOWNLOAD_FILE_NAME = "redive_cn.db.br"
    const val DATABASE_NAME = "redive_cn.db"
    const val DATABASE_WAL = "redive_cn.db-wal"
    const val DATABASE_SHM = "redive_cn.db-shm"

    //国服备份数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_BACKUP =
        BuildConfig.VERSION_CODE.toString() + DATABASE_DOWNLOAD_FILE_NAME
    const val DATABASE_BACKUP_NAME = BuildConfig.VERSION_CODE.toString() + DATABASE_NAME
    const val DATABASE_WAL_BACKUP = BuildConfig.VERSION_CODE.toString() + DATABASE_WAL


    //其它数据库
    const val DATABASE_NEWS = "news.db"
    const val DATABASE_PVP = "pvp.db"

    //角色卡片接口
    const val CHARACTER_URL = RESOURCE_URL + "card/profile/"
    const val CHARACTER_FULL_URL = RESOURCE_URL + "card/full/"

    //现实角色卡片接口
    const val Reality_CHARACTER_URL = RESOURCE_URL + "card/actual_profile/"

    //装备图标接口
    const val UNKNOWN_EQUIP_ID = 999999
    const val EQUIPMENT_URL = RESOURCE_URL + "icon/equipment/"
    const val UNKNOWN_EQUIPMENT_ICON = EQUIPMENT_URL + UNKNOWN_EQUIP_ID + WEBP

    //图标接口
    const val UNIT_ICON_URL = RESOURCE_URL + "icon/unit/"

    //技能图标接口
    const val SKILL_ICON_URL = RESOURCE_URL + "icon/skill/"

    //本地储存
    const val SP_DATABASE_TYPE = "database_type"
    const val SP_DATABASE_VERSION = "database_version"
    const val SP_DATABASE_VERSION_JP = "database_version_jp"
    const val SP_STAR_CHARACTER = "star_character"
    const val SP_STAR_EQUIP = "star_equip"
    const val SP_VIBRATE_STATE = "vibrate_state"
    const val SP_ANIM_STATE = "animation_state"

    //默认值
    const val NOTICE_TITLE = "正在下载数据"
    const val NOTICE_TOAST_SUCCESS = "数据更新完成！"
    const val NOTICE_TOAST_CHANGE_SUCCESS = "数据切换完成！"
    const val RANK_UPPER = "RANK"

    //常量值
    const val LOG_TAG = "log_tag"
    const val RANK = "rank"

    val ATTR = arrayListOf(
        "HP",
        "HP吸收",
        "物理攻击力",
        "魔法攻击力",
        "物理防御力",
        "魔法防御力",
        "物理暴击",
        "魔法暴击",
        "物理穿透",
        "魔法穿透",
        "命中",
        "回避",
        "HP回复",
        "回复量上升",
        "TP回复",
        "TP上升",
        "TP消耗减少",
    )

    val errorIDs = arrayListOf(
        101001,
        101301,
        101501,
        102201,
        102801,
        103801,
        104501,
        104601,
        105401,
    )

    val notExistsIDs = arrayListOf(
        109731, 109831, 109931,
    )

    // 异常
    const val EXCEPTION = "异常"
    const val EXCEPTION_BACK_TOP = "回到顶部$EXCEPTION"
    const val EXCEPTION_MENU_NAV = "菜单跳转$EXCEPTION"
    const val EXCEPTION_API = "接口$EXCEPTION"
    const val EXCEPTION_DOWNLOAD_DB = "数据库文件下载$EXCEPTION"
    const val EXCEPTION_SAVE_DB = "数据库文件保存$EXCEPTION"
    const val EXCEPTION_LOAD_PIC = "图片加载$EXCEPTION"
    const val EXCEPTION_DOWNLOAD_PIC = "图片下载$EXCEPTION"
    const val EXCEPTION_LOAD_ATTR = "获取属性$EXCEPTION"
    const val EXCEPTION_UNIT_NULL = "角色信息空值$EXCEPTION"
    const val EXCEPTION_SKILL = "角色技能$EXCEPTION"
    const val EXCEPTION_PVP_DIALOG = "竞技场查询弹窗$EXCEPTION"
}