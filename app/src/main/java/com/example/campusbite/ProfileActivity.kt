package com.example.campusbite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.campusbite.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("userName", "Student") ?: "Student"

        binding.tvProfileName.text = userName

        loadActiveOrder()

        binding.cardActiveOrder.setOnClickListener {
            openActiveOrderReceipt()
        }

        binding.btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadActiveOrder() {
        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        val orderId = sharedPref.getString("last_order_id", null)

        if (orderId != null) {
            val items = sharedPref.getString("last_order_items", "No Items") ?: "No Items"
            val pickupTime = sharedPref.getString("last_order_time", "--:--") ?: "--:--"

            binding.cardActiveOrder.visibility = View.VISIBLE
            binding.tvNoActiveOrder.visibility = View.GONE
            binding.tvOrderId.text = "Order #$orderId"
            binding.tvOrderItems.text = "Items: ${items.replace("\n", ", ")}"
            binding.tvPickupTime.text = "Estimated Pickup: $pickupTime"
        } else {
            binding.cardActiveOrder.visibility = View.GONE
            binding.tvNoActiveOrder.visibility = View.VISIBLE
        }
    }

    private fun openActiveOrderReceipt() {
        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        val orderId = sharedPref.getString("last_order_id", null) ?: return
        val items = sharedPref.getString("last_order_items", "") ?: ""
        val total = sharedPref.getInt("last_order_total", 0)
        val pickupTime = sharedPref.getString("last_order_time", "") ?: ""

        val intent = Intent(this, ReceiptActivity::class.java).apply {
            putExtra("ORDER_ID", orderId)
            putExtra("ITEMS", items)
            putExtra("TOTAL", total)
            putExtra("PICKUP_TIME", pickupTime)
        }
        startActivity(intent)
    }
}