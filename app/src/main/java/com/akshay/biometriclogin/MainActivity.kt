package com.akshay.biometriclogin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.biometriclogin.BiometricLogin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        BiometricLogin.basicLogin(this, "Login","test", onSuccess = {
            Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()
        }, onError= { error -> Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()})
    }
}