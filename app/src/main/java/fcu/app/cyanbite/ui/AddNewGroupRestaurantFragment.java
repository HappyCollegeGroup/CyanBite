package fcu.app.cyanbite.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcu.app.cyanbite.R;

public class AddNewGroupRestaurantFragment extends Fragment {

    private EditText etRestaurantName;

    private String groupName;
    private String groupPhone;
    private String groupLocation;
    private String orderingTime;
    private String collectionTime;

    public AddNewGroupRestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            groupName = getArguments().getString("groupName");
            groupPhone = getArguments().getString("groupPhone");
            groupLocation = getArguments().getString("groupLocation");
            orderingTime = getArguments().getString("orderingTime");
            collectionTime = getArguments().getString("collectionTime");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_group_restaurant, container, false);

        etRestaurantName = view.findViewById(R.id.et_group_restaurant);

        Button btnSubmitRestaurant = view.findViewById(R.id.btn_submit_add_new_group);
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

        return view;
    }
}
