package com.rafaelpereiraramos.applogwriter

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Rafael P. Ramos on 02/11/2018.
 */
class StopLogMessage : LogMessage {

    override fun toString(): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val threadName = Thread.currentThread().name

        return "[$dateStr][WARNING]: Parando o serviço de log $threadName"
    }
}