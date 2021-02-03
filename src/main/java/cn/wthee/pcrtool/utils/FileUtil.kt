package cn.wthee.pcrtool.utils

import android.os.Build
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.*

/**
 * 文件路径获取
 */
object FileUtil {

    /**
     * 数据库所在文件夹
     */
    fun getDatabaseDir() = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        MyApplication.context.dataDir.absolutePath
    else {
        val path = MyApplication.context.filesDir.absolutePath
        path.substring(0, path.length - 6)
    } + "/databases"

    /**
     * 数据库路径
     */
    fun getDatabasePath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_NAME else Constants.DATABASE_NAME_JP

    /**
     * 数据库备份路径
     */
    fun getDatabaseBackupPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_BACKUP_NAME else Constants.DATABASE_BACKUP_NAME_JP

    /**
     * wal 文件路径
     */
    fun getDatabaseWalPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_WAL else Constants.DATABASE_WAL_JP

    /**
     * 备份 wal 文件路径
     */
    fun getDatabaseBackupWalPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_WAL_BACKUP else Constants.DATABASE_WAL_JP_BACKUP

    /**
     * shm 文件路径
     */
    private fun getDatabaseShmPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_SHM else Constants.DATABASE_SHM_JP

    /**
     * shm 文件路径
     */
    private fun getDatabaseBackupShmPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_SHM_BACKUP else Constants.DATABASE_SHM_JP_BACKUP


    /**
     * 数据库是否需要判断
     */
    fun needUpdate(type: Int): Boolean {
        val dbFile = File(getDatabasePath(type))
        val walFile = File(getDatabaseWalPath(type))
        val dbNotExists = !dbFile.exists()
        val dbSizeError = dbFile.length() < 1 * 1024 * 1024
        val walSizeError = walFile.exists() && walFile.length() < 1 * 1024
        return dbNotExists || dbSizeError || walSizeError
    }


    /**
     * 删除数据库文件
     */
    fun deleteMainDatabase(type: Int) {
        val db = File(getDatabasePath(type))
        if (db.exists()) {
            db.delete()
        }
        val wal = File(getDatabaseWalPath(type))
        if (wal.exists()) {
            wal.delete()
        }
        val shm = File(getDatabaseShmPath(type))
        if (shm.exists()) {
            shm.delete()
        }
    }

    /**
     * 删除备份数据库文件
     */
    fun deleteBackupDatabase(type: Int) {
        val db = File(getDatabaseBackupPath(type))
        if (db.exists()) {
            db.delete()
        }
        val wal = File(getDatabaseBackupWalPath(type))
        if (wal.exists()) {
            wal.delete()
        }
        val shm = File(getDatabaseBackupShmPath(type))
        if (shm.exists()) {
            shm.delete()
        }
    }

    /**
     * 保存文件
     */
    fun save(input: InputStream, output: File) {
        val out = FileOutputStream(output)
        val byte = ByteArray(1024 * 4)
        var line: Int
        while (input.read(byte).also { line = it } > 0) {
            out.write(byte, 0, line)
        }
        out.flush()
        out.close()
        input.close()
    }

    /**
     * 删除文件
     */
    fun delete(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * 备份文件
     */
    fun copy(source: String, dest: String) {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = FileInputStream(File(source))
            out = FileOutputStream(File(dest))
            val buffer = ByteArray(1024 * 4)
            var len: Int
            while (`in`.read(buffer).also { len = it } > 0) {
                out.write(buffer, 0, len)
            }
        } catch (e: Exception) {
            MainScope().launch {
                ToastUtil.short(ResourcesUtil.getString(R.string.backup_error))
            }
        } finally {
            `in`?.close()
            out?.close()
        }
    }


    /**
     * 获取历史数据库文件列表
     */
    private fun getOldList(): List<File>? {
        val file = File(getDatabaseDir())
        return file.listFiles()?.filter {
            try {
                val code = it.name.split("r")[0].toInt()
                code != BuildConfig.VERSION_CODE
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * 获取历史数据库文件大小
     */
    fun getOldDatabaseSize(): Long {
        var size = 0f
        getOldList()?.forEach {
            size += it.length()
        }
        return size.toLong()
    }


    /**
     * 删除历史数据库文件
     */
    fun deleteOldDatabase() {
        getOldList()?.forEach {
            it.delete()
        }
    }


    /**
     * 格式化文件大小格式
     */
    fun Long.convertFileSize(): String {
        val kb: Long = 1024
        val mb = kb * 1024
        val gb = mb * 1024
        return if (this >= gb) {
            String.format("%.1f GB", this.toFloat() / gb)
        } else if (this >= mb) {
            val f = this.toFloat() / mb
            String.format(if (f > 100) "%.0f MB" else "%.1f MB", f)
        } else if (this >= kb) {
            val f = this.toFloat() / kb
            String.format(if (f > 100) "%.0f KB" else "%.1f KB", f)
        } else String.format("%d B", this)
    }
}