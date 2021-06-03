package com.crowdspace.crowdspace

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("HH:mm:ss EEE MMM dd ")
    return formatter.format(date)
}