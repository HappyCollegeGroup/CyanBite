package fcu.app.cyanbite.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import fcu.app.cyanbite.R;

public class ProfileActivity extends AppCompatActivity {

    private Button btnReturn, btnSave;
    private EditText etName, etPhone, etAddress;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnReturn = findViewById(R.id.btn_return);
        btnSave = findViewById(R.id.btn_save);
        etName = findViewById(R.id.et_account);
        etPhone = findViewById(R.id.et_old_password);
        etAddress = findViewById(R.id.et_address);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uid = user.getUid();

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();

                Map<String, Object> profile = new HashMap<>();
                profile.put("name", name);
                profile.put("phone", phone);
                profile.put("address", address);

                db.collection("profile").document(uid)
                        .set(profile, SetOptions.merge())
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(ProfileActivity.this, "儲存成功", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(ProfileActivity.this, "儲存失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show());

            }
        });

        db.collection("profile").document(uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    etName.setText((String)querySnapshot.get("name"));
                    etPhone.setText((String)querySnapshot.get("phone"));
                    etAddress.setText((String)querySnapshot.get("address"));
                });
    }
}