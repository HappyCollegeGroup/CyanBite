package fcu.app.cyanbite.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcu.app.cyanbite.R;

public class GroupDetailActivity extends AppCompatActivity {

    private String originalGroupImage, originalGroupName;
    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picking
    private String currentGroupImageBase64; // Stores the Base64 string of the current image
    private ImageButton imgButtonGroupDetailPic; // Reference to the ImageButton in the layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_detail);

        Button btnReturn = findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        // Retrieve all group data passed from the initiating activity via Intent
        originalGroupName = getIntent().getStringExtra("groupName");
        String groupPhone = getIntent().getStringExtra("groupPhone");
        String groupLocation = getIntent().getStringExtra("groupLocation");
        String orderingTime = getIntent().getStringExtra("orderingTime");
        String collectionTime = getIntent().getStringExtra("collectionTime");
        String groupCity = getIntent().getStringExtra("groupCity");
        String groupDistrict = getIntent().getStringExtra("groupDistrict");
        String groupDescription = getIntent().getStringExtra("groupDescription");
        currentGroupImageBase64 = getIntent().getStringExtra("groupImage"); // Get the Base64 image string

        List<String> restaurantPaths = getIntent().getStringArrayListExtra("restaurantPaths");

        // Initialize UI components (EditTexts and ImageButton) by finding their IDs
        EditText etGroupName = findViewById(R.id.et_group_detail_name);
        EditText etGroupPhone = findViewById(R.id.et_group_detail_phone);
        EditText etGroupLocation = findViewById(R.id.et_group_detail_location);
        EditText etOrderingTime = findViewById(R.id.et_group_detail_ordering_time);
        EditText etCollectionTime = findViewById(R.id.et_group_detail_collection_time);
        EditText etRestaurant = findViewById(R.id.et_group_detail_restaurant);
        EditText etGroupCity = findViewById(R.id.et_group_detail_city);
        EditText etGroupDistrict = findViewById(R.id.et_group_detail_district);
        EditText etGroupDescription = findViewById(R.id.et_group_detail_description);
        imgButtonGroupDetailPic = findViewById(R.id.img_btn_group_detail_pic);


        // Set the retrieved data to the corresponding EditText fields
        etGroupName.setText(originalGroupName);
        etGroupPhone.setText(groupPhone);
        etGroupLocation.setText(groupLocation);
        etOrderingTime.setText(orderingTime);
        etCollectionTime.setText(collectionTime);
        etGroupCity.setText(groupCity);
        etGroupDistrict.setText(groupDistrict);
        etGroupDescription.setText(groupDescription);

        // --- Image Display Logic ---
        // Check if a Base64 image string exists and is not empty
        if (currentGroupImageBase64 != null && !currentGroupImageBase64.isEmpty()) {
            try {
                // Decode the Base64 string into a Bitmap
                Bitmap bitmap = decodeImageFromDataUrl(currentGroupImageBase64);
                if (bitmap != null) {
                    // If decoding is successful, display the Bitmap on the ImageButton
                    imgButtonGroupDetailPic.setImageBitmap(bitmap);
                } else {
                    // If decoding fails (e.g., corrupted data), show a default image and a toast
                    imgButtonGroupDetailPic.setImageResource(R.drawable.camera); // Default camera icon
                    Toast.makeText(this, "圖片格式錯誤，無法載入。", Toast.LENGTH_SHORT).show();
                }
            } catch (IllegalArgumentException e) {
                // Catch specific errors if the Base64 string format is invalid
                Toast.makeText(this, "圖片資料無效: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                imgButtonGroupDetailPic.setImageResource(R.drawable.camera); // Default camera icon
            }
        } else {
            // If no image data is provided, set the default camera icon
            imgButtonGroupDetailPic.setImageResource(R.drawable.camera);
        }

        // Set a click listener for the ImageButton to allow users to pick a new image
        imgButtonGroupDetailPic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // --- Restaurant Name Display Logic ---
        // Fetch and display restaurant names based on their Firestore document paths
        if (restaurantPaths != null && !restaurantPaths.isEmpty()) {
            StringBuilder restaurantNamesBuilder = new StringBuilder();
            final int[] completedQueries = {0}; // Counter to track completion of async queries

            for (String path : restaurantPaths) {
                db.document(path).get() // Get document from the specified path
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String name = documentSnapshot.getString("name");
                                if (name != null) {
                                    if (restaurantNamesBuilder.length() > 0) {
                                        restaurantNamesBuilder.append(", "); // Add comma for multiple names
                                    }
                                    restaurantNamesBuilder.append(name);
                                }
                            }
                            completedQueries[0]++;
                            // After all restaurant queries complete, set the text
                            if (completedQueries[0] == restaurantPaths.size()) {
                                etRestaurant.setText(restaurantNamesBuilder.toString());
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "無法載入餐廳資訊: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            completedQueries[0]++;
                            // Ensure text is set even if some queries fail
                            if (completedQueries[0] == restaurantPaths.size()) {
                                etRestaurant.setText(restaurantNamesBuilder.toString());
                            }
                        });
            }
        } else {
            etRestaurant.setText(""); // If no restaurant paths, set text to empty
        }

        // --- Save Button Logic ---
        Button btnSave = findViewById(R.id.btn_save_group);
        btnSave.setOnClickListener(v -> {
            // Get the potentially updated values from the EditTexts
            String newName = etGroupName.getText().toString();
            String newPhone = etGroupPhone.getText().toString();
            String newLocation = etGroupLocation.getText().toString();
            String newOrderingTime = etOrderingTime.getText().toString();
            String newCollectionTime = etCollectionTime.getText().toString();
            String restaurantInput = etRestaurant.getText().toString().trim();
            String newCity = etGroupCity.getText().toString().trim();
            String newDistrict = etGroupDistrict.getText().toString().trim();
            String newDescription = etGroupDescription.getText().toString().trim();

            // Parse the restaurant input string into a list of names
            List<String> inputRestaurantNames = new ArrayList<>();
            if (!restaurantInput.isEmpty()) {
                String[] parts = restaurantInput.split("\\s*,\\s*"); // Split by comma, trimming spaces
                for (String part : parts) {
                    if (!part.isEmpty()) {
                        inputRestaurantNames.add(part);
                    }
                }
            }

            // Find the current group document in Firestore using its original name
            db.collection("group")
                    .whereEqualTo("name", originalGroupName)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                            String docId = doc.getId(); // Get the document's ID for updating

                            List<DocumentReference> newRestaurantReferences = new ArrayList<>();
                            if (inputRestaurantNames.isEmpty()) {
                                // If no restaurant names are entered, update the group without any restaurant references
                                updateGroup(docId, newName, newPhone, newLocation, newOrderingTime, newCollectionTime, newCity, newDistrict, newDescription, currentGroupImageBase64, newRestaurantReferences);
                            } else {
                                // Query for each restaurant name to get its DocumentReference
                                final int[] completedRestaurantQueries = {0};
                                for (String rName : inputRestaurantNames) {
                                    db.collection("restaurant")
                                            .whereEqualTo("name", rName)
                                            .limit(1)
                                            .get()
                                            .addOnSuccessListener(restaurantQuerySnapshot -> {
                                                if (!restaurantQuerySnapshot.isEmpty()) {
                                                    // Add the DocumentReference if the restaurant is found
                                                    newRestaurantReferences.add(restaurantQuerySnapshot.getDocuments().get(0).getReference());
                                                } else {
                                                    Toast.makeText(this, "找不到餐廳: " + rName + "，將不會更新該餐廳。", Toast.LENGTH_SHORT).show();
                                                }
                                                completedRestaurantQueries[0]++;
                                                // Only update the group after all restaurant queries have completed
                                                if (completedRestaurantQueries[0] == inputRestaurantNames.size()) {
                                                    updateGroup(docId, newName, newPhone, newLocation, newOrderingTime, newCollectionTime, newCity, newDistrict, newDescription, currentGroupImageBase64, newRestaurantReferences);
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "查詢餐廳失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                completedRestaurantQueries[0]++;
                                                if (completedRestaurantQueries[0] == inputRestaurantNames.size()) {
                                                    updateGroup(docId, newName, newPhone, newLocation, newOrderingTime, newCollectionTime, newCity, newDistrict, newDescription, currentGroupImageBase64, newRestaurantReferences);
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
            // Find the group document by its original name to get its ID for deletion
            db.collection("group")
                    .whereEqualTo("name", originalGroupName)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            String docId = querySnapshot.getDocuments().get(0).getId(); // Get the document ID

                            db.collection("groups").document(docId)
                                    .delete() // Perform the delete operation
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "群組已成功刪除！", Toast.LENGTH_SHORT).show();
                                        finish(); // Close the activity after successful deletion
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


        Button btnShowList = findViewById(R.id.btn_list);
        btnShowList.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetailActivity.this, GroupShowBuyerActivity.class);
            intent.putExtra("name", originalGroupName);
            startActivity(intent);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result from picking an image from the gallery
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData(); // Get the URI of the selected image
            try {
                // Decode the selected image URI into a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgButtonGroupDetailPic.setImageBitmap(bitmap); // Display the new image

                // Encode the newly selected image to Base64 and update currentGroupImageBase64
                currentGroupImageBase64 = encodeImageToDataUrl(imageUri);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "載入圖片失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String encodeImageToDataUrl(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream); // Decode the original bitmap

        Bitmap resizedBitmap = resizeBitmap(originalBitmap, 800); // Resize for efficiency

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream); // Compress with 70% quality
        byte[] byteArray = outputStream.toByteArray();

        String mimeType = getContentResolver().getType(imageUri); // Get MIME type (e.g., image/jpeg)
        // Construct and return the Data URL string
        return "data:" + mimeType + ";base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }


    private Bitmap decodeImageFromDataUrl(String dataUrl) {
        // Find the index of the comma, which separates the metadata from the actual Base64 data
        int commaIndex = dataUrl.indexOf(',');
        if (commaIndex != -1 && dataUrl.length() > commaIndex + 1) {
            String base64String = dataUrl.substring(commaIndex + 1); // Extract the pure Base64 part
            try {
                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT); // Decode Base64 to byte array
                return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length); // Convert byte array to Bitmap
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return null; // Return null if Base64 string is invalid
            }
        }
        return null; // Return null if data URL format is incorrect
    }


    private Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale = Math.min((float) maxSize / width, (float) maxSize / height);
        if (scale >= 1) return bitmap; // No scaling needed if the image is already smaller than maxSize

        int newWidth = Math.round(scale * width);
        int newHeight = Math.round(scale * height);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true); // Create a new scaled bitmap
    }


    private void updateGroup(String docId, String newName, String newPhone, String newLocation,
                             String newOrderingTime, String newCollectionTime, String newCity, String newDistrict,
                             String newDescription, String newGroupImageBase64,
                             List<DocumentReference> newRestaurantReferences) {
        // Create a Map to hold the fields to be updated
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("phone", newPhone);
        updates.put("place", newLocation);
        updates.put("orderTime", newOrderingTime);
        updates.put("collectionTime", newCollectionTime);
        updates.put("city", newCity);
        updates.put("district", newDistrict);
        updates.put("description", newDescription);
        updates.put("image", newGroupImageBase64); // Update the image field with the new Base64 string
        updates.put("restaurant", newRestaurantReferences); // Update restaurant references list

        // Perform the update operation on the specific document in Firestore
        db.collection("group").document(docId)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after a successful update
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "更新失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}