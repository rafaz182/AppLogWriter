package com.rafaelpereiraramos.applogwriter

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Rafael P. Ramos on 02/11/2018.
 */
data class WriteLogMessage(
    val tag: String,
    val message: String,
    val date: Date = Date()
) : LogMessage {

    override fun toString(): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date)

        return "[$dateStr][$tag]: $message"
    }
}