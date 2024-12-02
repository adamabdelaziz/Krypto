package org.adam.kryptobot.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatUnixTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.format(date)
}