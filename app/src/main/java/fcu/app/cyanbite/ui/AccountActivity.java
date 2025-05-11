package fcu.app.cyanbite.ui;

import static fcu.app.cyanbite.util.Util.setStatusBar;
import static fcu.app.cyanbite.util.Util.slideBack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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
    }

    private void initView() {
        etAccount = findViewById(R.id.et_account);
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        btnReturn = findViewById(R.id.btn_return);
        btnSave = findViewById(R.id.btn_save);
    }

    private void setupListener() {
        btnReturn.setOnClickListener(view ->  {
            finish();
        });

        btnSave.setOnClickListener(view ->  {
            String oldPassword = etOldPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                user.reauthenticate(credential)
                    .addOnSuccessListener(task -> {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(aVoid -> {
                                    showMessage(getString(R.string.save_success));
                                })
                                .addOnFailureListener(e -> {
                                    showMessage(getString(R.string.save_fail) + ": " + e.getMessage());
                                });
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
        etAccount.setText(user.getEmail());
    }
}