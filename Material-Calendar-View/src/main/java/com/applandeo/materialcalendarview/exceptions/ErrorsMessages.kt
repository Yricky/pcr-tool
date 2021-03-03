package com.applandeo.materialcalendarview.exceptions

/**
 * Created by Mateusz Kornakiewicz on 09.08.2018.
 *
 * Modified by wthee
 */
object ErrorsMessages {
    const val ONE_DAY_PICKER_MULTIPLE_SELECTION =
        "ONE_DAY_PICKER DOES NOT SUPPORT MULTIPLE DAYS, USE setDate() METHOD INSTEAD"
    const val OUT_OF_RANGE_MIN = "SET DATE EXCEEDS THE MINIMUM DATE"
    const val OUT_OF_RANGE_MAX = "SET DATE EXCEEDS THE MAXIMUM DATE"
    const val RANGE_PICKER_NOT_RANGE = "RANGE_PICKER ACCEPTS ONLY CONTINUOUS LIST OF CALENDARS"
}