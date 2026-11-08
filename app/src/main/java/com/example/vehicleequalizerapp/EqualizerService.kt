package com.example.vehicleequalizerapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Serviço Vinculado utilizando Binder e AIDL para Comunicação Interprocesso (IPC).
 * Implementa o Stub gerado pelo compilador a partir de IEqualizerService.aidl.
 */
class EqualizerService : Service() {

    companion object {
        private const val TAG = "EqualizerServiceIPC"
    }

    // Backend interno de processamento de áudio veicular
    private val audioSystem = VehicleAudioSystem()

    /**
     * Implementação concreta do Binder AIDL (Stub).
     * Essa classe lida com a descompactação (unmarshalling) dos dados IPC enviados via Binder.
     */
    private val binder = object : IEqualizerService.Stub() {

        override fun setEqualizerEnabled(enabled: Boolean) {
            Log.i(TAG, "[IPC/AIDL RECEIVE] setEqualizerEnabled() chamado via Binder IPC. Enabled=$enabled")
            audioSystem.setEqualizerEnabled(enabled)
        }

        override fun setBassLevel(level: Int) {
            Log.d(TAG, "[IPC/AIDL RECEIVE] setBassLevel() chamado via Binder IPC. Nível=$level dB")
            audioSystem.updateBass(level)
        }

        override fun setMidLevel(level: Int) {
            Log.d(TAG, "[IPC/AIDL RECEIVE] setMidLevel() chamado via Binder IPC. Nível=$level dB")
            audioSystem.updateMid(level)
        }

        override fun setTrebleLevel(level: Int) {
            Log.d(TAG, "[IPC/AIDL RECEIVE] setTrebleLevel() chamado via Binder IPC. Nível=$level dB")
            audioSystem.updateTreble(level)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "===> [SERVICE LIFECYCLE] onCreate: EqualizerService com suporte a AIDL/Binder iniciado.")
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.i(TAG, "===> [SERVICE LIFECYCLE] onBind: Retornando interface Binder IEqualizerService.Stub.")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.w(TAG, "===> [SERVICE LIFECYCLE] onUnbind: Cliente desconectado da interface AIDL.")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.e(TAG, "===> [SERVICE LIFECYCLE] onDestroy: EqualizerService destruído.")
        super.onDestroy()
    }
}
