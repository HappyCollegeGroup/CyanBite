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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fcu.app.cyanbite.R;

public class RegisterActivity extends AppCompatActivity {
    private TextView tvGoLogin;
    private EditText etAccount, etPassword;
    private Button btnRegister;
//    private SharedPreferences prefs;
    private FirebaseAuth mAuth;

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

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
//        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

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
        mAuth.createUserWithEmailAndPassword(account, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            SharedPreferences.Editor editor = prefs.edit();
//                            editor.putBoolean("is_login", true);
//                            editor.apply();

                            navigateTo(MainActivity.class);
                        } else {
                            Toast.makeText(RegisterActivity.this, "註冊失敗, " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}