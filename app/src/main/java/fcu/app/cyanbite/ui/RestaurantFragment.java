package fcu.app.cyanbite.ui;

import static fcu.app.cyanbite.util.Util.setStatusBar;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.RestaurantAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private RestaurantAdapter adapter;

    public RestaurantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantFragment newInstance(String param1, String param2) {
        RestaurantFragment fragment = new RestaurantFragment();
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
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);

        FloatingActionButton btnAddRestaurant = view.findViewById(R.id.btn_next);
        btnAddRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RestaurantAddActivity.class);
                startActivity(intent);
            }
        });

        db = FirebaseFirestore.getInstance();

        // 綁定 RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rv_restaurant_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RestaurantAdapter(restaurantList, new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant restaurant) {
                Intent intent = new Intent(getActivity(), RestaurantManageActivity.class);
                intent.putExtra("restaurant_data", restaurant);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        loadData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setStatusBar(getActivity(), true);
        loadData();
    }

    private void loadData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = user.getUid();
        db.collection("restaurant")
                .whereEqualTo("uid", userUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        restaurantList.clear();

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String id = doc.getId();
                            String name = doc.getString("name");
                            String image = doc.getString("image");
                            String address = doc.getString("address");
                            String phone = doc.getString("phone");
                            List<Map<String, Object>> DocFoodList = (List<Map<String, Object>>) doc.get("menu");
                            List<Food> foodList = new ArrayList<>();

                            for (Map<String, Object> foodMap : DocFoodList) {
                                String foodname = (String) foodMap.get("name");
                                Long foodprice = (Long) foodMap.get("price"); // Firestore 整數預設為 Long
                                String foodimage = (String) foodMap.get("image");

                                Food food = new Food(foodname, foodprice, foodimage);
                                foodList.add(food);
                            }
                            restaurantList.add(new Restaurant(id, name, phone, address, foodList, image));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}