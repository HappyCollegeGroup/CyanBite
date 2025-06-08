package fcu.app.cyanbite.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log; // Import Log for debugging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
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
import fcu.app.cyanbite.model.Group;
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
    private String groupImageBase64;
    private ArrayList<String> groupTags; // Declare ArrayList for tags
    private ImageButton imgbtn;
    private Group group;

    private OnGroupSwitchListener callback;
    private FirebaseFirestore db;
    private Bundle prev;

    private List<String> selectedRestaurantIds = new ArrayList<>();
    private RestaurantAdapter adapter;
    private List<Restaurant> allRestaurantList = new ArrayList<>();

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

        adapter = new RestaurantAdapter(allRestaurantList, restaurant -> {
            Fragment fragment = new RestaurantShowInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurant_data", restaurant);
            fragment.setArguments(bundle);

            getParentFragmentManager().setFragmentResultListener(
                    "add_selected_restaurant", this,
                    (requestKey, result) -> {
                        Log.d(TAG, "Received fragment result for key: " + requestKey);
                        Restaurant selected = (Restaurant) result.getSerializable("selectedRestaurant");
                        if (selected != null) {
                            String id = selected.getId();
                            if (!selectedRestaurantIds.contains(id)) { // Avoid duplicates
                                selectedRestaurantIds.add(id);
                                Log.d(TAG, "Added restaurant ID: " + id + " to selected list.");
                                etRestaurantName.setText(getSelectedRestaurantsNames(allRestaurantList));
                                Log.d(TAG, "etRestaurantName updated to: " + etRestaurantName.getText().toString());
                            } else {
                                Log.d(TAG, "Restaurant ID " + id + " already in selected list.");
                            }
                        } else {
                            Log.w(TAG, "Received null selectedRestaurant from result.");
                        }
                    });

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack("restaurant_info_tag")
                    .commit();
            Log.d(TAG, "Navigating to RestaurantShowInfoFragment.");
        });

        rvRestaurantList.setAdapter(adapter);

        if (prev != null) {
            String initialSelectedRestaurantId = prev.getString("selectedRestaurantId");
            if (initialSelectedRestaurantId != null && !selectedRestaurantIds.contains(initialSelectedRestaurantId)) {
                selectedRestaurantIds.add(initialSelectedRestaurantId); // Add initial ID to the list
                Log.d(TAG, "Initial selected restaurant ID from prev bundle: " + initialSelectedRestaurantId);
                // The etRestaurantName will be updated after allRestaurantList is loaded from Firestore
            }
            groupImageBase64 = prev.getString("groupImage");
            groupTags = prev.getStringArrayList("groupTags"); // Retrieve groupTags
            if (groupImageBase64 != null) {
                Log.d(TAG, "Group image base64 retrieved from prev bundle.");
            } else {
                Log.w(TAG, "Group image base64 is null in prev bundle.");
            }
            if (groupTags != null) {
                Log.d(TAG, "Group tags retrieved from prev bundle: " + groupTags.toString());
            } else {
                Log.w(TAG, "Group tags is null in prev bundle.");
            }
        } else {
            Log.d(TAG, "Prev bundle is null.");
        }

        db.collection("restaurant")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allRestaurantList.clear(); // Clear old data
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
                        adapter.notifyDataSetChanged(); // Notify Adapter data has changed
                        Log.d(TAG, "Restaurant list loaded from Firestore. Count: " + allRestaurantList.size());

                        if (prev != null && prev.getString("selectedRestaurantId") != null) {
                            etRestaurantName.setText(getSelectedRestaurantsNames(allRestaurantList));
                            Log.d(TAG, "Initial etRestaurantName update after list load: " + etRestaurantName.getText().toString());
                        }

                    } else {
                        Log.e(TAG, "Error loading restaurant list: " + task.getException().getMessage(), task.getException());
                        Toast.makeText(getActivity(), "載入餐廳列表失敗: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        btnSubmitRestaurant.setOnClickListener(v -> {
            Log.d(TAG, "Submit button clicked.");

            if (selectedRestaurantIds.isEmpty()) {
                Toast.makeText(getActivity(), "請選擇至少一間餐廳", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "No restaurant selected. Submission cancelled.");
                return;
            }

            if (prev == null) {
                Toast.makeText(getActivity(), "團購資訊不完整，請返回重新填寫", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Prev bundle is null. Group info missing.");
                return;
            }

            groupName = prev.getString("groupName");
            groupPhone = prev.getString("groupPhone");
            groupLocation = prev.getString("groupLocation");
            orderingTime = prev.getString("orderingTime");
            collectionTime = prev.getString("collectionTime");

            groupCity = prev.getString("groupCity");
            groupDistrict = prev.getString("groupDistrict");
            groupDescription = prev.getString("groupDescription");
            groupImageBase64 = prev.getString("groupImage");
            groupTags = prev.getStringArrayList("groupTags");

            if (groupName == null || groupPhone == null || groupLocation == null || orderingTime == null || collectionTime == null || groupCity == null || groupDistrict == null || groupDescription == null || groupImageBase64 == null || groupTags == null || groupTags.isEmpty()) {
                Toast.makeText(getActivity(), "團購基本資訊缺失或類型未選，無法提交！", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Missing group basic info or tags: " +
                        "name=" + groupName + ", phone=" + groupPhone +
                        ", location=" + groupLocation + ", orderingTime=" + orderingTime +
                        ", collectionTime=" + collectionTime +
                        ", city=" + groupCity + ", district=" + groupDistrict + ", description=" + groupDescription +
                        ", image=" + (groupImageBase64 != null ? "present" : "missing") +
                        ", tags=" + (groupTags != null ? groupTags.toString() : "null"));
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

            String uid = db.collection("groups").document().getId(); // Generate new document ID

            Map<String, Object> group = new HashMap<>();
            group.put("name", groupName);
            group.put("phone", groupPhone);
            group.put("place", groupLocation);
            group.put("orderTime", orderingTime);
            group.put("collectionTime", collectionTime);

            group.put("city", groupCity);
            group.put("district", groupDistrict);
            group.put("description", groupDescription);
            group.put("image", groupImageBase64);
            group.put("tag", groupTags);

            List<DocumentReference> restaurantRefs = new ArrayList<>();
            for (String restId : selectedRestaurantIds) {
                restaurantRefs.add(db.collection("restaurant").document(restId));
                Log.d(TAG, "Adding restaurant reference: " + restId);
            }
            group.put("restaurant", restaurantRefs);
            group.put("uid", creatorId);

            // Generate and add search keywords
            if (groupName != null) {
                List<String> searchKeywords = generateSearchKeywords(groupName);
                group.put("searchKeywords", searchKeywords);
                Log.d(TAG, "Generated search keywords: " + searchKeywords.toString());
            } else {
                Log.w(TAG, "groupName is null, cannot generate search keywords.");
            }

            Log.d(TAG, "Attempting to submit group data: " + group.toString());

            db.collection("groups").document(uid).set(group)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "團購資料已提交！", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Group data submitted successfully! Group ID: " + uid);
                        getActivity().finish(); // Finish Activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "提交失敗：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error submitting group data: " + e.getMessage(), e);
                    });
        });

        btnBack.setOnClickListener(v -> {
            if (callback != null) {
                callback.onSwitchToGroupInfo(); // Notify Activity to switch back to GroupInfoFragment
                Log.d(TAG, "Back button clicked. Switching to GroupInfo.");
            }
        });

        return view;
    }


    private List<String> generateSearchKeywords(String text) {
        List<String> keywords = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            keywords.add(""); // Add empty string for empty input
            return keywords;
        }

        String cleanedText = text.trim();
        keywords.add(""); // Add empty string as the first keyword

        for (int i = 0; i < cleanedText.length(); i++) {
            for (int j = i + 1; j <= cleanedText.length(); j++) {
                keywords.add(cleanedText.substring(i, j));
            }
        }
        return keywords;
    }

    /**
     * Helper method: Get restaurant names string based on selected restaurant ID list
     * @param allRestaurants List of all restaurants to look up names
     * @return Comma-separated string of restaurant names
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