package com.example.campusbite

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.campusbite.databinding.ItemFoodCardBinding

class FoodAdapter(
    private var itemList: List<FoodItem>,
    private val onAddToCartClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(val binding: ItemFoodCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.tvFoodName.text = item.name
        holder.binding.tvFoodPrice.text = "₹${item.price}"
        holder.binding.imgFood.setImageResource(item.imageResId)

        if (item.isAvailable) {
            holder.binding.tvAvailability.text = "• Available"
            holder.binding.tvAvailability.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
            holder.binding.fabAdd.isEnabled = true
        } else {
            holder.binding.tvAvailability.text = "• Out of Stock"
            holder.binding.tvAvailability.setTextColor(android.graphics.Color.parseColor("#D32F2F"))
            holder.binding.fabAdd.isEnabled = false
        }

        holder.binding.fabAdd.setOnClickListener {
            onAddToCartClick(item)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun updateList(newList: List<FoodItem>) {
        itemList = newList
        notifyDataSetChanged()
    }
}