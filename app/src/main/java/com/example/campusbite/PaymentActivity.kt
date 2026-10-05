package com.example.campusbite

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campusbite.databinding.ActivityPaymentBinding
import kotlin.random.Random

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pickupTime = intent.getStringExtra("PICKUP_TIME") ?: "12:00 PM"
        val totalAmount = intent.getIntExtra("TOTAL_AMOUNT", 0)

        binding.tvPaymentAmount.text = "Amount to Pay: ₹$totalAmount"

        binding.btnPayNow.setOnClickListener {
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()

            val orderId = "CB-${Random.nextInt(1000, 9999)}"
            val itemSummary = CartManager.cartItems.joinToString(", ") { "${it.name} (₹${it.price})" }

            // Schedule Notifications
            NotificationHelper.createNotificationChannel(this)
            NotificationHelper.schedulePickupReminder(this, pickupTime)
            NotificationHelper.scheduleOrderReadyNotification(this)

            val intent = Intent(this, ReceiptActivity::class.java).apply {
                putExtra("ORDER_ID", orderId)
                putExtra("ITEMS", itemSummary)
                putExtra("TOTAL", totalAmount)
                putExtra("PICKUP_TIME", pickupTime)
            }
            startActivity(intent)
            finish()
        }
    }
}