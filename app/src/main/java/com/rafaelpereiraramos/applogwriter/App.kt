package com.rafaelpereiraramos.applogwriter

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import timber.log.Timber

/**
 * Created by Rafael P. Ramos on 02/11/2018.
 */
class App : Application() {

    lateinit var logService: AppLogService

    override fun onCreate() {
        super.onCreate()

        startLogService()
    }

    fun startLogService() {

        var serviceConnection = object : ServiceConnection{
            override fun onServiceDisconnected(name: ComponentName?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                if (service!! is AppLogService.LogServiceBinder) {
                    val binder =
                        service as AppLogService.LogServiceBinder

                    logService = binder.getService(applicationContext.filesDir)

                    Timber.plant(LogFileTree(logService))
                }
            }
        }

        val intent = Intent(this, AppLogService::class.java)

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}