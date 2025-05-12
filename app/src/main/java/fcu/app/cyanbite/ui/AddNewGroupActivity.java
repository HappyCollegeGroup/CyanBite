package fcu.app.cyanbite.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.ui.OnGroupSwitchListener;

public class AddNewGroupActivity extends AppCompatActivity implements OnGroupSwitchListener {

    private Button btnReturn;
    private EditText etGroupName;
    private EditText etGroupPhone;
    private EditText etGroupLocation;
    private EditText etGroupOrderingTime;
    private EditText etGroupCollectionTime;
    private EditText etGroupRestaurant;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnReturn = findViewById(R.id.btn_return);
        etGroupName = findViewById(R.id.et_group_name);
        etGroupPhone = findViewById(R.id.et_group_phone);
        etGroupLocation = findViewById(R.id.et_group_location);
        etGroupOrderingTime = findViewById(R.id.et_group_ordering_time);
        etGroupCollectionTime = findViewById(R.id.et_group_collection_time);
        etGroupRestaurant = findViewById(R.id.et_group_restaurant);

        btnReturn.setOnClickListener(v -> finish());

        // 初次啟動載入 Info Fragment
        if (savedInstanceState == null) {
            AddNewGroupInfoFragment fragment = new AddNewGroupInfoFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView, fragment);
            transaction.commit();
        }
    }

    @Override
    public void onSwitchToGroupInfo() {
        replaceFragment(new AddNewGroupInfoFragment());
    }

    @Override
    public void onSwitchToGroupMenu() {
        replaceFragment(new AddNewGroupRestaurantFragment());
    }

    @Override
    public void onGroupTabSwitched(int index) {
        if (index == 0) {
            onSwitchToGroupInfo();
        } else if (index == 1) {
            onSwitchToGroupMenu();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.commit();
    }
}
