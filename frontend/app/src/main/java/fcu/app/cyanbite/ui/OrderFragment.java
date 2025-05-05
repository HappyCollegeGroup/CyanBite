package fcu.app.cyanbite.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.OrderGroupListAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Group;
import fcu.app.cyanbite.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
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
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        Button btnShoppingCart = view.findViewById(R.id.btn_shopping_cart);
        btnShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShoppingCartActivity.class);
                startActivity(intent);
            }
        });

        List<Food> foodList = new ArrayList<>();
        foodList.add(new Food("部隊鍋泡麵", 120, R.drawable.image));
        foodList.add(new Food("部隊鍋泡麵", 120, R.drawable.image));
        foodList.add(new Food("部隊鍋泡麵", 120, R.drawable.image));
        foodList.add(new Food("部隊鍋泡麵", 120, R.drawable.image));
        foodList.add(new Food("部隊鍋泡麵", 120, R.drawable.image));
        foodList.add(new Food("部隊鍋泡麵", 120, R.drawable.image));
        foodList.add(new Food("部隊鍋泡麵", 120, R.drawable.image));

        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant("逢甲一起訂1", "0900-000-000", "逢甲大學", foodList, R.drawable.image));
        restaurantList.add(new Restaurant("逢甲一起訂2", "0900-000-000", "逢甲大學", foodList, R.drawable.image));
        restaurantList.add(new Restaurant("逢甲一起訂3", "0900-000-000", "逢甲大學", foodList, R.drawable.image));
        restaurantList.add(new Restaurant("逢甲一起訂4", "0900-000-000", "逢甲大學", foodList, R.drawable.image));

        List<Group> groupList = new ArrayList<>();
        groupList.add(new Group("逢甲一起訂1", "0900-000-000", "逢甲大學", "9:00~10:00", "12:00", restaurantList, R.drawable.image));
        groupList.add(new Group("逢甲一起訂2", "0900-000-000", "逢甲大學", "9:00~10:00", "12:00", restaurantList, R.drawable.image));
        groupList.add(new Group("逢甲一起訂3", "0900-000-000", "逢甲大學", "9:00~10:00", "12:00", restaurantList, R.drawable.image));
        groupList.add(new Group("逢甲一起訂4", "0900-000-000", "逢甲大學", "9:00~10:00", "12:00", restaurantList, R.drawable.image));

        RecyclerView recyclerView = view.findViewById(R.id.rv_order_group_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        OrderGroupListAdapter adapter = new OrderGroupListAdapter(getActivity(), groupList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.cyan));
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(window, window.getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
    }
}