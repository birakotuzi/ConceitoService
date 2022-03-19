package br.edu.ifsp.scl.sdm.conceitoservice

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LifetimeStartedService : Service() {

    /*Contador de Segundos*/
    private var lifetime: Int = 0

    companion object {
        /* Para passar o lifetime entre Activity e Service*/
        val EXTRA_LIFETIME = "EXTRA_LIFETIME"
    }

    /*Nossa thread de trabalho que conta segundos e Background*/
    private inner class WorkerThread: Thread() {
        var running = false

        override fun run() {
            running = true
            while (running) {
                // Dorme 1 segundo
                sleep(1000)

                // Envia o lifetime para a activity
                sendBroadcast(Intent("ACTION_RECEIVE_LIFETIME").also {
                    it.putExtra(EXTRA_LIFETIME, ++lifetime)
                })
            }
        }
    }

    private lateinit var workerThread: WorkerThread

    /*Primeira função executada em qualquer Serviço*/
    override fun onCreate() {
        super.onCreate()
        workerThread = WorkerThread()
    }

    /* Só faz sentido se for Serviço vinculado, senão retornar null*/
    override fun onBind(intent: Intent): IBinder? = null
    /*override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }*/

    /*Chamado quando a Activity executa startService.
    Executa indefinidamente até que seja chamado o método stopSelf (a partir do Serviço) ou stopService(a partir da Activity)*/
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!workerThread.running) {
            workerThread.start()
        }
        return START_STICKY
    }

    /*Última função executada. Apaga a luz e fecha a porta*/
    override fun onDestroy() {
        super.onDestroy()
        workerThread.running = false
    }
}