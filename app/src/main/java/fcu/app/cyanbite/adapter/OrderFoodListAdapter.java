package fcu.app.cyanbite.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Group;
import fcu.app.cyanbite.model.Restaurant;

public class OrderFoodListAdapter extends RecyclerView.Adapter<OrderFoodListAdapter.ViewHolder> {
    private Context context;
    private List<Restaurant> restaurantList;
    private String group;

    public OrderFoodListAdapter(Context context, String group, List<Restaurant> restaurantList) {
        this.context = context;
        this.group = group;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = null;
        int current = 0;
        for (Restaurant currentRestaurant : restaurantList) {
            if (current <= position && current + currentRestaurant.getFoodList().size() > position) {
                restaurant = currentRestaurant;
                break;
            }
            current += currentRestaurant.getFoodList().size();
        }

        if (restaurant != null) {
            Food food = restaurant.getFoodList().get(position - current);
            holder.tvName.setText(food.getName());
            holder.tvPrice.setText("$ " + food.getPrice());
            holder.imgFood.setImageBitmap(food.getImageBitmap());

            Restaurant finalRestaurant = restaurant;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                    View dialog = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet, null);
                    ((ImageView) (dialog.findViewById(R.id.iv_btm_sheet_food))).setImageBitmap(food.getImageBitmap());
                    ((TextView) (dialog.findViewById(R.id.tv_btm_sheet_name))).setText(food.getName());
                    ((TextView) (dialog.findViewById(R.id.tv_btm_sheet_price))).setText("$ " + food.getPrice());
                    ((Button) dialog.findViewById(R.id.btn_btm_sheet_cancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.cancel();
                        }
                    });
                    ((Button) dialog.findViewById(R.id.btn_btm_sheet_order)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            Map<String, Object> order = new HashMap<>();
                            order.put("uid", user.getUid());
                            order.put("restaurant", finalRestaurant.getName());
                            order.put("food", food.getName());
                            order.put("group", group);
                            order.put("price", food.getPrice());
                            order.put("image", food.getImage());
                            order.put("time", FieldValue.serverTimestamp());

                            db.collection("order")
                                    .add(order)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(context, "訂餐成功", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "訂餐失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            bottomSheetDialog.cancel();
                        }
                    });
                    bottomSheetDialog.setContentView(dialog);
                    bottomSheetDialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int result = 0;
        for (Restaurant restaurant : restaurantList) {
            result += restaurant.getFoodList().size();
        }
        return result;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView imgFood;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_description);
            imgFood = itemView.findViewById(R.id.img_group);
        }
    }
}
