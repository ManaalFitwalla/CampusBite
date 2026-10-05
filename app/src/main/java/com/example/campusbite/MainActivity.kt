package com.example.campusbite

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.campusbite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var foodAdapter: FoodAdapter
    private val allFoodItems = ArrayList<FoodItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handles top status bar and bottom navigation bar system paddings dynamically
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        val rawName = sharedPref.getString("userName", "Manaal") ?: "Manaal"
        val displayName = extractMainName(rawName)
        binding.tvGreeting.text = "Hello, $displayName"

        setupFoodList()

        binding.rvFoodItems.layoutManager = GridLayoutManager(this, 2)
        foodAdapter = FoodAdapter(allFoodItems) { item ->
            CartManager.addItem(item)
            updateCartBadge()
            Toast.makeText(this, "${item.name} added to cart!", Toast.LENGTH_SHORT).show()
        }
        binding.rvFoodItems.adapter = foodAdapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterSearch(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnFilterAll.setOnClickListener {
            setActiveFilter(binding.btnFilterAll)
            filterCategory("All")
        }
        binding.btnFilterSpecial.setOnClickListener {
            setActiveFilter(binding.btnFilterSpecial)
            filterCategory("Today's Special")
        }
        binding.btnFilterLunch.setOnClickListener {
            setActiveFilter(binding.btnFilterLunch)
            filterCategory("Lunch")
        }
        binding.btnFilterSnacks.setOnClickListener {
            setActiveFilter(binding.btnFilterSnacks)
            filterCategory("Snacks")
        }
        binding.btnFilterDrinks.setOnClickListener {
            setActiveFilter(binding.btnFilterDrinks)
            filterCategory("Drinks")
        }

        binding.navHome.setOnClickListener {
            Toast.makeText(this, "You are on Home Screen", Toast.LENGTH_SHORT).show()
        }
        binding.navCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    private fun updateCartBadge() {
        val cartCount = CartManager.cartItems.size
        // Safe check for TextView vs Navigation menu item
        val navCartView = binding.navCart
        if (navCartView is TextView) {
            navCartView.text = "🛒\nCart ($cartCount)"
        }
    }

    private fun extractMainName(fullName: String): String {
        val parts = fullName.trim().split("\\s+".toRegex())
        return when {
            parts.size >= 2 -> parts[1]
            parts.isNotEmpty() -> parts[0]
            else -> "Manaal"
        }
    }

    private fun setActiveFilter(selectedButton: TextView) {
        val buttons = listOf(
            binding.btnFilterAll,
            binding.btnFilterSpecial,
            binding.btnFilterLunch,
            binding.btnFilterSnacks,
            binding.btnFilterDrinks
        )

        for (btn in buttons) {
            if (btn == selectedButton) {
                btn.setBackgroundResource(R.drawable.bg_chip_teal)
                btn.setTextColor(Color.WHITE)
            } else {
                btn.setBackgroundResource(R.drawable.bg_chip_outline)
                btn.setTextColor(Color.parseColor("#222222"))
            }
        }
    }

    private fun setupFoodList() {
        allFoodItems.clear()
        allFoodItems.add(FoodItem(1, "Cold Coffee", 40, true, "Drinks", R.drawable.ic_coffee))
        allFoodItems.add(FoodItem(2, "Veg Sandwich", 45, true, "Today's Special", R.drawable.ic_sandwich))
        allFoodItems.add(FoodItem(3, "Veg Burger", 50, true, "Snacks", R.drawable.ic_burger))
        allFoodItems.add(FoodItem(4, "Paneer Wrap", 60, true, "Snacks", R.drawable.ic_wrap))
        allFoodItems.add(FoodItem(5, "Hakka Noodles", 70, true, "Lunch", R.drawable.ic_noodles))
        allFoodItems.add(FoodItem(6, "Veg Thali", 80, true, "Today's Special", R.drawable.ic_thali))
        allFoodItems.add(FoodItem(7, "Fried Rice", 65, true, "Lunch", R.drawable.ic_fried_rice))
        allFoodItems.add(FoodItem(8, "Veg Biryani", 90, false, "Lunch", R.drawable.ic_biryani))
        allFoodItems.add(FoodItem(9, "Samosa (2 pcs)", 25, true, "Snacks", R.drawable.ic_samosa))
        allFoodItems.add(FoodItem(10, "French Fries", 50, true, "Snacks", R.drawable.ic_fries))
        allFoodItems.add(FoodItem(11, "Thums Up", 20, true, "Drinks", R.drawable.ic_thumbs_up))
        allFoodItems.add(FoodItem(12, "Zeera Soda", 20, true, "Drinks", R.drawable.ic_zeera_soda))
        allFoodItems.add(FoodItem(13, "Sprite", 20, false, "Drinks", R.drawable.ic_sprite))
    }

    private fun filterCategory(category: String) {
        if (category == "All") {
            foodAdapter.updateList(allFoodItems)
        } else {
            val filtered = allFoodItems.filter { it.category == category }
            foodAdapter.updateList(filtered)
        }
    }

    private fun filterSearch(query: String) {
        val filtered = allFoodItems.filter {
            it.name.contains(query, ignoreCase = true)
        }
        foodAdapter.updateList(filtered)
    }
}