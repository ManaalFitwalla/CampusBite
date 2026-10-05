package com.example.campusbite

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusbite.databinding.ActivityVendorDashboardBinding

class VendorDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVendorDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabs()
        loadLiveOrders()
        loadMenuManagement()

        binding.btnStaffLogout.setOnClickListener {
            val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
            sharedPref.edit().putBoolean("isLoggedIn", false).apply()
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupTabs() {
        binding.btnTabLiveOrders.setOnClickListener {
            binding.btnTabLiveOrders.setBackgroundColor(Color.parseColor("#005B5C"))
            binding.btnTabLiveOrders.setTextColor(Color.WHITE)

            binding.btnTabMenuStock.setBackgroundColor(Color.parseColor("#A3D9A5"))
            binding.btnTabMenuStock.setTextColor(Color.WHITE)

            binding.containerLiveOrders.visibility = View.VISIBLE
            binding.containerMenuStock.visibility = View.GONE
        }

        binding.btnTabMenuStock.setOnClickListener {
            binding.btnTabMenuStock.setBackgroundColor(Color.parseColor("#005B5C"))
            binding.btnTabMenuStock.setTextColor(Color.WHITE)

            binding.btnTabLiveOrders.setBackgroundColor(Color.parseColor("#A3D9A5"))
            binding.btnTabLiveOrders.setTextColor(Color.WHITE)

            binding.containerLiveOrders.visibility = View.GONE
            binding.containerMenuStock.visibility = View.VISIBLE
        }
    }

    private fun loadLiveOrders() {
        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)
        val orderId = sharedPref.getString("last_order_id", "CB-3884") ?: "CB-3884"
        val items = sharedPref.getString("last_order_items", "Sandwich x1, Coffee x1") ?: "Sandwich x1, Coffee x1"
        val studentName = sharedPref.getString("userName", "Fitwalla Manaal Abdul Hafeez") ?: "Fitwalla Manaal Abdul Hafeez"
        val pickupTime = sharedPref.getString("last_order_time", "15:00") ?: "15:00"
        val currentStatus = sharedPref.getString("last_order_status", "Preparing") ?: "Preparing"

        val ordersList = mutableListOf(
            StaffOrder(orderId, studentName, items, pickupTime, currentStatus)
        )

        binding.rvStaffOrders.layoutManager = LinearLayoutManager(this)
        binding.rvStaffOrders.adapter = StaffOrdersAdapter(ordersList) { updatedOrder ->
            sharedPref.edit().putString("last_order_status", updatedOrder.status).apply()
            Toast.makeText(this, "Order #${updatedOrder.id} status updated to: ${updatedOrder.status}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMenuManagement() {
        val sharedPref = getSharedPreferences("CampusBitePrefs", Context.MODE_PRIVATE)

        val defaultItems = listOf(
            CanteenMenuItem("Biryani", 90, true),
            CanteenMenuItem("Burger", 50, true),
            CanteenMenuItem("Coffee", 25, true),
            CanteenMenuItem("Fried Rice", 70, true),
            CanteenMenuItem("Fries", 50, true),
            CanteenMenuItem("Noodles", 60, true),
            CanteenMenuItem("Samosa", 25, true),
            CanteenMenuItem("Sandwich", 40, true),
            CanteenMenuItem("Wrap", 50, true),
            CanteenMenuItem("Sprite", 20, true),
            CanteenMenuItem("Thali", 80, true),
            CanteenMenuItem("Thumbs Up", 20, true),
            CanteenMenuItem("Zeera Soda", 15, true)
        )

        val menuList = defaultItems.map { defaultItem ->
            val savedPrice = sharedPref.getInt("menu_item_price_${defaultItem.name}", defaultItem.price)
            val savedStock = sharedPref.getBoolean("menu_item_stock_${defaultItem.name}", defaultItem.inStock)
            CanteenMenuItem(defaultItem.name, savedPrice, savedStock)
        }.toMutableList()

        binding.rvStaffMenu.layoutManager = LinearLayoutManager(this)
        binding.rvStaffMenu.adapter = StaffMenuAdapter(menuList, this) { updatedItem ->
            sharedPref.edit()
                .putInt("menu_item_price_${updatedItem.name}", updatedItem.price)
                .putBoolean("menu_item_stock_${updatedItem.name}", updatedItem.inStock)
                .apply()
        }
    }
}

data class StaffOrder(val id: String, val studentName: String, val items: String, val pickupSlot: String, var status: String)
data class CanteenMenuItem(val name: String, var price: Int, var inStock: Boolean)

class StaffOrdersAdapter(
    private val orders: List<StaffOrder>,
    private val onStatusChanged: (StaffOrder) -> Unit
) : RecyclerView.Adapter<StaffOrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvOrderItems: TextView = view.findViewById(R.id.tvOrderItems)
        val tvTimeSlot: TextView = view.findViewById(R.id.tvTimeSlot)
        val btnUpdateStatus: Button = view.findViewById(R.id.btnUpdateStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_staff_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "Order #${order.id}"
        holder.tvOrderStatus.text = order.status
        holder.tvStudentName.text = "Student: ${order.studentName}"
        holder.tvOrderItems.text = "Items: ${order.items}"
        holder.tvTimeSlot.text = "Pickup Slot: ${order.pickupSlot}"

        when (order.status) {
            "Preparing" -> {
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#FFF3E0"))
                holder.tvOrderStatus.setTextColor(Color.parseColor("#E65100"))
            }
            "Ready for Pickup" -> {
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#E8F5E9"))
                holder.tvOrderStatus.setTextColor(Color.parseColor("#2E7D32"))
            }
            else -> {
                holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#E0F2FE"))
                holder.tvOrderStatus.setTextColor(Color.parseColor("#0284C7"))
            }
        }

        holder.btnUpdateStatus.setOnClickListener {
            val context = holder.itemView.context
            val options = arrayOf("🟡 Preparing", "🟢 Ready for Pickup", "✅ Completed")
            AlertDialog.Builder(context)
                .setTitle("Update Order Status")
                .setItems(options) { _, which ->
                    order.status = options[which].replace("🟡 ", "").replace("🟢 ", "").replace("✅ ", "")
                    onStatusChanged(order)
                    notifyItemChanged(position)
                }
                .show()
        }
    }

    override fun getItemCount() = orders.size
}

class StaffMenuAdapter(
    private val menuItems: List<CanteenMenuItem>,
    private val context: Context,
    private val onItemUpdated: (CanteenMenuItem) -> Unit
) : RecyclerView.Adapter<StaffMenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMenuItemName: TextView = view.findViewById(R.id.tvMenuItemName)
        val tvMenuItemPrice: TextView = view.findViewById(R.id.tvMenuItemPrice)
        val tvStockBadge: TextView = view.findViewById(R.id.tvStockBadge)
        val btnEditPrice: Button = view.findViewById(R.id.btnEditPrice)
        val btnToggleStock: Button = view.findViewById(R.id.btnToggleStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_staff_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menuItems[position]

        holder.tvMenuItemName.text = item.name
        holder.tvMenuItemPrice.text = "₹${item.price}"

        if (item.inStock) {
            holder.tvStockBadge.text = "IN STOCK"
            holder.tvStockBadge.setBackgroundColor(Color.parseColor("#E8F5E9"))
            holder.tvStockBadge.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            holder.tvStockBadge.text = "OUT OF STOCK"
            holder.tvStockBadge.setBackgroundColor(Color.parseColor("#FFEBEE"))
            holder.tvStockBadge.setTextColor(Color.parseColor("#C62828"))
        }

        holder.btnToggleStock.setOnClickListener {
            item.inStock = !item.inStock
            onItemUpdated(item)
            notifyItemChanged(position)
        }

        holder.btnEditPrice.setOnClickListener {
            showPriceEditDialog(item, position)
        }
    }

    private fun showPriceEditDialog(item: CanteenMenuItem, position: Int) {
        val input = EditText(context)
        input.setText(item.price.toString())
        input.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(context)
            .setTitle("Edit Price (${item.name})")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newPrice = input.text.toString().toIntOrNull()
                if (newPrice != null) {
                    item.price = newPrice
                    onItemUpdated(item)
                    notifyItemChanged(position)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun getItemCount() = menuItems.size
}