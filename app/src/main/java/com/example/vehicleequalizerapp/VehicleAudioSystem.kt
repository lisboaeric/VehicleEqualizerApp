package com.example.vehicleequalizerapp

import android.util.Log

/**
 * Backend Simulado do Sistema de Áudio Veicular.
 * Representa a camada de processamento de áudio embarcada no veículo e
 * a comunicação com o hardware/barramento de dados (rede CAN).
 */
class VehicleAudioSystem {

    companion object {
        private const val TAG = "VehicleAudioBackend"
        private const val TAG_CAN = "VehicleCANBus"
    }

    // Estado interno do equalizador (ligado/desligado)
    var isEnabled: Boolean = false
        private set

    // Níveis dos canais de equalização (0 a 100)
    var bassLevel: Int = 50
        private set

    var midLevel: Int = 50
        private set

    var trebleLevel: Int = 50
        private set

    // Nível atual de volume do veículo recebido via CAN (0 a 100)
    var currentVolume: Int = 65
        private set

    init {
        Log.i(TAG, "Módulo de Processamento de Áudio Veicular inicializado com sucesso.")
        // Simulação inicial de leitura do barramento CAN
        simulateIncomingCanVolume(currentVolume)
    }

    /**
     * Define o estado de ativação do equalizador e comunica ao hardware.
     */
    fun setEqualizerEnabled(enabled: Boolean) {
        isEnabled = enabled
        Log.i(TAG, "[DSP Hardware] Estado do Equalizador alterado -> Ativo: $isEnabled")
        applyEqualizerSettings()
    }

    /**
     * Atualiza o nível de graves (Bass) do DSP.
     */
    fun updateBass(level: Int) {
        bassLevel = level
        Log.d(TAG, "[DSP Hardware] Nível de Graves (Bass) atualizado: $bassLevel/100")
        applyEqualizerSettings()
    }

    /**
     * Atualiza o nível de médios (Mid) do DSP.
     */
    fun updateMid(level: Int) {
        midLevel = level
        Log.d(TAG, "[DSP Hardware] Nível de Médios (Mid) atualizado: $midLevel/100")
        applyEqualizerSettings()
    }

    /**
     * Atualiza o nível de agudos (Treble) do DSP.
     */
    fun updateTreble(level: Int) {
        trebleLevel = level
        Log.d(TAG, "[DSP Hardware] Nível de Agudos (Treble) atualizado: $trebleLevel/100")
        applyEqualizerSettings()
    }

    /**
     * Simula a aplicação em tempo real das curvas de equalização no processador de sinal digital (DSP).
     */
    fun applyEqualizerSettings() {
        if (isEnabled) {
            Log.i(
                TAG,
                "==> [DSP APLICADO] Curva ativa: Graves=$bassLevel dB | Médios=$midLevel dB | Agudos=$trebleLevel dB | Vol=$currentVolume"
            )
        } else {
            Log.w(TAG, "==> [DSP BYPASS] Equalizador inativo. Sinal de áudio em bypass linear.")
        }
    }

    /**
     * Simula o recebimento de mensagem através do barramento automotivo CAN (Controller Area Network),
     * por exemplo, comandos originados do controle de volante (Steering Wheel Controls).
     */
    fun simulateIncomingCanVolume(newVolume: Int) {
        currentVolume = newVolume
        Log.i(
            TAG_CAN,
            "[CAN RX] ID: 0x2A4 (FRAME_AUDIO_VOLUME) -> Dados recebidos: Volume=$currentVolume% | Status: OK"
        )
    }
}
