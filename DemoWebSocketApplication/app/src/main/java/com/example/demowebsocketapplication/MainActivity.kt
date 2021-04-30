package com.example.demowebsocketapplication

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btSocket = findViewById<Button>(R.id.bt_main_connenct_socket);
        val btSendMessage = findViewById<Button>(R.id.bt_main_send_socket);

        btSocket.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

            }
        })


        btSendMessage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

            }
        })
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName) {

        }

        override fun onServiceConnected(p0: ComponentName, p1: IBinder?) {

        }

    }

}