package fcu.app.cyanbite.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.RestaurantMenuListAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantAddMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantAddMenuFragment extends Fragment {

    private OnTabSwitchListener callback;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private RestaurantMenuListAdapter adapter;
    private List<Food> foodList;
    private Restaurant restaurant;

    public RestaurantAddMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantAddMenuFragment.
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

    public static RestaurantAddMenuFragment newInstance(String param1, String param2) {
        RestaurantAddMenuFragment fragment = new RestaurantAddMenuFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_add_menu, container, false);

        Bundle args = getArguments();
        if (args != null) {
            restaurant = (Restaurant) args.getSerializable("restaurant_new_data");
        }

        Button btnToInfo = view.findViewById(R.id.btn_cancel);
        btnToInfo.setOnClickListener(v -> {
            if (callback != null) {
                callback.onSwitchToInfo();  // 通知 Activity 切換
            }
        });

        Button btnToList = view.findViewById(R.id.btn_save);
        btnToList.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);

            restaurant.setFoodList(foodList);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                    .document()
                    .set(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "更新成功", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "更新失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            startActivity(intent);
            requireActivity().finish();
        });

        recyclerView = view.findViewById(R.id.rv_add_menu_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 建立資料
        foodList = new ArrayList<>();
        // 不要加「新增」，adapter 會自動補上！

        // 設定 Adapter
        adapter = new RestaurantMenuListAdapter(requireContext(), foodList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}