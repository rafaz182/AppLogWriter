package com.rafaelpereiraramos.applogwriter

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.io.File
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by Rafael P. Ramos on 31/10/2018.
 */
class AppLogService private constructor(
    private var storageDir: File
): Service() {

    private val writer = WriteTask(storageDir)

    var clearOldFiles = true

    override fun onBind(intent: Intent?): IBinder?  {

    }

    override fun onDestroy() {

    }

    fun write(logMessage: LogMessage) = writer.insert(logMessage)


    fun clearOldFiles(): Boolean {
        if (!clearOldFiles)
            return false


    }

    private fun setStorageDir(storageDir: File):AppLogService {
        this.storageDir = storageDir
        return this
    }

    companion object {
        private val instance = AtomicReference<AppLogService>()

        fun getInstance(storageDir: File): AppLogService {
            var appLog = instance.get()

            if (appLog == null) {
                appLog = AppLogService(storageDir)

                return if (instance.compareAndSet(null, appLog))
                    appLog
                else
                    instance.get().setStorageDir(storageDir)
            }

            return appLog.setStorageDir(storageDir)
        }
    }

    inner class LogServiceBinder : Binder() {
        fun getService(storageDir: File) = AppLogService.getInstance(storageDir)
    }
}