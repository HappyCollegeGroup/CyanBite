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
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import fcu.app.cyanbite.R;

public class LoginActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView tvGoRegister;
    private EditText etAccount, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        setupListener();
    }

    private void initView() {
        tvGoRegister = findViewById(R.id.tv_go_register);
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
    }

    private void setupListener() {
        tvGoRegister.setOnClickListener(view -> {
            navigateTo(this, RegisterActivity.class);
        });


        btnLogin.setOnClickListener(view -> {
            String account = etAccount.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            handleLogin(account, password);
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void handleLogin(String account, String password) {
        btnLogin.setEnabled(false);

        if (account.isEmpty() || password.isEmpty()) {
            showMessage(getString(R.string.login_should_not_empty));
            btnLogin.setEnabled(true);
            return;
        }

        mAuth.signInWithEmailAndPassword(account, password)
                .addOnSuccessListener(aVoid -> {
                    navigateTo(this, MainActivity.class);
                })
                .addOnFailureListener(e -> {
                    String error = getString(R.string.login_fail) + ": " + e.getMessage();
                    showMessage(error);
                })
                .addOnCompleteListener(task ->  {
                    btnLogin.setEnabled(true);
                });
    }
}