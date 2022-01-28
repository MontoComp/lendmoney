package com.cixsolution.jzc.pevoex.Utils

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toFullDateString(pattern: String ="dd/MM/yyyy '-' hh:mm") =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this.time)

fun Calendar.toDateString(pattern: String ="dd/MM/yyyy") =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this.time)

fun Calendar.toTimeString(pattern: String ="hh:mm") =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this.time)


fun Date.toCalendar(): Calendar = this.let { date -> Calendar.getInstance().apply { time = date } }

fun Date.toFullStringDate(pattern: String ="yyyy-MM-dd HH:mm:ss"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun Date.toMonth(pattern: String ="MMMM"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun Date.toWeekday(pattern: String ="EEE"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun Date.toDay(pattern: String ="dd"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun Date.toDateString(pattern: String ="dd/MM/yyyy") =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun Date.toTimeString(pattern: String ="hh:mm") =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun String.toDate(pattern: String ="dd/MM/yyyy"): Date = with(this){
    try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    }catch (_: Exception){
        return Date()
    }
}
fun String.toTime(pattern: String ="hh:mm"): Date = with(this){
    try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    }catch (_: Exception){
        return Date()
    }
}
fun String.toFullDate(pattern: String ="yyyy-MM-dd HH:mm:ss"): Date = with(this){
    try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    }catch (_: Exception){
        return Date()
    }
}

fun String.normalizeDate() = if (this.length == 1){
    "0$this"
}else this

fun betweenDays(starDate:Long,endDate:Long): Long{
    return try {
        java.util.concurrent.TimeUnit.DAYS.convert(starDate-endDate, java.util.concurrent.TimeUnit.MILLISECONDS) +1
    }catch (e:Exception){
        -1
    }
}
