package fcu.app.cyanbite.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.OrderItem; // Make sure this import is correct

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private List<OrderItem> orderItems;

    /**
     * Constructor for the OrderItemAdapter.
     * @param orderItems The list of OrderItem objects to display.
     */
    public OrderItemAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single food item within a buyer's order (item_food_in_order.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_in_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        // Set food name and quantity
        holder.tvFoodNameQuantity.setText(item.getFoodName() + " x " + item.getQuantity());

        // Set food price, formatted to two decimal places
        holder.tvFoodPrice.setText("$" + String.format("%.2f", item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    /**
     * ViewHolder class to hold references to the views in item_food_in_order.xml.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodNameQuantity;
        TextView tvFoodPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link the UI elements from item_food_in_order.xml to Java variables
            tvFoodNameQuantity = itemView.findViewById(R.id.tv_food_name_quantity);
            tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
        }
    }
}