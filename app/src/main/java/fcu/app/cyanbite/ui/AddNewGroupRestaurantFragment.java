package fcu.app.cyanbite.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log; // Import Log for debugging
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

    private static final String TAG = "AddNewGroupRestaurant"; // Tag for Logcat

    private EditText etRestaurantName;
    private String groupName;
    private String groupPhone;
    private String groupLocation;
    private String orderingTime;
    private String collectionTime;

    private String groupCity;
    private String groupDistrict;
    private String groupDescription;

    private OnGroupSwitchListener callback;
    private FirebaseFirestore db;
    private Bundle prev;

    private List<String> selectedRestaurantIds = new ArrayList<>();
    private RestaurantAdapter adapter;
    private List<Restaurant> allRestaurantList = new ArrayList<>(); // Used for getSelectedRestaurantsNames

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

        // 初始化 adapter，使用 allRestaurantList
        adapter = new RestaurantAdapter(allRestaurantList, restaurant -> {
            // 當點擊餐廳時，導航到 RestaurantShowInfoFragment
            Fragment fragment = new RestaurantShowInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurant_data", restaurant);
            fragment.setArguments(bundle);

            // 設置 Fragment 結果監聽器，用於從 RestaurantShowMenuFragment 接收選中的餐廳
            getParentFragmentManager().setFragmentResultListener(
                    "add_selected_restaurant", this,
                    (requestKey, result) -> {
                        Log.d(TAG, "Received fragment result for key: " + requestKey);
                        Restaurant selected = (Restaurant) result.getSerializable("selectedRestaurant");
                        if (selected != null) {
                            String id = selected.getId();
                            if (!selectedRestaurantIds.contains(id)) { // 避免重複加入
                                selectedRestaurantIds.add(id);
                                Log.d(TAG, "Added restaurant ID: " + id + " to selected list.");
                                // 更新 EditText 顯示選中的餐廳名稱
                                etRestaurantName.setText(getSelectedRestaurantsNames(allRestaurantList));
                                Log.d(TAG, "etRestaurantName updated to: " + etRestaurantName.getText().toString());
                            } else {
                                Log.d(TAG, "Restaurant ID " + id + " already in selected list.");
                            }
                        } else {
                            Log.w(TAG, "Received null selectedRestaurant from result.");
                        }
                    });

            // 執行 Fragment 替換，並將事務添加到回退棧，標籤為 "restaurant_info_tag"
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack("restaurant_info_tag") // <--- 添加這個標籤
                    .commit();
            Log.d(TAG, "Navigating to RestaurantShowInfoFragment.");
        });

        rvRestaurantList.setAdapter(adapter);

        // 如果上個畫面已傳入餐廳 ID，就載入餐廳名稱
        if (prev != null) {
            String initialSelectedRestaurantId = prev.getString("selectedRestaurantId");
            if (initialSelectedRestaurantId != null && !selectedRestaurantIds.contains(initialSelectedRestaurantId)) {
                selectedRestaurantIds.add(initialSelectedRestaurantId); // 將初始ID加入列表
                Log.d(TAG, "Initial selected restaurant ID from prev bundle: " + initialSelectedRestaurantId);
                // The etRestaurantName will be updated after allRestaurantList is loaded from Firestore
            }
        } else {
            Log.d(TAG, "Prev bundle is null.");
        }

        // 從 Firestore 載入餐廳清單
        db.collection("restaurant")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allRestaurantList.clear(); // 清空舊資料
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String id = doc.getId();
                            String name = doc.getString("name");
                            String image = doc.getString("image");
                            String address = doc.getString("address");
                            String phone = doc.getString("phone");

                            List<Map<String, Object>> docFoodList = (List<Map<String, Object>>) doc.get("menu");
                            List<Food> foodList = new ArrayList<>();
                            if (docFoodList != null) {
                                for (Map<String, Object> foodMap : docFoodList) {
                                    String foodName = (String) foodMap.get("name");
                                    Long foodPrice = (Long) foodMap.get("price");
                                    String foodImage = (String) foodMap.get("image");
                                    foodList.add(new Food(foodName, foodPrice, foodImage));
                                }
                            }
                            allRestaurantList.add(new Restaurant(id, name, phone, address, foodList, image));
                        }
                        adapter.notifyDataSetChanged(); // 通知 Adapter 資料已更新
                        Log.d(TAG, "Restaurant list loaded from Firestore. Count: " + allRestaurantList.size());

                        // 載入完畢後，如果之前有傳入初始選中ID，現在更新 EditText
                        if (prev != null && prev.getString("selectedRestaurantId") != null) {
                            etRestaurantName.setText(getSelectedRestaurantsNames(allRestaurantList));
                            Log.d(TAG, "Initial etRestaurantName update after list load: " + etRestaurantName.getText().toString());
                        }

                    } else {
                        Log.e(TAG, "Error loading restaurant list: " + task.getException().getMessage(), task.getException());
                        Toast.makeText(getActivity(), "載入餐廳列表失敗: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // 「提交團購」按鈕的點擊事件
        btnSubmitRestaurant.setOnClickListener(v -> {
            Log.d(TAG, "Submit button clicked.");

            if (selectedRestaurantIds.isEmpty()) {
                Toast.makeText(getActivity(), "請選擇至少一間餐廳", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "No restaurant selected. Submission cancelled.");
                return;
            }

            // 確保 prev bundle 不是 null，且包含所有必要的群組資訊
            if (prev == null) {
                Toast.makeText(getActivity(), "團購資訊不完整，請返回重新填寫", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Prev bundle is null. Group info missing.");
                return;
            }

            // 獲取團購基本資訊
            groupName = prev.getString("groupName");
            groupPhone = prev.getString("groupPhone");
            groupLocation = prev.getString("groupLocation");
            orderingTime = prev.getString("orderingTime");
            collectionTime = prev.getString("collectionTime");

            groupCity = prev.getString("groupCity");
            groupDistrict = prev.getString("groupDistrict");
            groupDescription = prev.getString("groupDescription");



            // 再次檢查是否所有資訊都存在
            if (groupName == null || groupPhone == null || groupLocation == null || orderingTime == null || collectionTime == null) {
                Toast.makeText(getActivity(), "團購基本資訊缺失，無法提交！", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Missing group basic info: " +
                        "name=" + groupName + ", phone=" + groupPhone +
                        ", location=" + groupLocation + ", orderingTime=" + orderingTime +
                        ", collectionTime=" + collectionTime +
                        ", city=" + groupCity + ", district=" + groupDistrict + ", description=" + groupDescription);
                return;
            }

            String creatorId = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : "unknown";

            if ("unknown".equals(creatorId)) {
                Toast.makeText(getActivity(), "未登入，無法提交團購", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Creator ID is 'unknown'. User not logged in?");
                return;
            }

            String uid = db.collection("groups").document().getId(); // 生成新的文檔ID

            Map<String, Object> group = new HashMap<>();
            group.put("name", groupName);
            group.put("phone", groupPhone);
            group.put("place", groupLocation);
            group.put("orderTime", orderingTime);
            group.put("collectionTime", collectionTime);

            group.put("city", groupCity);
            group.put("district", groupDistrict);
            group.put("description", groupDescription);


            List<DocumentReference> restaurantRefs = new ArrayList<>();
            for (String restId : selectedRestaurantIds) {
                restaurantRefs.add(db.collection("restaurant").document(restId));
                Log.d(TAG, "Adding restaurant reference: " + restId);
            }
            group.put("restaurant", restaurantRefs);
            group.put("creatorId", creatorId);

            Log.d(TAG, "Attempting to submit group data: " + group.toString());

            db.collection("groups").document(uid).set(group)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "團購資料已提交！", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Group data submitted successfully! Group ID: " + uid);
                        getActivity().finish(); // 完成 Activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "提交失敗：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error submitting group data: " + e.getMessage(), e);
                    });
        });

        btnBack.setOnClickListener(v -> {
            if (callback != null) {
                callback.onSwitchToGroupInfo(); // 通知 Activity 切換回 GroupInfoFragment
                Log.d(TAG, "Back button clicked. Switching to GroupInfo.");
            }
        });

        return view;
    }

    /**
     * 輔助方法：根據選中的餐廳 ID 列表獲取餐廳名稱字串
     * @param allRestaurants 所有餐廳的列表，用於查找名稱
     * @return 逗號分隔的餐廳名稱字串
     */
    private String getSelectedRestaurantsNames(List<Restaurant> allRestaurants) {
        StringBuilder sb = new StringBuilder();
        for (String id : selectedRestaurantIds) {
            boolean found = false;
            for (Restaurant r : allRestaurants) {
                if (r.getId().equals(id)) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(r.getName());
                    found = true;
                    break;
                }
            }
            if (!found) {
                Log.w(TAG, "Restaurant with ID " + id + " not found in allRestaurantList.");
            }
        }
        return sb.length() > 0 ? sb.toString() : "";
    }
}