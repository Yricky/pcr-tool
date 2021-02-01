package cn.wthee.pcrtool.utils

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.service.MyAPIService
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import cn.wthee.pcrtool.utils.ResourcesUtil.getString
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 应用更新
 */
object AppUpdateUtil {

    /**
     * 校验版本
     */
    fun init(context: Context, inflater: LayoutInflater, showToast: Boolean = false) {

        //本地版本
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, 0)
        val localVersion = if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
            info.longVersionCode
        } else {
            info.versionCode.toLong()
        }

        val service = ApiUtil.create(
            MyAPIService::class.java,
            Constants.API_URL
        )
        MainScope().launch {
            try {
                if (NetworkUtil.isEnable()) {
                    val version = service.getAppVersion()
                    if (version.message == "success") {
                        if (localVersion < version.data!!.versionCode) {
                            //有新版本发布，弹窗
                            DialogUtil.create(
                                context,
                                LayoutWarnDialogBinding.inflate(inflater),
                                "版本更新：${info.versionName} > ${version.data!!.versionName} ",
                                version.data!!.content,
                                "暂不更新",
                                "前往下载",
                                object : DialogListener {
                                    override fun onCancel(dialog: AlertDialog) {
                                        dialog.dismiss()
                                    }

                                    override fun onConfirm(dialog: AlertDialog) {
                                        BrowserUtil.open(context, version.data!!.url)
                                    }
                                }).show()

                        } else if (showToast) {
                            ToastUtil.short("应用已是最新版本~")
                        }
                    }
                } else {
                    ToastUtil.short(getString(R.string.network_error))
                }
            } catch (e: Exception) {

            }

        }

    }

}