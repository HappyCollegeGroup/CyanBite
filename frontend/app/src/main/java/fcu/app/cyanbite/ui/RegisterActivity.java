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
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import fcu.app.cyanbite.R;

public class RegisterActivity extends AppCompatActivity {
    private TextView tvGoLogin;
    private EditText etAccount, etPassword;
    private Button btnRegister;
    private SharedPreferences prefs;

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

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        tvGoLogin = findViewById(R.id.tv_go_login);
        tvGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo(LoginActivity.class);
            }
        });

        etAccount = findViewById(R.id.et_register_account);
        etPassword = findViewById(R.id.et_register_password);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = etAccount.getText().toString();
                String password = etPassword.getText().toString();
                register(account, password);
            }
        });
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(RegisterActivity.this, cls);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void register(String account, String password) {
        if ("CyanBite".equals(account)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_login", true);
            editor.apply();

            navigateTo(MainActivity.class);
        } else {
            Toast.makeText(this, "帳號已存在", Toast.LENGTH_LONG).show();
        }
    }
}