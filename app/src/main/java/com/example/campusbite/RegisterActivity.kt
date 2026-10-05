package com.example.campusbite

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campusbite.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val degreeOptions = arrayOf("Diploma", "Degree (B.E / B.Tech)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, degreeOptions)
        binding.spDegree.adapter = adapter

        binding.btnUploadIdCard.setOnClickListener {
            Toast.makeText(this, "ID Card Selected", Toast.LENGTH_SHORT).show()
        }

        binding.btnRegister.setOnClickListener {
            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}