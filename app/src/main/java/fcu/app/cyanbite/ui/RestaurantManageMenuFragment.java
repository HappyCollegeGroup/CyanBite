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

import java.util.ArrayList;
import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.RestaurantMenuListAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantManageMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantManageMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnTabSwitchListener callback;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private RestaurantMenuListAdapter adapter;
    private List<Food> foodList;

    public RestaurantManageMenuFragment() {
        // Required empty public constructor
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

        Button btnToList = view.findViewById(R.id.btn_save);
        btnToList.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("navigate_to", "restaurant");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            requireActivity().finish();
        });

        recyclerView = view.findViewById(R.id.rv_manage_menu_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 改為使用 restaurant 中的 menu
        if (restaurant != null && restaurant.getFoodList() != null) {
            foodList = restaurant.getFoodList();
        } else {
            foodList = new ArrayList<>();  // 若為空則避免閃退
        }

        adapter = new RestaurantMenuListAdapter(requireContext(), foodList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}