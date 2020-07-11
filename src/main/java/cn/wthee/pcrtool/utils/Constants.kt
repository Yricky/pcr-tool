package cn.wthee.pcrtool.utils

object Constants {
    //图片格式
    const val WEBP = ".webp"

    //接口地址
    const val API_URL = "https://redive.estertion.win/"

    //数据库中版
    const val DATABASE_CN_DOWNLOAD_File_Name = "redive_cn.db.br"
    const val DATABASE_CN_Name = "redive_cn.db"
    const val DATABASE_CN_WAL = "redive_cn.db-wal"

    //角色卡片接口
    const val CHARACTER_URL = API_URL + "card/profile/"

    //现实角色卡片接口
    const val Reality_CHARACTER_URL = API_URL + "card/actual_profile/"

    //装备图标接口
    const val EQUIPMENT_URL = API_URL + "icon/equipment/"

    //图标接口
    const val UNIT_ICON_URL = API_URL + "icon/unit/"

    //角色界面接口
    const val CHARACTER_PLATE_URL = API_URL + "icon/plate/"

    //技能图标接口
    const val SKILL_ICON_URL = API_URL + "icon/skill/"

    //角色Rank
    const val CHARACTER_MIN_RANK = 2

    //Log输出
    const val LOG_TAG = "pcrtool_log_info"

    //本地储存
    const val SP_DATABASE_VERSION = "database_version"
    const val SP_SORT_TYPE = "sort_type"
    const val SP_SORT_ASC = "sort_asc"

    //数量
    const val SP_COUNT_CHARACTER = "count_character"
    const val SP_COUNT_EQUIP = "count_equip"
    const val SP_COUNT_ENEMY = "count_enemy"

    //默认值
    const val DATABASE_VERSION = "202005141837"
    const val SORT_TYPE = 0
    const val SORT_ASC = true
    const val UNKNOW_EQUIP_ID = 999999
    const val NOTICE_TITLE = "正在更新数据..."
    const val NOTICE_TITLE_PRE = "PCR Tool\n"

    const val NOTICE_TOAST_SUCCESS = "数据更新完成"
    const val NOTICE_TOAST_TITLE = "数据更新中\n详情查看通知栏"
    const val NOTICE_TOAST_TIMEOUT = "数据库信息无法访问\n请尝试切换上网方式"
    const val NOTICE_TOAST_CHECKING = "正在检测数据库版本"
    const val NOTICE_TOAST_CHECKED = "数据库已是最新版本"

    //列表列数
    const val COLUMN_COUNT = 1
    const val COLUMN_COUNT_EQUIP = 4

    //排序
    const val SORT_AGE = 0
    const val SORT_HEIGHT = 1
    const val SORT_WEIGHT = 2
    const val SORT_POSITION = 3

}