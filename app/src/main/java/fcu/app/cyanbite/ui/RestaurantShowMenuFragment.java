package fcu.app.cyanbite.ui;

import android.content.Context;
import android.os.Bundle;

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
        Restaurant finalRestaurant = restaurant;  // 為了 lambda 使用

        btnAddToList.setOnClickListener(v -> {
            if (finalRestaurant != null) {
                // 將選中的餐廳放入 Bundle
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedRestaurant", finalRestaurant);

                // 建立並傳入參數給 AddNewGroupRestaurantFragment
                AddNewGroupRestaurantFragment fragment = new AddNewGroupRestaurantFragment();
                fragment.addArgument(bundle);  // 正確呼叫 addArgument
                fragment.setArguments(bundle); // 確保 getArguments() 仍然有效（如果有用到）

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_manage_restaurant, fragment)  // 注意容器 ID
                        .addToBackStack(null)
                        .commit();
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
