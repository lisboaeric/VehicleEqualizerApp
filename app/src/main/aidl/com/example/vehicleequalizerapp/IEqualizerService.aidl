// IEqualizerService.aidl
package com.example.vehicleequalizerapp;

/**
 * Interface AIDL para Comunicação Interprocesso (IPC) via Binder.
 * Permite que clientes (como a MainActivity ou outros módulos automotivos)
 * enviem comandos padronizados para o serviço de áudio em segundo plano.
 */
interface IEqualizerService {
    /**
     * Ativa ou desativa o equalizador do DSP.
     */
    void setEqualizerEnabled(boolean enabled);

    /**
     * Define o nível de graves (0 a 100).
     */
    void setBassLevel(int level);

    /**
     * Define o nível de médios (0 a 100).
     */
    void setMidLevel(int level);

    /**
     * Define o nível de agudos (0 a 100).
     */
    void setTrebleLevel(int level);
}
