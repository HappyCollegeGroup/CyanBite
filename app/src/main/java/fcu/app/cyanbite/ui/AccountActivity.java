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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fcu.app.cyanbite.R;

public class AccountActivity extends AppCompatActivity {

    private EditText etAccount, etOldPassword, etNewPassword;
    private Button btnReturn, btnSave;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etAccount = findViewById(R.id.et_account);
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        btnReturn = findViewById(R.id.btn_return);
        btnSave = findViewById(R.id.btn_save);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = etOldPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                user.reauthenticate(credential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                user.updatePassword(newPassword)
                                        .addOnSuccessListener(aVoid ->
                                                Toast.makeText(AccountActivity.this, "修改成功", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(AccountActivity.this, "修改失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show());;
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(AccountActivity.this, "修改失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        etAccount.setText(user.getEmail());
    }
}