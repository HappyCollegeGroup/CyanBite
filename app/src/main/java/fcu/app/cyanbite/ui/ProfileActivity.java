package fcu.app.cyanbite.ui;

import static fcu.app.cyanbite.util.Util.setStatusBar;
import static fcu.app.cyanbite.util.Util.slideBack;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import fcu.app.cyanbite.R;

public class ProfileActivity extends AppCompatActivity {
    public static final String COLLECTION_PROFILE = "profile";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_DISTRICT = "district";
//    public static final String FIELD_ROAD = "road";
//    public static final String FIELD_NUMBER = "number";
    private EditText etName, etPhone, etAddress;
    private Button btnReturn, btnSave;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String uid;
    private String address, city, district, road, number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        initFirebase();
        initView();
        setupListener();
        showEditTextValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatusBar(this, true);
    }

    @Override
    public void finish() {
        super.finish();
        slideBack(this);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uid = user.getUid();
    }

    private void initView() {
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        btnSave = findViewById(R.id.btn_save);
        btnReturn = findViewById(R.id.btn_return);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    address = result.getData().getStringExtra("address");
                    city = result.getData().getStringExtra("city");
                    district = result.getData().getStringExtra("district");
                    road = result.getData().getStringExtra("road");
                    number = result.getData().getStringExtra("number");
                    etAddress.setText(address);
                }
            });

    private void setupListener() {
        etAddress.setOnClickListener(view -> {
            Intent intent = new Intent(this, PlaceSelectionActivity.class);
            intent.putExtra("initial_address", String.valueOf(etAddress.getText()));
            activityResultLauncher.launch(intent);
        });

        btnReturn.setOnClickListener(view -> {
            finish();
        });

        btnSave.setOnClickListener(view -> {
            btnSave.setEnabled(false);

            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                showMessage("請輸入所有欄位!");
            } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
                showMessage("請輸入合法的電話號碼!");
            } else {
                Map<String, Object> profile = new HashMap<>();
                profile.put(FIELD_NAME, name);
                profile.put(FIELD_PHONE, phone);
                profile.put(FIELD_ADDRESS, address);
                profile.put(FIELD_CITY, city);
                profile.put(FIELD_DISTRICT, district);
//                profile.put(FIELD_ROAD, road);
//                profile.put(FIELD_NUMBER, number);

                db.collection(COLLECTION_PROFILE).document(uid)
                        .set(profile, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            showMessage(getString(R.string.save_success));
                        })
                        .addOnFailureListener(e -> {
                            showMessage(getString(R.string.save_fail) + ": " + e.getMessage());
                        })
                        .addOnCompleteListener(e -> {
                            btnSave.setEnabled(true);
                        });
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showEditTextValue() {
        db.collection(COLLECTION_PROFILE).document(uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    etName.setText((String)querySnapshot.get(FIELD_NAME));
                    etPhone.setText((String)querySnapshot.get(FIELD_PHONE));
                    etAddress.setText((String)querySnapshot.get(FIELD_ADDRESS));
                });
    }
}