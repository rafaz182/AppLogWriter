package com.rafaelpereiraramos.applogwriter

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Rafael P. Ramos on 02/11/2018.
 */
class WriteLogMessage(
    val tag: String? = "Tag",
    val message: String
) : LogMessage {

    lateinit var date: Date

    constructor(date: Date = Date(), tag: String, message: String): this(tag, message) {
        this.date = date
    }

    override fun toString(): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date)

        return "[$dateStr][$tag]: $message"
    }
}