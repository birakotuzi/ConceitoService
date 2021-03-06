package br.edu.ifsp.scl.sdm.conceitoservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class LifetimeBoundService : Service() {

    /*Contador de Segundos*/
    var lifetime: Int = 0
    private set

    /*Nossa thread de trabalho que conta segundos e Background*/
    private inner class WorkerThread: Thread() {
        var running = false

        override fun run() {
            running = true
            while (running) {
                // Dorme 1 segundo
                sleep(1000)
                lifetime++
            }
        }
    }

    private lateinit var workerThread: WorkerThread

    inner class LifetimeBoundServiceBinder: Binder() {
        fun getService() = this@LifetimeBoundService
    }

    private val lifetimeBoundServiceBinder = LifetimeBoundServiceBinder()

    override fun onCreate() {
        super.onCreate()
        workerThread = WorkerThread()
    }

    override fun onDestroy() {
        super.onDestroy()
        workerThread.running = false
    }

    override fun onBind(intent: Intent): IBinder {
        if (!workerThread.running) {
            workerThread.start()
        }
        return lifetimeBoundServiceBinder
    }
}