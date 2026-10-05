package com.example.campusbite

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusbite.databinding.ActivityCartBinding
import java.util.Calendar

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateTotal()

        binding.btnOrderNow.setOnClickListener {
            if (CartManager.cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            } else {
                showTimePickerDialog()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(CartManager.cartItems) {
            updateTotal()
        }
        binding.rvCartItems.adapter = adapter
    }

    private fun updateTotal() {
        val total = CartManager.getTotal()
        binding.tvCartTotal.text = "₹$total"
        if (CartManager.cartItems.isEmpty()) {
            binding.tvEmptyCart.visibility = View.VISIBLE
            binding.btnOrderNow.isEnabled = false
        } else {
            binding.tvEmptyCart.visibility = View.GONE
            binding.btnOrderNow.isEnabled = true
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            R.style.GreenTimePickerTheme,
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

                val itemsSummary = CartManager.cartItems.joinToString("\n") { item ->
                    "${item.name} x1"
                }
                val totalAmount = CartManager.getTotal()
                val randomOrderId = "CB-${(1000..9999).random()}"

                // Save latest active order in SharedPreferences for dynamic staff retrieval and student notifications
                val sharedPref = getSharedPreferences("CampusBitePrefs", MODE_PRIVATE)
                sharedPref.edit().apply {
                    putBoolean("has_active_order", true)
                    putString("last_order_id", randomOrderId)
                    putString("last_order_items", itemsSummary.replace("\n", ", "))
                    putInt("last_order_total", totalAmount)
                    putString("last_order_time", formattedTime)
                    putString("last_order_status", "Preparing")
                    putBoolean("status_notified", false)
                    apply()
                }

                // Launch Receipt screen
                val intent = Intent(this, ReceiptActivity::class.java).apply {
                    putExtra("ORDER_ID", randomOrderId)
                    putExtra("ITEMS", itemsSummary)
                    putExtra("TOTAL", totalAmount)
                    putExtra("PICKUP_TIME", formattedTime)
                }
                startActivity(intent)
                finish()
            },
            hour,
            minute,
            false
        )

        timePickerDialog.setTitle("Select Pickup Time Today")
        timePickerDialog.show()

        val tealColor = android.graphics.Color.parseColor("#005B5C")
        timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)?.setTextColor(tealColor)
        timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)?.setTextColor(tealColor)
    }
}