package fcu.app.cyanbite.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.RestaurantMenuListAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantManageMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantManageMenuFragment extends Fragment implements ImageSelectListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnTabSwitchListener callback;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int PICK_IMAGE_RESTAURANT = 1001;
    private static final int PICK_IMAGE_MENU_ITEM = 1002;
    private RecyclerView recyclerView;
    private RestaurantMenuListAdapter adapter;
    private List<Food> foodList;
    Restaurant restaurant;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Food currentSelectedFood;
    private ImageButton currentImageButton;

    public RestaurantManageMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSelectImageRequested(Food food, ImageButton imageButton) {
        currentSelectedFood = food;
        currentImageButton = imageButton;

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantManageMenuFragment.
     */
    // TODO: Rename and change types and number of parameters

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTabSwitchListener) {
            callback = (OnTabSwitchListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnTabSwitchListener");
        }
    }

    public static RestaurantManageMenuFragment newInstance(String param1, String param2) {
        RestaurantManageMenuFragment fragment = new RestaurantManageMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_manage_menu, container, false);

        // 從 arguments 取出 restaurant
        Bundle args = getArguments();
        if (args != null) {
            restaurant = (Restaurant) args.getSerializable("restaurant_data");
        }

        Button btnToInfo = view.findViewById(R.id.btn_back);
        btnToInfo.setOnClickListener(v -> {
            if (callback != null) {
                callback.onSwitchToInfo();  // 通知 Activity 切換
            }
        });

        Button btnToList = view.findViewById(R.id.btn_save);
        btnToList.setOnClickListener(v -> {
//            if (foodList != null && !foodList.isEmpty()) {
//                StringBuilder sb = new StringBuilder("Food List: ");
//                for (Food food : foodList) {
//                    sb.append("- ").append(food.getName()); // 假設 Food 有 getName()
//                }
//                Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getContext(), "Food list is empty!", Toast.LENGTH_SHORT).show();
//            }
            restaurant.setFoodList(foodList);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // 要更新的餐廳 document ID
            String id = restaurant.getId();

            // 將 foodList 轉為 List<Map<String, Object>> 格式
            List<Map<String, Object>> foodMapList = new ArrayList<>();
            for (Food food : restaurant.getFoodList()) {
                Map<String, Object> foodMap = new HashMap<>();
                foodMap.put("name", food.getName());
                foodMap.put("price", food.getPrice());
                foodMap.put("image", food.getImage());
                foodMapList.add(foodMap);
            }

            // 建立要更新的內容
            Map<String, Object> updates = new HashMap<>();
            updates.put("menu", foodMapList);
            updates.put("name", restaurant.getName());
            updates.put("phone", restaurant.getPhone());
            updates.put("address", restaurant.getLocation());
            updates.put("image", restaurant.getImage());

            // 執行更新
            db.collection("restaurant")
                    .document(id)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "更新成功", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "更新失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });


//            Intent intent = new Intent(getActivity(), MainActivity.class);
//            startActivity(intent);
        });

        recyclerView = view.findViewById(R.id.rv_manage_menu_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 改為使用 restaurant 中的 menu
        if (restaurant != null && restaurant.getFoodList() != null) {
            foodList = restaurant.getFoodList();
        } else {
            foodList = new ArrayList<>();  // 若為空則避免閃退
        }

        adapter = new RestaurantMenuListAdapter(requireContext(), foodList , this, true);
        recyclerView.setAdapter(adapter);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        Toast.makeText(getContext(), "圖片選擇成功", Toast.LENGTH_SHORT).show();

                        try {
                            String base64String = encodeImageToDataUrl(imageUri);

                            Toast.makeText(getContext(), "準備更新圖片到Food物件", Toast.LENGTH_SHORT).show();
                            adapter.updateFoodImage(currentSelectedFood, base64String);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "圖片編碼失敗: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "圖片選擇取消或失敗", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        return view;
    }

    private Bitmap decodeBase64ToBitmap(String base64Str) {
        // 去除前綴 "data:image/jpeg;base64," 或類似的部分
        String pureBase64 = base64Str.substring(base64Str.indexOf(",") + 1);
        byte[] decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void updateFoodImage(Food food, String base64Image, ImageButton imgbtn) {
        food.setImage(base64Image);  // 把base64存入Food物件
        imgbtn.setImageBitmap(decodeBase64ToBitmap(base64Image));  // 更新 ImageButton 顯示

        adapter.notifyDataSetChanged(); // 或是notifyItemChanged，視情況
    }

    private String encodeImageToDataUrl(Uri imageUri) throws IOException {
        // 讀取圖片輸入流
        InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

        // 步驟一：縮小圖片（最多寬或高為800px）
        Bitmap resizedBitmap = resizeBitmap(originalBitmap, 800);

        // 步驟二：壓縮圖片為 JPEG（壓縮品質 70%）
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        // 步驟三：取得 mimeType 並組成 Data URL
        String mimeType = requireContext().getContentResolver().getType(imageUri);
        String base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        return "data:" + mimeType + ";base64," + base64;
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale = Math.min((float) maxSize / width, (float) maxSize / height);
        if (scale >= 1) return bitmap; // 如果太小，不需縮放

        int newWidth = Math.round(scale * width);
        int newHeight = Math.round(scale * height);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}