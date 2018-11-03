package com.rafaelpereiraramos.applogwriter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    lateinit var thread1: Thread
    //var executor1 = Executors.newSingleThreadExecutor()
    lateinit var thread2: Thread

    private var isRunnig = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        thread1 = Thread({
            var counter = 0
            while (isRunnig) Timber.tag("TestTrd1").i(counter.inc().toString())
        },"NameOfThread1")
        thread2 = Thread({
            var counter = 0
            while (isRunnig) Timber.tag("TestTrd2").i(counter.inc().toString())
        },"NameOfThread2")

        setEvent()
    }

    private fun setEvent() {
        btn_thread1.setOnClickListener {
            thread1.start()
        }

        btn_tread2.setOnClickListener {
            thread2.start()
        }

        btn_stop.setOnClickListener {
            isRunnig = false
            thread1.join(1)
            thread2.join(1)
        }
    }
}
