package cn.wthee.pcrtool.utils

import android.graphics.Bitmap
import androidx.palette.graphics.Palette

/**
 * 取色器
 */
object PaletteHelper {

    fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()
}