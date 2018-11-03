package com.rafaelpereiraramos.applogwriter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    var executor1 = Executors.newCachedThreadPool()
    lateinit var thread2: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //thread1 = Thread("NameOfThread1")
        thread2 = Thread("NameOfThread2")

        setEvent()
    }

    private fun setEvent() {
        btn_thread1.setOnClickListener {
            executor1.execute {
                var counter = 0
                while (true) Timber.tag("TestTrd1").i(counter.inc().toString()) }
        }

        btn_stop.setOnClickListener {
            executor1.shutdown()
            executor1.shutdownNow()
        }
    }
}
