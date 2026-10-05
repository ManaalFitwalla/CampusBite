package com.example.campusbite

object CartManager {
    val cartItems = ArrayList<FoodItem>()

    fun addItem(item: FoodItem) {
        cartItems.add(item)
    }

    fun removeItem(item: FoodItem) {
        cartItems.remove(item)
    }

    fun getTotal(): Int {
        return cartItems.sumOf { it.price }
    }

    fun clearCart() {
        cartItems.clear()
    }
}