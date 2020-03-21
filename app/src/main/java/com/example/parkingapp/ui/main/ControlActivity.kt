package com.example.parkingapp.ui.main

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import java.util.*
import android.bluetooth.BluetoothSocket
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parkingapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View


class ControlActivity: AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.parkingapp.R.layout.control_layout)
        m_address = intent.getStringExtra(BluetoothActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute()

        var mega_buffer = ""
        var datos=""

        Thread(Runnable {
            while(true){
                while(m_isConnected) {
                    var buffer = ByteArray(256)
                    val bytes = m_bluetoothSocket!!.inputStream.read(buffer)
                    datos += String(buffer, 0, bytes)
                    val datos_temp = datos
                    //Toast.makeText(this, datos, Toast.LENGTH_SHORT).show()
                    Log.d("---------BLUETOOTHLOG--------",datos)
                    if(datos.isNotEmpty()) {
                        runOnUiThread {
                            val str = this.log_arduino.text
                            this.log_arduino.setText(
                                "$str\n" + "ARDUINO: $datos_temp"
                            )
                            scroll.post {
                                scroll.fullScroll(View.FOCUS_DOWN)
                            }
                        }

                        val cambios = datos.split(";")
                        if(cambios.size>1) {
                            datos = ""
                            for (cambio in cambios) {
                                val info = cambio.split('-')
                                if (info.size == 3 && (info[2].equals("false") || info[2].equals("true"))) {
                                    FirebaseFirestore.getInstance()
                                        .collection("Parqueos")
                                        .document("00${info[1]}-${info[0]}")
                                        .update("disponible", info[2].toBoolean())
                                }else{
                                    datos+=cambio
                                }
                            }
                        }
                    }
                    Thread.sleep(500)
                }
            }
        }).start()

        control_led_on.setOnClickListener { sendCommand("a") }
        control_led_off.setOnClickListener {
            sendCommand("b")
            //var datos:ByteArray?  = null
            /*
            var buffer = ByteArray(256)
            val bytes = m_bluetoothSocket!!.inputStream.read(buffer)
            val datos = String(buffer, 0, bytes)
            Toast.makeText(this, datos,Toast.LENGTH_SHORT).show()

             */
            //Thread.sleep(5000L)
        }
        control_led_disconnect.setOnClickListener { disconnect() }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        /*
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

         */
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true
            }
            m_progress.dismiss()
        }
    }
}
