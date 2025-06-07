package fcu.app.cyanbite.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fcu.app.cyanbite.R;

public class GroupDetailActivity extends AppCompatActivity {

    private String originalGroupName;
    private FirebaseFirestore db; // Declare Firestore instance at class level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Get data from the Intent
        originalGroupName = getIntent().getStringExtra("groupName");
        String groupPhone = getIntent().getStringExtra("groupPhone");
        String groupLocation = getIntent().getStringExtra("groupLocation");
        String orderingTime = getIntent().getStringExtra("orderingTime");
        String collectionTime = getIntent().getStringExtra("collectionTime");

        String groupCity = getIntent().getStringExtra("groupCity");
        String groupDistrict = getIntent().getStringExtra("groupDistrict");
        String groupDescription = getIntent().getStringExtra("groupDescription");


        List<String> restaurantPaths = getIntent().getStringArrayListExtra("restaurantPaths");

        // Initialize EditText fields
        EditText etGroupName = findViewById(R.id.et_group_detail_name);
        EditText etGroupPhone = findViewById(R.id.et_group_detail_phone);
        EditText etGroupLocation = findViewById(R.id.et_group_detail_location);
        EditText etOrderingTime = findViewById(R.id.et_group_detail_ordering_time);
        EditText etCollectionTime = findViewById(R.id.et_group_detail_collection_time);
        EditText etRestaurant = findViewById(R.id.et_group_detail_restaurant);

        EditText etGroupCity = findViewById(R.id.et_group_detail_city);
        EditText etGroupDistrict = findViewById(R.id.et_group_detail_district);
        EditText etGroupDescription = findViewById(R.id.et_group_detail_description);



        // Set initial values to EditText fields
        etGroupName.setText(originalGroupName);
        etGroupPhone.setText(groupPhone);
        etGroupLocation.setText(groupLocation);
        etOrderingTime.setText(orderingTime);
        etCollectionTime.setText(collectionTime);

        etGroupCity.setText(groupCity);
        etGroupDistrict.setText(groupDistrict);
        etGroupDescription.setText(groupDescription);


        if (restaurantPaths != null && !restaurantPaths.isEmpty()) {
            StringBuilder restaurantNamesBuilder = new StringBuilder();
            final int[] completedQueries = {0}; // To track completion of all queries

            for (String path : restaurantPaths) {
                db.document(path).get() // Get the document snapshot for the given path
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Assuming your restaurant document has a 'name' field
                                String name = documentSnapshot.getString("name");
                                if (name != null) {
                                    if (restaurantNamesBuilder.length() > 0) {
                                        restaurantNamesBuilder.append(", ");
                                    }
                                    restaurantNamesBuilder.append(name);
                                }
                            }
                            completedQueries[0]++;
                            // Check if all restaurant names have been fetched
                            if (completedQueries[0] == restaurantPaths.size()) {
                                etRestaurant.setText(restaurantNamesBuilder.toString());
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle error for fetching a single restaurant document
                            Toast.makeText(this, "無法載入餐廳資訊: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            completedQueries[0]++; // Still increment to avoid blocking display
                            if (completedQueries[0] == restaurantPaths.size()) {
                                etRestaurant.setText(restaurantNamesBuilder.toString());
                            }
                        });
            }
        } else {
            etRestaurant.setText(""); // No restaurants to display
        }


        Button btnSave = findViewById(R.id.btn_save_group);
        btnSave.setOnClickListener(v -> {
            String newName = etGroupName.getText().toString();
            String newPhone = etGroupPhone.getText().toString();
            String newLocation = etGroupLocation.getText().toString();
            String newOrderingTime = etOrderingTime.getText().toString();
            String newCollectionTime = etCollectionTime.getText().toString();
            String restaurantInput = etRestaurant.getText().toString().trim();

            String newCity = etGroupCity.getText().toString().trim();
            String newDistrict = etGroupDistrict.getText().toString().trim();
            String newDescription = etGroupDescription.getText().toString().trim();



            List<String> inputRestaurantNames = new ArrayList<>();
            if (!restaurantInput.isEmpty()) {
                String[] parts = restaurantInput.split("\\s*,\\s*");
                for (String part : parts) {
                    if (!part.isEmpty()) {
                        inputRestaurantNames.add(part);
                    }
                }
            }

            // Find the group document using its original name
            db.collection("groups")
                    .whereEqualTo("name", originalGroupName)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                            String docId = doc.getId();


                            List<DocumentReference> newRestaurantReferences = new ArrayList<>();
                            if (inputRestaurantNames.isEmpty()) {
                                updateGroup(docId, newName, newPhone, newLocation, newOrderingTime, newCollectionTime, newCity, newDistrict, newDescription, newRestaurantReferences);
                            } else {
                                final int[] completedRestaurantQueries = {0};
                                for (String rName : inputRestaurantNames) {
                                    // 注意：這裡假設您的餐廳集合名稱是 "restaurant" (單數)
                                    db.collection("restaurant")
                                            .whereEqualTo("name", rName) // Query by restaurant name
                                            .limit(1)
                                            .get()
                                            .addOnSuccessListener(restaurantQuerySnapshot -> {
                                                if (!restaurantQuerySnapshot.isEmpty()) {
                                                    // Add the DocumentReference of the found restaurant
                                                    newRestaurantReferences.add(restaurantQuerySnapshot.getDocuments().get(0).getReference());
                                                } else {
                                                    Toast.makeText(this, "找不到餐廳: " + rName + "，將不會更新該餐廳。", Toast.LENGTH_SHORT).show();
                                                }
                                                completedRestaurantQueries[0]++;
                                                // Check if all restaurant queries are done
                                                if (completedRestaurantQueries[0] == inputRestaurantNames.size()) {
                                                    // Only update the group once all restaurant references are resolved
                                                    updateGroup(docId, newName, newPhone, newLocation, newOrderingTime, newCollectionTime, newCity, newDistrict, newDescription, newRestaurantReferences);
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "查詢餐廳失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                completedRestaurantQueries[0]++;
                                                if (completedRestaurantQueries[0] == inputRestaurantNames.size()) {
                                                    updateGroup(docId, newName, newPhone, newLocation, newOrderingTime, newCollectionTime, newCity, newDistrict, newDescription, newRestaurantReferences);
                                                }
                                            });
                                }
                            }
                        } else {
                            Toast.makeText(this, "找不到該群組來更新。", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "查詢群組失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        Button btnDelete = findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(v -> {
            // Find the group document by its original name first
            db.collection("groups")
                    .whereEqualTo("name", originalGroupName)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            // Get the document ID of the group to delete
                            String docId = querySnapshot.getDocuments().get(0).getId();

                            // Delete the document
                            db.collection("groups").document(docId)
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "群組已成功刪除！", Toast.LENGTH_SHORT).show();
                                        finish(); // Return to the previous activity (e.g., GroupFragment)
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "刪除群組失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(this, "找不到該群組來刪除。", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "查詢群組失敗，無法執行刪除：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void updateGroup(String docId, String newName, String newPhone, String newLocation,
                             String newOrderingTime, String newCollectionTime, String newCity, String newDistrict,
                             String newDescription,
                             List<DocumentReference> newRestaurantReferences) {
        db.collection("groups").document(docId)
                .update(
                        "name", newName,
                        "phone", newPhone,
                        "place", newLocation,
                        "orderTime", newOrderingTime,
                        "collectionTime", newCollectionTime,

                        "city", newCity,
                        "district", newDistrict,
                        "description", newDescription,

                        "restaurant", newRestaurantReferences
                )
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the previous screen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "更新失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}