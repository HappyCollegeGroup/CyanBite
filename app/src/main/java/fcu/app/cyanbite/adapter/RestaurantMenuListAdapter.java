package fcu.app.cyanbite.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.ui.ImageSelectListener;

public class RestaurantMenuListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Food> foodList;
    private ImageSelectListener imageSelectListener;
    private BottomSheetDialog currentBottomSheetDialog = null;
    private ImageButton currentImageButton = null;
    private Food currentEditingFood = null;

    private String currentbase64;
    private Bitmap currentbitmap;

    private boolean add;

    public RestaurantMenuListAdapter(Context context, List<Food> foodList, ImageSelectListener listener, boolean add) {
        this.context = context;
        this.foodList = foodList;
        this.imageSelectListener = listener;
        this.add = add;
    }


    public int getItemViewType(int position) {
        return (add && position == 0) ? 0 : 1;
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
            Food food = foodList.get(position - (add ? 1 : 0)); // position 要減 1
            ViewHolder vh = (ViewHolder) holder;
            vh.tvName.setText(food.getName());
            vh.tvPrice.setText("$ " + food.getPrice());
            if (food.getImageBitmap() != null){
                vh.imgFood.setImageBitmap(food.getImageBitmap());
            }

            holder.itemView.setOnClickListener(view -> {
                showBottomSheet(food); // 傳入資料做編輯
            });
        }
    }

    private void showBottomSheet(Food food) {
        currentBottomSheetDialog = new BottomSheetDialog(context);
        View dialog = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_edit_menu, null);

        EditText etName = dialog.findViewById(R.id.et_food_name);
        EditText etPrice = dialog.findViewById(R.id.et_food_price);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        ImageButton imgbtn = dialog.findViewById(R.id.img_btn_food);

        currentImageButton = imgbtn;
        currentEditingFood = food;

        if (food != null) {
            etName.setText(food.getName());
            etPrice.setText(String.valueOf(food.getPrice()));
            imgbtn.setImageBitmap(food.getImageBitmap());

            btnCancel.setText("刪除");
//            btnCancel.setBackgroundColor(context.getColor(android.R.color.holo_red_light));
            btnCancel.setOnClickListener(v -> {
                int index = foodList.indexOf(food);
                if (index >= 0) {
                    foodList.remove(index);
                    notifyItemRemoved(index + 1);
                }
                currentBottomSheetDialog.dismiss();
                clearCurrentEditing();
            });
        } else {
            btnCancel.setText("取消");
//            btnCancel.setBackgroundColor(context.getColor(android.R.color.darker_gray));
            btnCancel.setOnClickListener(v -> {
                currentBottomSheetDialog.dismiss();
                clearCurrentEditing();
            });
        }

        imgbtn.setOnClickListener(v -> {
            imageSelectListener.onSelectImageRequested(food, imgbtn);
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(context, "請填寫完整資訊", Toast.LENGTH_SHORT).show();
                return;
            }

            Long price = Long.parseLong(priceStr);

            if (food == null) {
                // 新增
                foodList.add(new Food(name, price, currentbase64));
                notifyItemInserted(foodList.size());
            } else {
                // 編輯
                food.setName(name);
                food.setPrice(price);
                food.setImage(currentbase64);
                notifyDataSetChanged();
            }

            currentBottomSheetDialog.dismiss();
            clearCurrentEditing();
        });

        currentBottomSheetDialog.setContentView(dialog);
        currentBottomSheetDialog.show();
    }

    private void clearCurrentEditing() {
        currentBottomSheetDialog = null;
        currentImageButton = null;
        currentEditingFood = null;
    }

    public void updateFoodImage(Food food, String base64Image) {

        currentbase64 = base64Image;

        if (currentImageButton == null) {
            Toast.makeText(context, "currentImageButton is null，無法設定圖片", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = decodeBase64ToBitmap(base64Image);
        if (bitmap == null) {
            Toast.makeText(context, "decodeBase64ToBitmap 回傳 null", Toast.LENGTH_SHORT).show();
            return;
        }

        currentbitmap = bitmap;
        currentImageButton.setImageBitmap(bitmap);
        Toast.makeText(context, "圖片已成功設定", Toast.LENGTH_SHORT).show();
    }

    private Bitmap decodeBase64ToBitmap(String base64Str) {
        if (base64Str == null) return null;

        // 去除 data:image/jpeg;base64, 的前綴
        if (base64Str.contains(",")) {
            base64Str = base64Str.substring(base64Str.indexOf(",") + 1);
        }

        try {
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }



    public int getItemCount() {
        // +1：永遠保留第一個是「新增」
        return foodList.size() + (add ? 1 : 0);
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