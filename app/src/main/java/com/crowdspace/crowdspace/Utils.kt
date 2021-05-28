package com.crowdspace.crowdspace

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy")
    return formatter.format(date)
}