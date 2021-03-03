package com.applandeo.materialcalendarview.utils

import android.graphics.PorterDuff
import android.graphics.Typeface
import android.widget.TextView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.R
import java.util.*

/**
 * This class is used to set a style of calendar cells.
 *
 *
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 *
 * Modified by wthee
 */
object DayColorsUtils {
    /**
     * This is general method which sets a color of the text, font type and a background of a TextView object.
     * It is used to set day cell (numbers) style.
     *
     * @param textView   TextView containing a day number
     * @param textColor  A resource of a color of the day number
     * @param typeface   A type of text style, can be set as NORMAL or BOLD
     * @param background A resource of a background drawable
     */
    fun setDayColors(textView: TextView?, textColor: Int, typeface: Int, background: Int) {
        if (textView == null) {
            return
        }
        textView.setTypeface(null, typeface)
        textView.setTextColor(textColor)
        textView.setBackgroundResource(background)
    }

    /**
     * This method sets a color of the text, font type and a background of a TextView object.
     * It is used to set day cell (numbers) style in the case of selected day (when calendar is in
     * the picker mode). It also colors a background of the selection.
     *
     * @param dayLabel           TextView containing a day number
     * @param calendarProperties A resource of a selection background color
     */
    fun setSelectedDayColors(dayLabel: TextView?, calendarProperties: CalendarProperties) {
        setDayColors(
            dayLabel, calendarProperties.selectionLabelColor, Typeface.NORMAL,
            R.drawable.background_color_circle_selector
        )
        setDayBackgroundColor(dayLabel, calendarProperties.selectionColor)
    }

    /**
     * This method is used to set a color of texts, font types and backgrounds of TextView objects
     * in a current visible month. Visible day labels from previous and forward months are set using
     * setDayColors() method. It also checks if a day number is a day number of today and set it
     * a different color and bold face type.
     *
     * @param day                A calendar instance representing day date
     * @param today              A calendar instance representing today date
     * @param dayLabel           TextView containing a day numberx
     * @param calendarProperties A resource of a color used to mark today day
     */
    fun setCurrentMonthDayColors(
        day: Calendar?, today: Calendar?, dayLabel: TextView?,
        calendarProperties: CalendarProperties
    ) {
        if (today == day) {
            setTodayColors(dayLabel, calendarProperties)
        } else if (EventDayUtils.isEventDayWithLabelColor(day, calendarProperties)) {
            setEventDayColors(day, dayLabel, calendarProperties)
        } else if (calendarProperties.highlightedDays.contains(day)) {
            setHighlightedDayColors(dayLabel, calendarProperties)
        } else {
            setNormalDayColors(dayLabel, calendarProperties)
        }
    }

    private fun setTodayColors(dayLabel: TextView?, calendarProperties: CalendarProperties) {
        setDayColors(
            dayLabel, calendarProperties.todayLabelColor, Typeface.BOLD,
            R.drawable.background_transparent
        )

        // Sets custom background color for present
        if (calendarProperties.todayColor != 0) {
            setDayColors(
                dayLabel, calendarProperties.selectionLabelColor, Typeface.NORMAL,
                R.drawable.background_color_circle_selector
            )
            setDayBackgroundColor(dayLabel, calendarProperties.todayColor)
        }
    }

    private fun setEventDayColors(
        day: Calendar?,
        dayLabel: TextView?,
        calendarProperties: CalendarProperties
    ) {
        EventDayUtils.getEventDayWithLabelColor(day, calendarProperties)
            .executeIfPresent { eventDay: EventDay ->
                setDayColors(
                    dayLabel, eventDay.labelColor,
                    Typeface.NORMAL, R.drawable.background_transparent
                )
            }
    }

    private fun setHighlightedDayColors(
        dayLabel: TextView?,
        calendarProperties: CalendarProperties
    ) {
        setDayColors(
            dayLabel, calendarProperties.highlightedDaysLabelsColor,
            Typeface.NORMAL, R.drawable.background_transparent
        )
    }

    private fun setNormalDayColors(dayLabel: TextView?, calendarProperties: CalendarProperties) {
        setDayColors(
            dayLabel, calendarProperties.daysLabelsColor, Typeface.NORMAL,
            R.drawable.background_transparent
        )
    }

    private fun setDayBackgroundColor(dayLabel: TextView?, color: Int) {
        dayLabel!!.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }
}