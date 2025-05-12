package fcu.app.cyanbite.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import fcu.app.cyanbite.R;

public class GroupDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        String groupName = getIntent().getStringExtra("groupName");
        String groupPhone = getIntent().getStringExtra("groupPhone");
        String groupLocation = getIntent().getStringExtra("groupLocation");
        String orderingTime = getIntent().getStringExtra("orderingTime");
        String collectionTime = getIntent().getStringExtra("collectionTime");
        List<String> restaurant = getIntent().getStringArrayListExtra("restaurant");

        EditText etGroupName = findViewById(R.id.et_group_detail_name);
        EditText etGroupPhone = findViewById(R.id.et_group_detail_phone);
        EditText etGroupLocation = findViewById(R.id.et_group_detail_location);
        EditText etOrderingTime = findViewById(R.id.et_group_detail_ordering_time);
        EditText etCollectionTime = findViewById(R.id.et_group_detail_collection_time);
        EditText etRestaurant = findViewById(R.id.et_group_detail_restaurant);

        etGroupName.setText(groupName);
        etGroupPhone.setText(groupPhone);
        etGroupLocation.setText(groupLocation);
        etOrderingTime.setText(orderingTime);
        etCollectionTime.setText(collectionTime);
        etRestaurant.setText(restaurant.toString());

        Button btnSave = findViewById(R.id.btn_save_group);
        btnSave.setOnClickListener(v -> {
            String newName = etGroupName.getText().toString();
            String newPhone = etGroupPhone.getText().toString();
            String newLocation = etGroupLocation.getText().toString();
            String newOrderingTime = etOrderingTime.getText().toString();
            String newCollectionTime = etCollectionTime.getText().toString();
            String restaurantInput = etRestaurant.getText().toString().trim();
            List<String> newRestaurantList = new ArrayList<>();
            if (!restaurantInput.isEmpty()) {
                String[] parts = restaurantInput.split("\\s*,\\s*"); // 支援「A,B」或「A, B」
                for (String part : parts) {
                    if (!part.isEmpty()) {
                        newRestaurantList.add(part);
                    }
                }
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("groups")
                    .whereEqualTo("name", groupName)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                            String docId = doc.getId();

                            db.collection("groups").document(docId)
                                    .update(
                                            "name", newName,
                                            "phone", newPhone,
                                            "location", newLocation,
                                            "orderingTime", newOrderingTime,
                                            "collectionTime", newCollectionTime,
                                            "restaurant", newRestaurantList
                                    )
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                                        finish(); // 返回上一頁
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "更新失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                        }
                    })
                    .addOnFailureListener(e -> {
                    });
        });
    }
}
