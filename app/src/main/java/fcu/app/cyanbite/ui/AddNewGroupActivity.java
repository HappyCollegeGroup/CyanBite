package fcu.app.cyanbite.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import fcu.app.cyanbite.R;

public class AddNewGroupActivity extends AppCompatActivity {

    private EditText etGroupName;
    private EditText etGroupPhone;
    private EditText etGroupLocation;
    private EditText etGroupOrderingTime;
    private EditText etGroupCollectionTime;
    private EditText etGroupRestaurant;
    private Button addNewGroupSubmit;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        etGroupName = findViewById(R.id.et_group_name);
        etGroupPhone = findViewById(R.id.etp_group_phone);
        etGroupLocation = findViewById(R.id.et_group_location);
        etGroupOrderingTime = findViewById(R.id.et_group_ordering_time);
        etGroupCollectionTime = findViewById(R.id.et_group_collection_time);
        etGroupRestaurant = findViewById(R.id.et_group_restaurant);
        addNewGroupSubmit = findViewById(R.id.btn_submit_add_new_group);

        addNewGroupSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = etGroupName.getText().toString().trim();
                String groupPhone = etGroupPhone.getText().toString().trim();
                String groupLocation = etGroupLocation.getText().toString().trim();
                String orderingTime = etGroupOrderingTime.getText().toString().trim();
                String collectionTime = etGroupCollectionTime.getText().toString().trim();
                String restaurant = etGroupRestaurant.getText().toString().trim();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (groupName.isEmpty() || groupPhone.isEmpty() || groupLocation.isEmpty()
                        || orderingTime.isEmpty() || collectionTime.isEmpty() || restaurant.isEmpty()) {
                    Toast.makeText(AddNewGroupActivity.this, "請填寫所有欄位", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> group = new HashMap<>();
                group.put("name", groupName);
                group.put("phone", groupPhone);
                group.put("location", groupLocation);
                group.put("orderingTime", orderingTime);
                group.put("collectionTime", collectionTime);
                group.put("restaurant", restaurant);


                db.collection("groups")
                        .add(group)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddNewGroupActivity.this, "新增團購成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddNewGroupActivity.this, GroupFragment.class);
                                setResult(RESULT_OK);
                                finish();
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddNewGroupActivity.this, "新增失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

}