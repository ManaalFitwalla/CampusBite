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
                Toast.makeText(this, "Pickup time set to $formattedTime", Toast.LENGTH_SHORT).show()
            },
            hour,
            minute,
            false
        )

        timePickerDialog.setTitle("Select Pickup Time Today")
        timePickerDialog.show()

        // Explicitly color the OK and Cancel buttons after showing
        val tealColor = android.graphics.Color.parseColor("#005B5C")
        timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)?.setTextColor(tealColor)
        timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)?.setTextColor(tealColor)
    }
}