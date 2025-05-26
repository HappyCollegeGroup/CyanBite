package fcu.app.cyanbite.ui;

import android.content.Context;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.RestaurantAdapter;
import fcu.app.cyanbite.model.Food;
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
    private String selectedRestaurantId;

    private List<String> selectedRestaurantIds = new ArrayList<>();
    private RestaurantAdapter adapter;  // ✅ 宣告為成員變數

    public AddNewGroupRestaurantFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnGroupSwitchListener) {
            callback = (OnGroupSwitchListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnGroupSwitchListener");
        }
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
        RecyclerView rvRestaurantList = view.findViewById(R.id.rv_restaurant_list);
        rvRestaurantList.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Restaurant> restaurantList = new ArrayList<>();

        // ✅ 初始化 adapter，並使用成員變數
        adapter = new RestaurantAdapter(restaurantList, restaurant -> {
            Fragment fragment = new RestaurantShowMenuFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurant_data", restaurant);
            fragment.setArguments(bundle);

            getParentFragmentManager().setFragmentResultListener(
                    "add_selected_restaurant", this,
                    (requestKey, result) -> {
                        Restaurant selected = (Restaurant) result.getSerializable("selectedRestaurant");
                        if (selected != null) {
                            String id = selected.getId();
                            if (!selectedRestaurantIds.contains(id)) {
                                selectedRestaurantIds.add(id);
                                etRestaurantName.setText(getSelectedRestaurantsNames(restaurantList));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });


        rvRestaurantList.setAdapter(adapter);

        // ✅ 註冊 FragmentResultListener，接收餐廳資料
        getParentFragmentManager().setFragmentResultListener("request_restaurant_selection", this, (requestKey, result) -> {
            Restaurant selectedRestaurant = (Restaurant) result.getSerializable("selectedRestaurant");
            if (selectedRestaurant != null) {
                selectedRestaurantId = selectedRestaurant.getId();
                etRestaurantName.setText(selectedRestaurant.getName());
            }
        });

        // 如果上個畫面已傳入餐廳 ID，就載入餐廳名稱
        if (prev != null) {
            selectedRestaurantId = prev.getString("selectedRestaurantId");
            if (selectedRestaurantId != null) {
                db.collection("restaurant").document(selectedRestaurantId).get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                String name = doc.getString("name");
                                etRestaurantName.setText(name);
                            }
                        });
            }
        }

        // 從 Firestore 載入餐廳清單
        db.collection("restaurant")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String id = doc.getId();
                            String name = doc.getString("name");
                            String image = doc.getString("image");
                            String address = doc.getString("address");
                            String phone = doc.getString("phone");
                            List<Map<String, Object>> docFoodList = (List<Map<String, Object>>) doc.get("menu");
                            List<Food> foodList = new ArrayList<>();
                            for (Map<String, Object> foodMap : docFoodList) {
                                String foodName = (String) foodMap.get("name");
                                Long foodPrice = (Long) foodMap.get("price");
                                String foodImage = (String) foodMap.get("image");
                                foodList.add(new Food(foodName, foodPrice, foodImage));
                            }
                            restaurantList.add(new Restaurant(id, name, phone, address, foodList, image));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        btnSubmitRestaurant.setOnClickListener(v -> {
            if (selectedRestaurantIds.isEmpty()) {
                Toast.makeText(getActivity(), "請選擇至少一間餐廳", Toast.LENGTH_SHORT).show();
                return;
            }

            groupName = prev.getString("groupName");
            groupPhone = prev.getString("groupPhone");
            groupLocation = prev.getString("groupLocation");
            orderingTime = prev.getString("orderingTime");
            collectionTime = prev.getString("collectionTime");

            String creatorId = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : "unknown";

            String uid = db.collection("groups").document().getId();

            Map<String, Object> group = new HashMap<>();
            group.put("name", groupName);
            group.put("phone", groupPhone);
            group.put("location", groupLocation);
            group.put("orderingTime", orderingTime);
            group.put("collectionTime", collectionTime);

            List<DocumentReference> restaurantRefs = new ArrayList<>();
            for (String restId : selectedRestaurantIds) {
                restaurantRefs.add(db.collection("restaurant").document(restId));
            }
            group.put("restaurant", restaurantRefs);
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

    private String getSelectedRestaurantsNames(List<Restaurant> allRestaurants) {
        StringBuilder sb = new StringBuilder();
        for (String id : selectedRestaurantIds) {
            for (Restaurant r : allRestaurants) {
                if (r.getId().equals(id)) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(r.getName());
                    break;
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : "";
    }
}
