package fcu.app.cyanbite.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.BuyerOrder; // This is the correct model for the adapter
// import fcu.app.cyanbite.model.Order; // This import is not needed in BuyerOrderAdapter

public class BuyerOrderAdapter extends RecyclerView.Adapter<BuyerOrderAdapter.ViewHolder> {

    // CORRECTED: The list should be of BuyerOrder objects
    private List<BuyerOrder> buyerOrders;

    /**
     * Constructor for the BuyerOrderAdapter.
     * @param buyerOrders The list of BuyerOrder objects to display.
     */
    public BuyerOrderAdapter(List<BuyerOrder> buyerOrders) {
        this.buyerOrders = buyerOrders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single buyer order item (item_buyer_order.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buyer_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // CORRECTED: Now 'buyerOrder' correctly gets a BuyerOrder object from the list
        BuyerOrder buyerOrder = buyerOrders.get(position);

        // Set buyer's name
        holder.tvBuyerName.setText("購買人: " + buyerOrder.getBuyerName());

        // Set the total price, formatted to two decimal places
        holder.tvTotalPrice.setText("總價: $" + String.format("%.2f", buyerOrder.getTotalPrice()));

        // --- Setup for the nested RecyclerView (Ordered Items List) ---
        // Create an instance of OrderItemAdapter for the current buyer's list of items
        // You'll need to ensure OrderItemAdapter exists and works with List<OrderItem>
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(buyerOrder.getItems());

        // Set a LinearLayoutManager for the nested RecyclerView
        // This makes the items appear in a vertical list within the buyer's order card
        holder.rvOrderItemsList.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        // Set the OrderItemAdapter to the nested RecyclerView
        holder.rvOrderItemsList.setAdapter(orderItemAdapter);
    }

    @Override
    public int getItemCount() {
        // Return the total number of BuyerOrder objects in the list
        return buyerOrders.size();
    }

    /**
     * ViewHolder class to hold references to the views in item_buyer_order.xml.
     * This improves performance by recycling views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBuyerName;
        TextView tvTotalPrice;
        RecyclerView rvOrderItemsList; // The RecyclerView inside this item to show food items

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link the UI elements from item_buyer_order.xml to Java variables
            tvBuyerName = itemView.findViewById(R.id.tv_buyer_name);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            rvOrderItemsList = itemView.findViewById(R.id.rv_order_items_list);
        }
    }
}