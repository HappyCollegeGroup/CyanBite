package fcu.app.cyanbite.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.RestaurantAdapter;
import fcu.app.cyanbite.model.Restaurant;

public class AddNewGroupRestaurantFragment extends Fragment {

    private EditText etRestaurantName;

    private String groupName;
    private String groupPhone;
    private String groupLocation;
    private String orderingTime;
    private String collectionTime;
    private OnGroupSwitchListener callback;
    private FirebaseFirestore db;
    private Bundle prev;
    private String selectedRestaurantName;



    public AddNewGroupRestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnGroupSwitchListener) {
            callback = (OnGroupSwitchListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnGroupSwitchListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void addArgument(Bundle bundle){
        prev = bundle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_group_restaurant, container, false);

        db = FirebaseFirestore.getInstance();

        etRestaurantName = view.findViewById(R.id.et_group_restaurant);

        Button btnSubmitRestaurant = view.findViewById(R.id.btn_submit_add_new_group);
        Button btnBack = view.findViewById(R.id.btn_back);

        List<Restaurant> qrestaurantList = new ArrayList<>();
        qrestaurantList.add(new Restaurant("逢甲一起訂1", "0900-000-000", "逢甲大學", null, R.drawable.image));
        qrestaurantList.add(new Restaurant("逢甲一起訂2", "0900-000-000", "逢甲大學", null, R.drawable.image));
        qrestaurantList.add(new Restaurant("逢甲一起訂3", "0900-000-000", "逢甲大學", null, R.drawable.image));


        RecyclerView rvRestaurantList = view.findViewById(R.id.rv_restaurant_list);
        rvRestaurantList.setLayoutManager(new LinearLayoutManager(getActivity()));


        RestaurantAdapter adapter = new RestaurantAdapter( qrestaurantList, new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant restaurant) {
                Intent intent = new Intent(getActivity(), RestaurantShowActivity.class);
                intent.putExtra("restaurant_data", restaurant);
                startActivity(intent);
            }

        });
        rvRestaurantList.setAdapter(adapter);

        btnSubmitRestaurant.setOnClickListener(v -> {
            String restaurantNames = etRestaurantName.getText().toString().trim();

            if (restaurantNames.isEmpty()) {
                Toast.makeText(getActivity(), "請填寫所有欄位", Toast.LENGTH_SHORT).show();
                return;
            }

            // 分割餐廳名稱，並移除每個名稱兩端的空白字元
            String[] restaurantArray = restaurantNames.split(",");
            List<String> restaurantList = new ArrayList<>();
            for (String restaurant : restaurantArray) {
                restaurantList.add(restaurant.trim());  // 移除名稱兩端的空白字元
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = db.collection("groups").document().getId();

            String creatorId = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : "unknown";

            groupName = prev.getString("groupName");
            groupPhone = prev.getString("groupPhone");
            groupLocation = prev.getString("groupLocation");
            orderingTime = prev.getString("orderingTime");
            collectionTime = prev.getString("collectionTime");

            Map<String, Object> group = new HashMap<>();
            group.put("name", groupName);
            group.put("phone", groupPhone);
            group.put("location", groupLocation);
            group.put("orderingTime", orderingTime);
            group.put("collectionTime", collectionTime);
            group.put("restaurant", restaurantList);
            group.put("creatorId", creatorId);

            db.collection("groups").document(uid).set(group)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "團購資料已提交", Toast.LENGTH_SHORT).show();
                        getActivity().finish();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "提交失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        });

        btnBack.setOnClickListener(v -> {
            if (callback != null) {
                callback.onSwitchToGroupInfo();
            }
        });

        return view;
    }
}
