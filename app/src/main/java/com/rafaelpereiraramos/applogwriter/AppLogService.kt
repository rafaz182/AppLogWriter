package com.rafaelpereiraramos.applogwriter

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.ArrayList

/**
 * Created by Rafael P. Ramos on 31/10/2018.
 */
class AppLogService private constructor(): Service() {

    private val writer = WriteTask(filesDir)
    private val binder = LogServiceBinder()

    var clearOldFiles = true

    override fun onBind(intent: Intent?): IBinder?  {
        synchronized(this) {
            clearOldFiles()

            if (!writer.isRunning) {
                writer.start()

                Executors.newSingleThreadExecutor().execute(writer)
            }
        }

        return binder
    }

    override fun onDestroy() {
        write(StopLogMessage())
    }

    fun write(logMessage: LogMessage) = writer.insert(logMessage)

    fun clearOldFiles(): Boolean {
        if (!clearOldFiles)
            return clearOldFiles

        val storageDirFiles = filesDir.listFiles()
        val today = Calendar.getInstance()
        val filesToDelete = ArrayList<File>()
        val filesDeleted = ArrayList<File>()

        today.time = Date()

        if (storageDirFiles == null)
            return false

        for (file in storageDirFiles) {
            val modifiedDate = Calendar.getInstance()

            modifiedDate.time = Date(file.lastModified())

            if (modifiedDate.before(today)) {
                modifiedDate.add(Calendar.DAY_OF_MONTH, 10)

                if (modifiedDate.before(today))
                    filesToDelete.add(file)
            }
        }

        for (file in filesToDelete)
            if (file.delete())
                filesDeleted.add(file)

        filesToDelete.removeAll(filesDeleted)

        return filesToDelete.size == 0
    }

    companion object {
        val instance = AtomicReference<AppLogService>()

        fun getInstance(): AppLogService {
            var appLog = instance.get()

            if (appLog == null) {
                appLog = AppLogService()

                return if (instance.compareAndSet(null, appLog))
                    appLog
                else
                    instance.get()
            }

            return appLog
        }
    }

    inner class LogServiceBinder : Binder() {
        fun getService() = AppLogService.getInstance()
    }
}