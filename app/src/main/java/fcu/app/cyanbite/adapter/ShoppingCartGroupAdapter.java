package fcu.app.cyanbite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Group;
import fcu.app.cyanbite.model.Restaurant;

public class ShoppingCartGroupAdapter extends RecyclerView.Adapter<ShoppingCartGroupAdapter.ViewHolder> {
    private Context context;
    private List<Group> groupList;

    public ShoppingCartGroupAdapter(Context context, List<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.tvGroupName.setText(group.getName());
        holder.rvFoodList.setLayoutManager(new GridLayoutManager(context, 2));
        List<Food> foodList = new ArrayList<>();
        for (Restaurant restaurant : group.getRestaurantList()) {
            foodList.addAll(restaurant.getFoodList());
        }
        ShoppingCartFoodAdapter adapter = new ShoppingCartFoodAdapter(context, foodList);
        holder.rvFoodList.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName;
        RecyclerView rvFoodList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_order_rv_group_name);
            rvFoodList = itemView.findViewById(R.id.rv_order_rv_food_list);
        }
    }
}
