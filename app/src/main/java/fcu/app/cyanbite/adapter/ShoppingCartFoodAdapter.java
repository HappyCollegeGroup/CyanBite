package fcu.app.cyanbite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Food;

public class ShoppingCartFoodAdapter extends RecyclerView.Adapter<ShoppingCartFoodAdapter.ViewHolder> {
    private Context context;
    private List<Food> foodList;

    public ShoppingCartFoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.tvName.setText(food.getName());
        holder.tvPrice.setText("$ " + food.getPrice());
        holder.imgFood.setImageResource(food.getImageResId());

        // 刪除按鈕直接刪除該項目
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    foodList.remove(pos);
                    notifyItemRemoved(pos);
                }
            }
        });

        // 點擊整個項目，開啟 BottomSheet 查看詳情與刪除
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                View dialog = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_check_order, null);

                ((ImageView) dialog.findViewById(R.id.iv_btm_sheet_food)).setImageResource(food.getImageResId());
                ((TextView) dialog.findViewById(R.id.tv_btm_sheet_name)).setText(food.getName());
                ((TextView) dialog.findViewById(R.id.tv_btm_sheet_price)).setText("$ " + food.getPrice());

                // BottomSheet 刪除按鈕
                Button deleteButton = dialog.findViewById(R.id.btn_btm_sheet_delete);
                if (deleteButton != null) {
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int pos = holder.getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                foodList.remove(pos);
                                notifyItemRemoved(pos);
                            }
                            bottomSheetDialog.dismiss();
                        }
                    });
                }

                // BottomSheet 取消按鈕
                Button cancelButton = dialog.findViewById(R.id.btn_btm_sheet_cancel);
                if (cancelButton != null) {
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.dismiss();
                        }
                    });
                }

                bottomSheetDialog.setContentView(dialog);
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView imgFood;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            imgFood = itemView.findViewById(R.id.img_food);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}