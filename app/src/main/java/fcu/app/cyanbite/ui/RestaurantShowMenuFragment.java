package fcu.app.cyanbite.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager; // 導入 FragmentManager
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast; // 導入 Toast

import java.util.ArrayList;
import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.RestaurantMenuListAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Restaurant;

public class RestaurantShowMenuFragment extends Fragment implements ImageSelectListener {

    private RecyclerView recyclerView;
    private RestaurantMenuListAdapter adapter;
    private List<Food> foodList;
    private Restaurant currentRestaurant; // 儲存從上一個 Fragment 傳入的餐廳物件

    public RestaurantShowMenuFragment() {
        // 需要一個空的建構子
    }
    @Override
    public void onSelectImageRequested(Food food, ImageButton imageButton) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // 從 arguments 中獲取 Restaurant 物件
            currentRestaurant = (Restaurant) getArguments().getSerializable("restaurant_data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_show_menu, container, false);

        // 「返回」按鈕的點擊事件：彈出當前 Fragment，回到 RestaurantShowInfoFragment
        Button btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // 「加入」按鈕的點擊事件
        Button btnAddToList = view.findViewById(R.id.btn_add);
        btnAddToList.setOnClickListener(v -> {
            if (currentRestaurant != null) {
                // 使用 Fragment Result API 將選中的餐廳回傳給 AddNewGroupRestaurantFragment
                Bundle resultBundle = new Bundle();
                resultBundle.putSerializable("selectedRestaurant", currentRestaurant); // 將 Restaurant 物件放入 Bundle

                // 設定 Fragment 結果，"add_selected_restaurant" 是唯一的請求鍵
                getParentFragmentManager().setFragmentResult("add_selected_restaurant", resultBundle);

                // **新增：顯示 Toast 訊息，告知使用者已成功加入**
                Toast.makeText(getContext(), "已將 " + currentRestaurant.getName() + " 加入團購清單", Toast.LENGTH_SHORT).show();

                // 彈出回退棧，直到 "restaurant_info_tag" 這個標籤為止，並且包含這個標籤所代表的 Fragment
                // 這會將 RestaurantShowMenuFragment 和 RestaurantShowInfoFragment 都彈出，直接回到 AddNewGroupRestaurantFragment
                getParentFragmentManager().popBackStack("restaurant_info_tag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        recyclerView = view.findViewById(R.id.rv_manage_menu_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 設定菜單資料：使用從 Bundle 中獲取的 currentRestaurant
        if (currentRestaurant != null && currentRestaurant.getFoodList() != null) {
            foodList = currentRestaurant.getFoodList();
        } else {
            foodList = new ArrayList<>();
        }

        adapter = new RestaurantMenuListAdapter(requireContext(), foodList, this, false);
        recyclerView.setAdapter(adapter);

        return view;
    }


}