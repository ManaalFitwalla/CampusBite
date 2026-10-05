package com.example.campusbite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.campusbite.databinding.ActivityReceiptBinding

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        val studentName = sharedPref.getString("userName", "Manaal") ?: "Manaal"
        val rollNo = sharedPref.getString("rollNo", "2100520") ?: "2100520"
        val enrollmentNo = sharedPref.getString("enrollmentNo", "EN2024-889") ?: "EN2024-889"

        val orderId = intent.getStringExtra("ORDER_ID") ?: "CB-${(1000..9999).random()}"
        val items = intent.getStringExtra("ITEMS") ?: "Veg Sandwich x1"
        val total = intent.getIntExtra("TOTAL", 0)
        val pickupTime = intent.getStringExtra("PICKUP_TIME") ?: "10:30 AM"

        binding.tvReceiptOrderId.text = orderId
        binding.tvStudentName.text = studentName
        binding.tvRollNo.text = rollNo
        binding.tvEnrollmentNo.text = enrollmentNo
        binding.tvReceiptItems.text = items
        binding.tvReceiptTotal.text = "₹$total"
        binding.tvReceiptPickupTime.text = pickupTime

        // Clear cart if opened from Cart flow
        CartManager.clearCart()

        binding.btnBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}