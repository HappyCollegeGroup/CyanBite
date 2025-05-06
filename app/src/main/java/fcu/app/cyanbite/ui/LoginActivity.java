package fcu.app.cyanbite.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import fcu.app.cyanbite.R;

public class LoginActivity extends AppCompatActivity {
    private TextView tvGoRegister;
    private EditText etAccount, etPassword;
    private Button btnLogin;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        auth();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvGoRegister = findViewById(R.id.tv_go_register);
        tvGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo(RegisterActivity.class);
            }
        });

        etAccount = findViewById(R.id.et_login_account);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = etAccount.getText().toString();
                String password = etPassword.getText().toString();
                login(account, password);
            }
        });
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(LoginActivity.this, cls);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void auth() {
        boolean isLogin = prefs.getBoolean("is_login", false);

        if (isLogin) {
            navigateTo(MainActivity.class);
        }
    }

    private void login(String account, String password) {
        if ("CyanBite".equals(account) && "123".equals(password)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_login", true);
            editor.apply();

            navigateTo(MainActivity.class);
        } else {
            Toast.makeText(this, "帳號或密碼錯誤!", Toast.LENGTH_LONG).show();
        }
    }
}