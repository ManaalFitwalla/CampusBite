package com.example.campusbite

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

        val orderId = intent.getStringExtra("ORDER_ID") ?: "#CB-0000"
        val items = intent.getStringExtra("ITEMS") ?: ""
        val total = intent.getIntExtra("TOTAL", 0)
        val pickupTime = intent.getStringExtra("PICKUP_TIME") ?: ""

        binding.tvReceiptOrderId.text = "Order ID: $orderId"
        binding.tvReceiptItems.text = "Items: $items"
        binding.tvReceiptTotal.text = "Total Amount: ₹$total"
        binding.tvReceiptPickupTime.text = "Pickup Time Today: $pickupTime"

        CartManager.clearCart()

        binding.btnBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}