package fcu.app.cyanbite.ui;

import static fcu.app.cyanbite.util.Util.setStatusBar;
import static fcu.app.cyanbite.util.Util.slideBack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
    private EditText etName, etPhone, etAddress;
    private Button btnReturn, btnSave;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String uid;

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

    private void setupListener() {
        btnReturn.setOnClickListener(view -> {
            finish();
        });

        btnSave.setOnClickListener(view -> {
            btnSave.setEnabled(false);

            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String address = etAddress.getText().toString();

            Map<String, Object> profile = new HashMap<>();
            profile.put(FIELD_NAME, name);
            profile.put(FIELD_PHONE, phone);
            profile.put(FIELD_ADDRESS, address);

            db.collection(COLLECTION_PROFILE).document(uid)
                    .set(profile, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        showMessage(getString(R.string.save_success));
                    })
                    .addOnFailureListener(e -> {
                        showMessage(getString(R.string.save_fail) + ": " + e.getMessage());
                    });
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