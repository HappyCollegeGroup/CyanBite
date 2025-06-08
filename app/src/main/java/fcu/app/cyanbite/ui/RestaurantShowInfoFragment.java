package fcu.app.cyanbite.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Restaurant;

public class RestaurantShowInfoFragment extends Fragment {

    private OnTabSwitchListener callback;
    private Restaurant currentRestaurant; // 儲存從上一個 Fragment 傳入的餐廳物件

    public RestaurantShowInfoFragment() {
        // 需要一個空的建構子
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTabSwitchListener) {
            callback = (OnTabSwitchListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnTabSwitchListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentRestaurant = (Restaurant) getArguments().getSerializable("restaurant_data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_show_info, container, false);

        if (currentRestaurant != null) {
            TextView tvName = view.findViewById(R.id.et_name);
            TextView tvPhone = view.findViewById(R.id.et_phone);
            TextView tvLocation = view.findViewById(R.id.et_location);
            ImageButton imgButton = view.findViewById(R.id.img_btn_restaurant);

            tvName.setText(currentRestaurant.getName());
            tvPhone.setText(currentRestaurant.getPhone());
            tvLocation.setText(currentRestaurant.getLocation());
            imgButton.setImageBitmap(currentRestaurant.getImageBitmap());
            // 處理圖片載入，請根據您的 Restaurant.getImage() 返回的類型選擇
            // 如果是圖片資源 ID (int):
            // imgButton.setImageResource(currentRestaurant.getImage());
            // 如果是圖片 URL (String)，建議使用 Glide:
            // Glide.with(this).load(currentRestaurant.getImage()).into(imgButton);

        } else {
            Toast.makeText(getActivity(), "未接收到餐廳資料", Toast.LENGTH_SHORT).show();
        }

        // 「返回」按鈕的點擊事件：彈出當前 Fragment，回到 RestaurantShowInfoFragment
        Button btnBack = view.findViewById(R.id.btn_cancel);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // 「查看菜單」按鈕，導航到 RestaurantShowMenuFragment
        Button btnViewMenu = view.findViewById(R.id.btn_next);
        btnViewMenu.setOnClickListener(v -> {
            RestaurantShowMenuFragment menuFragment = new RestaurantShowMenuFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurant_data", currentRestaurant); // 傳遞 currentRestaurant
            menuFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, menuFragment)
                    .addToBackStack(null) // 添加到回退棧，但無需特定標籤，因為 RestaurantShowMenuFragment 會彈出整個棧
                    .commit();
        });

        return view;
    }
}