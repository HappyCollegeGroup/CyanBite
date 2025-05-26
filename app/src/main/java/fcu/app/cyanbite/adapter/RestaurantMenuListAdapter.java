package fcu.app.cyanbite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Food;

public class RestaurantMenuListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Food> foodList;

    public RestaurantMenuListAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            // 新增菜餚區塊
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_food_add, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_food, parent, false);
        }
        return new ViewHolder(view, viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (getItemViewType(position) == 0) {
            // 新增菜餚區塊點擊事件
            holder.itemView.setOnClickListener(view -> {
                showBottomSheet(null); // 傳 null 表示是新增
            });
        } else {
            Food food = foodList.get(position - 1); // position 要減 1
            ViewHolder vh = (ViewHolder) holder;
            vh.tvName.setText(food.getName());
            vh.tvPrice.setText("$ " + food.getPrice());
            vh.imgFood.setImageBitmap(food.getImageBitmap());

            holder.itemView.setOnClickListener(view -> {
                showBottomSheet(food); // 傳入資料做編輯
            });
        }
    }

    private void showBottomSheet(Food food) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View dialog = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_edit_menu, null);

        EditText etName = dialog.findViewById(R.id.et_food_name);
        EditText etPrice = dialog.findViewById(R.id.et_food_price);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        if (food != null) {
            // 編輯模式：填入原資料
            etName.setText(food.getName());
            etPrice.setText(String.valueOf(food.getPrice()));

            // 刪除這筆資料
            btnCancel.setText("刪除");
            btnCancel.setBackgroundColor(context.getColor(android.R.color.holo_red_light)); // 紅色按鈕
            btnCancel.setOnClickListener(v -> {
                int index = foodList.indexOf(food);
                if (index >= 0) {
                    foodList.remove(index);
                    notifyItemRemoved(index + 1); // +1 是因為第0個是新增
                }
                bottomSheetDialog.dismiss();
            });
        } else {
            // 新增模式
            btnCancel.setText("取消");
            btnCancel.setBackgroundColor(context.getColor(android.R.color.darker_gray)); // 灰色按鈕
            btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(context, "請填寫完整資訊", Toast.LENGTH_SHORT).show();
                return;
            }

            int price = Integer.parseInt(priceStr);

            if (food == null) {
                // 新增
                foodList.add(new Food(name, price, R.drawable.image)); // 預設圖片
                notifyItemInserted(foodList.size());
            } else {
                // 編輯
                food.setName(name);
                food.setPrice(price);
                notifyDataSetChanged();
            }

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(dialog);
        bottomSheetDialog.show();
    }


    public int getItemCount() {
        // +1：永遠保留第一個是「新增」
        return foodList.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvAddFood;
        ImageView imgFood;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == 0) {
                tvAddFood = itemView.findViewById(R.id.tv_add_food);
            } else {
                tvName = itemView.findViewById(R.id.tv_name);
                tvPrice = itemView.findViewById(R.id.tv_description);
                imgFood = itemView.findViewById(R.id.img_group);
            }
        }
    }

}