package fcu.app.cyanbite.ui;

import static fcu.app.cyanbite.util.Util.navigateTo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import fcu.app.cyanbite.R;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView tvGoLogin;
    private EditText etAccount, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        initView();
        setupListener();
    }

    private void initView() {
        tvGoLogin = findViewById(R.id.tv_go_login);
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void setupListener() {
        tvGoLogin.setOnClickListener(view -> {
            navigateTo(this, LoginActivity.class);
        });


        btnRegister.setOnClickListener(view -> {
            String account = etAccount.getText().toString();
            String password = etPassword.getText().toString();
            register(account, password);
        });
    }

    private void showMessage(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void register(String account, String password) {
        btnRegister.setEnabled(false);

        if (account.isEmpty() || password.isEmpty()) {
            showMessage(getString(R.string.login_should_not_empty));
            btnRegister.setEnabled(true);
            return;
        }

        mAuth.createUserWithEmailAndPassword(account, password)
                .addOnSuccessListener(aVoid -> {
                    navigateTo(this, MainActivity.class);
                })
                .addOnFailureListener(e -> {
                    showMessage(getString(R.string.signup_fail) + ": " + e.getMessage());
                })
                .addOnCompleteListener(task ->  {
                    btnRegister.setEnabled(true);
                });
    }
}