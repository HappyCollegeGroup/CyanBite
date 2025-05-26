package fcu.app.cyanbite.ui;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.RestaurantMenuListAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Restaurant;

public class RestaurantShowMenuFragment extends Fragment {

    private OnTabSwitchListener callback;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private RestaurantMenuListAdapter adapter;
    private List<Food> foodList;

    public RestaurantShowMenuFragment() {
        // Required empty public constructor
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

    public static RestaurantShowMenuFragment newInstance(String param1, String param2) {
        RestaurantShowMenuFragment fragment = new RestaurantShowMenuFragment();
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
        View view = inflater.inflate(R.layout.fragment_restaurant_show_menu, container, false);

        // 從 arguments 取出 restaurant
        Bundle args = getArguments();
        Restaurant restaurant = null;
        if (args != null) {
            restaurant = (Restaurant) args.getSerializable("restaurant_data");
        }

        Button btnToInfo = view.findViewById(R.id.btn_back);
        btnToInfo.setOnClickListener(v -> {
            if (callback != null) {
                callback.onSwitchToInfo();  // 通知 Activity 切換
            }
        });

        Button btnAddToList = view.findViewById(R.id.btn_add);
        Restaurant thisRestaurant = restaurant;  // 為了 lambda 使用

        btnAddToList.setOnClickListener(v -> {
            if (thisRestaurant != null) {
                // 使用 Fragment Result API 將選中的餐廳回傳給 AddNewGroupRestaurantFragment
                Bundle resultBundle = new Bundle();
                resultBundle.putSerializable("selectedRestaurant", thisRestaurant); // 將 Restaurant 物件放入 Bundle

                // 設定 Fragment 結果，"request_restaurant_selection" 是唯一的請求鍵
                getParentFragmentManager().setFragmentResult("add_selected_restaurant", resultBundle);

                // 返回上一個 Fragment (AddNewGroupRestaurantFragment)
                getParentFragmentManager().popBackStack();

                // 移除原有的 Activity.setResult 和 finish()，因為現在是 Fragment 間的通訊
                // Intent resultIntent = new Intent();
                // resultIntent.putExtra("selectedRestaurantId", finalRestaurant.getId()); // 傳 ID
                // getActivity().setResult(RESULT_OK, resultIntent);
                // getActivity().finish();
            }
        });

        recyclerView = view.findViewById(R.id.rv_manage_menu_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 設定菜單資料
        if (restaurant != null && restaurant.getFoodList() != null) {
            foodList = restaurant.getFoodList();
        } else {
            foodList = new ArrayList<>();
        }

        adapter = new RestaurantMenuListAdapter(requireContext(), foodList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}