package fcu.app.cyanbite.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Restaurant;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    private List<Restaurant> restaurantList;
    private OnItemClickListener listener;

    public RestaurantAdapter(List<Restaurant> restaurantList, OnItemClickListener listener) {
        this.restaurantList = restaurantList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_restaurant_name);
        }

        public void bind(Restaurant restaurant, OnItemClickListener listener) {
            nameTextView.setText(restaurant.getName());
            itemView.setOnClickListener(v -> listener.onItemClick(restaurant));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(restaurantList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }
}

