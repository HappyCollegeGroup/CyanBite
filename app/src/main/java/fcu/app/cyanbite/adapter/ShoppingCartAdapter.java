package fcu.app.cyanbite.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Order;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private final List<Order> orderList;

    public ShoppingCartAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Order order, int position);
    }

    private OnDeleteClickListener deleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvFoodName, tvGroupName, tvRestaurantName, tvFoodPrice;
        ImageButton btnDelete;

        public ViewHolder(View view) {
            super(view);
            imgFood = view.findViewById(R.id.img_food);
            tvFoodName = view.findViewById(R.id.tv_food_name);
            tvGroupName = view.findViewById(R.id.tv_group_name);
            tvRestaurantName = view.findViewById(R.id.tv_restaurant_name);
            tvFoodPrice = view.findViewById(R.id.tv_food_price);
            btnDelete = view.findViewById(R.id.btn_delete);
        }
    }

    @NonNull
    @Override
    public ShoppingCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_cart, parent, false); // 你那個 item xml 的檔名
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvFoodName.setText(order.getFood());
        holder.tvGroupName.setText(order.getGroup());
        holder.tvRestaurantName.setText(order.getRestaurant());
        holder.tvFoodPrice.setText("NT$" + order.getPrice());

        Bitmap bitmap = order.getImageBitmap();
        if (bitmap != null) {
            holder.imgFood.setImageBitmap(bitmap);
        } else {
            holder.imgFood.setImageResource(R.drawable.breakfast);
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(order, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
