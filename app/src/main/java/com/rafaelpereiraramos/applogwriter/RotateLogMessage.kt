package com.rafaelpereiraramos.applogwriter

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Rafael P. Ramos on 02/11/2018.
 */
class RotateLogMessage : LogMessage {

    override fun toString(): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val threadName = Thread.currentThread().name

        return "[$dateStr][INFO]: Rotacionando o arquivo de log $threadName"
    }
}