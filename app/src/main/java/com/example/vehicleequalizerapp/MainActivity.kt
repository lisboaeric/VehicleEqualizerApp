package com.example.vehicleequalizerapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "VehicleEqualizerUI"

    // Referência para a interface AIDL gerada para comunicação Binder (Etapa 4)
    private var equalizerService: IEqualizerService? = null
    private var isBound: Boolean = false

    // Referências aos componentes da interface
    private lateinit var equalizerSwitch: Switch
    private lateinit var bassSeekBar: SeekBar
    private lateinit var midSeekBar: SeekBar
    private lateinit var trebleSeekBar: SeekBar
    private lateinit var tvBassValue: TextView
    private lateinit var tvMidValue: TextView
    private lateinit var tvTrebleValue: TextView
    private lateinit var tvHint: TextView

    /**
     * Gerenciador de conexão com o Serviço Android vinculado (ServiceConnection)
     * Utiliza IEqualizerService.Stub.asInterface(service) para obter a ponte IPC via Binder.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // Conversão do IBinder genérico na interface formal AIDL
            equalizerService = IEqualizerService.Stub.asInterface(service)
            isBound = true
            Log.i(TAG, "===> [BINDER IPC CONNECTED] Interface IEqualizerService vinculada com sucesso!")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            equalizerService = null
            isBound = false
            Log.w(TAG, "===> [BINDER IPC DISCONNECTED] Conexão Binder com o serviço foi perdida.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa os componentes da interface pelo ID do XML
        equalizerSwitch = findViewById(R.id.equalizerSwitch)
        bassSeekBar     = findViewById(R.id.bassSeekBar)
        midSeekBar      = findViewById(R.id.midSeekBar)
        trebleSeekBar   = findViewById(R.id.trebleSeekBar)
        tvBassValue     = findViewById(R.id.tvBassValue)
        tvMidValue      = findViewById(R.id.tvMidValue)
        tvTrebleValue   = findViewById(R.id.tvTrebleValue)
        tvHint          = findViewById(R.id.tvHint)

        Log.d(TAG, "Interface iniciada. Aguardando conexão IPC Binder...")

        // Configura o Switch para habilitar/desabilitar os SeekBars e notificar o Serviço via AIDL
        equalizerSwitch.setOnCheckedChangeListener { _, isChecked ->
            setEqualizerControlsEnabled(isChecked)
            try {
                Log.d(TAG, "[IPC CALL] Enviando setEqualizerEnabled($isChecked) via Binder...")
                equalizerService?.setEqualizerEnabled(isChecked)
            } catch (e: RemoteException) {
                Log.e(TAG, "Falha na chamada IPC do equalizador", e)
            }
        }

        // Listener para o SeekBar de Graves via AIDL
        bassSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvBassValue.text = progress.toString()
                if (fromUser) {
                    try {
                        Log.d(TAG, "[IPC CALL] Enviando setBassLevel($progress) via Binder...")
                        equalizerService?.setBassLevel(progress)
                    } catch (e: RemoteException) {
                        Log.e(TAG, "Erro na comunicação Binder/AIDL para graves", e)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Listener para o SeekBar de Médios via AIDL
        midSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvMidValue.text = progress.toString()
                if (fromUser) {
                    try {
                        Log.d(TAG, "[IPC CALL] Enviando setMidLevel($progress) via Binder...")
                        equalizerService?.setMidLevel(progress)
                    } catch (e: RemoteException) {
                        Log.e(TAG, "Erro na comunicação Binder/AIDL para médios", e)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Listener para o SeekBar de Agudos via AIDL
        trebleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvTrebleValue.text = progress.toString()
                if (fromUser) {
                    try {
                        Log.d(TAG, "[IPC CALL] Enviando setTrebleLevel($progress) via Binder...")
                        equalizerService?.setTrebleLevel(progress)
                    } catch (e: RemoteException) {
                        Log.e(TAG, "Erro na comunicação Binder/AIDL para agudos", e)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onStart() {
        super.onStart()
        // Vincula a Activity ao EqualizerService utilizando Intent explícito
        val intent = Intent(this, EqualizerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d(TAG, "Chamando bindService() para estabelecer conexão IPC Binder.")
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
            Log.d(TAG, "Chamando unbindService() para encerrar conexão IPC Binder.")
        }
    }

    /**
     * Habilita ou desabilita os SeekBars do equalizador.
     */
    private fun setEqualizerControlsEnabled(enabled: Boolean) {
        bassSeekBar.isEnabled   = enabled
        midSeekBar.isEnabled    = enabled
        trebleSeekBar.isEnabled = enabled

        val alpha = if (enabled) 1.0f else 0.4f
        bassSeekBar.alpha   = alpha
        midSeekBar.alpha    = alpha
        trebleSeekBar.alpha = alpha

        tvHint.visibility = if (enabled) android.view.View.GONE else android.view.View.VISIBLE
    }
}
