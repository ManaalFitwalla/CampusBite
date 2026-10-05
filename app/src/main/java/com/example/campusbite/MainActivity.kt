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
    private var currentCategory = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            if (item.isAvailable) {
                CartManager.addItem(item)
                updateCartBadge()
                Toast.makeText(this, "${item.name} added to cart!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "${item.name} is currently Out of Stock!", Toast.LENGTH_SHORT).show()
            }
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
        setupFoodList()
        filterCategory(currentCategory)
    }

    private fun updateCartBadge() {
        val cartCount = CartManager.cartItems.size
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
        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        allFoodItems.clear()

        // List of all 13 items with categories (supports items in multiple categories via comma separation)
        val rawItems = listOf(
            Triple("Biryani", 90 to "Lunch, Today's Special", R.drawable.ic_biryani),
            Triple("Burger", 50 to "Snacks", R.drawable.ic_burger),
            Triple("Coffee", 25 to "Drinks", R.drawable.ic_coffee),
            Triple("Fried Rice", 70 to "Lunch", R.drawable.ic_fried_rice),
            Triple("Fries", 50 to "Snacks", R.drawable.ic_fries),
            Triple("Noodles", 60 to "Lunch", R.drawable.ic_noodles),
            Triple("Samosa", 25 to "Snacks", R.drawable.ic_samosa),
            Triple("Sandwich", 40 to "Snacks, Today's Special", R.drawable.ic_sandwich),
            Triple("Wrap", 50 to "Snacks", R.drawable.ic_wrap),
            Triple("Sprite", 20 to "Drinks", R.drawable.ic_sprite),
            Triple("Thali", 80 to "Lunch, Today's Special", R.drawable.ic_thali),
            Triple("Thumbs Up", 20 to "Drinks", R.drawable.ic_thumbs_up),
            Triple("Zeera Soda", 15 to "Drinks", R.drawable.ic_zeera_soda)
        )

        var idCounter = 1
        for ((name, info, drawable) in rawItems) {
            val (defaultPrice, categories) = info

            val currentPrice = sharedPref.getInt("menu_item_price_$name", defaultPrice)
            val isAvailable = sharedPref.getBoolean("menu_item_stock_$name", true)

            allFoodItems.add(
                FoodItem(
                    id = idCounter++,
                    name = name,
                    price = currentPrice,
                    isAvailable = isAvailable,
                    category = categories,
                    imageResId = drawable
                )
            )
        }
    }

    private fun filterCategory(category: String) {
        currentCategory = category
        if (category == "All") {
            foodAdapter.updateList(allFoodItems)
        } else {
            val filtered = allFoodItems.filter { it.category.contains(category) }
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