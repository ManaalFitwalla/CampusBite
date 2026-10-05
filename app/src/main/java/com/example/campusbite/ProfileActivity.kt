package com.example.campusbite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.campusbite.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("userName", "Manaal") ?: "Manaal"

        binding.tvProfileName.text = userName

        binding.btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    // Call this helper method on your active order click listener
    private fun openActiveOrderReceipt() {
        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        val orderId = sharedPref.getString("last_order_id", "CB-2407") ?: "CB-2407"
        val items = sharedPref.getString("last_order_items", "Veg Sandwich x1\nCold Coffee x1") ?: "Veg Sandwich x1\nCold Coffee x1"
        val total = sharedPref.getInt("last_order_total", 85)
        val pickupTime = sharedPref.getString("last_order_time", "10:30 AM") ?: "10:30 AM"

        val intent = Intent(this, ReceiptActivity::class.java).apply {
            putExtra("ORDER_ID", orderId)
            putExtra("ITEMS", items)
            putExtra("TOTAL", total)
            putExtra("PICKUP_TIME", pickupTime)
        }
        startActivity(intent)
    }
}