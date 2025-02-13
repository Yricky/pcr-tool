package cn.wthee.pcrtool.data.model

import java.io.Serializable

/**
 * 通用接口返回数据
 */
class ResponseData<T>(
    var status: Int = 1,
    var data: T? = null,
    var message: String = ""
) : Serializable

/**
 * 错误
 */
fun <T> error(): ResponseData<T> = ResponseData(-1, null, "未正常获取数据，请重试~")

/**
 * 中断
 */
fun <T> cancel(): ResponseData<T> = ResponseData(-2, null, "查询取消~")

