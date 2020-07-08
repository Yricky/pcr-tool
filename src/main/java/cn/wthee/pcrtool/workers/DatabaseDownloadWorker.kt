package cn.wthee.pcrtool.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.service.DatabaseService
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.AppDatabase
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.concurrent.thread


class DatabaseDownloadWorker(
    @NonNull context: Context,
    @NonNull parameters: WorkerParameters?
) : CoroutineWorker(context, parameters!!) {

    private val title = "正在更新数据库..."
    private val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "1"
    private val noticeId = 1
    private lateinit var notification: NotificationCompat.Builder

    //适配低版本数据库路径
    private val folderPath = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        MyApplication.getContext().dataDir.absolutePath
    else {
        val path = MyApplication.getContext().filesDir.absolutePath
        path.substring(0, path.length - 6)
    } + "/databases"

    private val dbPath =
        MyApplication.getContext().getDatabasePath(Constants.DATABASE_CN_File_Name).absolutePath


    companion object {
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_VERSION = "KEY_VERSION"
    }

    override suspend fun doWork(): Result = coroutineScope {
        val inputData: Data = inputData
        //下载地址
        val inputUrl = inputData.getString(KEY_INPUT_URL) ?: return@coroutineScope Result.failure()
        //版本号
        val version = inputData.getString(KEY_VERSION) ?: return@coroutineScope Result.failure()
        setForegroundAsync(createForegroundInfo())
        return@coroutineScope download(inputUrl, version)
    }


    private fun download(inputUrl: String, version: String): Result {
        try {
            //创建Retrofit服务
            val service = ApiHelper.createWithClient(
                DatabaseService::class.java, inputUrl,
                ApiHelper.downloadClientBuild(object : DownloadListener {
                    //下载进度
                    override fun onProgress(progress: Int, currSize: Float, totalSize: Float) {
                        Log.e(Constants.LOG_TAG, progress.toString())
                        notification.setProgress(100, progress, false)
                            .setContentTitle(
                                "$title ${String.format(
                                    "%.1f",
                                    currSize
                                )}KB / ${String.format("%.1f", totalSize)} KB"
                            )
                            .build()
                        notificationManager.notify(noticeId, notification.build())
                    }

                    override fun onFinish() {
                    }
                })
            )

            //下载文件
            val response = service.getDb(Constants.DATABASE_CN_File_Name).execute()
            sp.edit {
                putString(
                    Constants.SP_DATABASE_VERSION,
                    version
                )
            }
            val file = response.body()?.byteStream()
            //保存
            saveDB(file)
            return Result.success()
        }catch (e: Exception){
            return Result.failure()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val context: Context = applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setTicker(title)
            .setSmallIcon(R.drawable.ic_logo)
            .setOngoing(true)
            .setProgress(100, 0, true)
        return ForegroundInfo(noticeId, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(channelId, "数据更新", NotificationManager.IMPORTANCE_MAX)
        notificationManager.createNotificationChannel(channel)
    }

    //数据库保存
    private fun saveDB(input: InputStream?) {
        //创建数据库文件夹
        val file = File(folderPath)
        if (!file.exists()) {
            file.mkdir()
        }
        //删除已有数据库文件
        val db = File(dbPath)
        if (db.exists()) {
            FileUtil.deleteDir(folderPath, dbPath)
        }
        try {
            input?.let { inputStream ->
                val out = FileOutputStream(db)
                val byte = ByteArray(1024 * 4)
                var line: Int
                while (inputStream.read(byte).also { line = it } > 0) {
                    out.write(byte, 0, line)
                }
                out.flush()
                out.close()
                inputStream.close()
                thread(start = true) {
                    //更新数据库
                    AppDatabase.getInstance().close()
                    synchronized(AppDatabase::class.java) {
                        UnzippedUtil.deCompress(db, true)
                        //TODO 更新完成后，重新加载数据
                        CharacterListFragment.viewModel.reload.postValue(true)
                    }
                }
            }
        } catch (e: Exception) {
            ToastUtil.short(e.message)
        }

    }

}