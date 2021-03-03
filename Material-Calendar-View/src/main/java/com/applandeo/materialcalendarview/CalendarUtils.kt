package com.applandeo.materialcalendarview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Mateusz Kornakiewicz on 03.08.2018.
 *
 * Modified by wthee
 */
object CalendarUtils {
    /*
    Utils method to create drawable containing text
     */
    fun getDrawableText(context: Context, text: String, color: Int, size: Int): Drawable {
        val resources = context.resources
        val bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = ContextCompat.getColor(context, color)
        val scale = resources.displayMetrics.density
        paint.textSize = (size * scale)
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val x = (bitmap.width - bounds.width()) / 2
        val y = (bitmap.height + bounds.height()) / 2
        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
        return BitmapDrawable(context.resources, bitmap)
    }

    /**
     * This method returns a list of calendar objects between two dates
     *
     * @param firstDay Calendar representing a first selected date
     * @param lastDay  Calendar representing a last selected date
     * @return List of selected dates between two dates
     */
    fun getDatesRange(firstDay: Calendar?, lastDay: Calendar): ArrayList<Calendar> {
        return if (lastDay.before(firstDay)) {
            getCalendarsBetweenDates(
                lastDay.time,
                firstDay!!.time
            )
        } else getCalendarsBetweenDates(
            firstDay!!.time,
            lastDay.time
        )
    }

    private fun getCalendarsBetweenDates(dateFrom: Date, dateTo: Date): ArrayList<Calendar> {
        val calendars = ArrayList<Calendar>()
        val calendarFrom = Calendar.getInstance()
        calendarFrom.time = dateFrom
        val calendarTo = Calendar.getInstance()
        calendarTo.time = dateTo
        val daysBetweenDates = TimeUnit.MILLISECONDS.toDays(
            calendarTo.timeInMillis - calendarFrom.timeInMillis
        )
        for (i in 1 until daysBetweenDates) {
            val calendar = calendarFrom.clone() as Calendar
            calendars.add(calendar)
            calendar.add(Calendar.DATE, i.toInt())
        }
        return calendars
    }
}