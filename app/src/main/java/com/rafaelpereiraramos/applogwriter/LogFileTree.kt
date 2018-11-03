package com.rafaelpereiraramos.applogwriter

import timber.log.Timber

/**
 * Created by Rafael P. Ramos on 03/11/2018.
 */
class LogFileTree(private val service: AppLogService) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        service.write(WriteLogMessage(tag, message))
    }
}