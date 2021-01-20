package cn.wthee.pcrtool.utils

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 *  把 - 拼接的字符串，转化为数组
 */
fun String.intArrayList(): ArrayList<Int> {
    val list = arrayListOf<Int>()
    val ids = this.split("-")
    ids.forEachIndexed { _, id ->
        if (id != "") {
            list.add(id.toInt())
        }
    }
    return list
}

val df: DateFormat = SimpleDateFormat("yyyy/MM/dd")

/**
 *  计算日期字符串间隔天数 yyyy-MM-dd  this - str2 相差天数
 */
fun String.days(str2: String): String {
    return try {
        val d1 = df.parse(this)!!
        val d2 = df.parse(str2)!!
        String.format("%02d", (d1.time - d2.time) / (60 * 60 * 1000 * 24))
    } catch (e: Exception) {
        "0"
    }
}