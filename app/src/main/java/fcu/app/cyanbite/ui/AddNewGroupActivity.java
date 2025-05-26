package fcu.app.cyanbite.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.ui.OnGroupSwitchListener;

public class AddNewGroupActivity extends AppCompatActivity implements OnGroupSwitchListener {

    private Button btnReturn;
//    private EditText etGroupName;
//    private EditText etGroupPhone;
//    private EditText etGroupLocation;
//    private EditText etGroupOrderingTime;
//    private EditText etGroupCollectionTime;
//    private EditText etGroupRestaurant;
//    private FirebaseFirestore db;
    private AddNewGroupInfoFragment addNewGroupInfoFragment;
    private AddNewGroupRestaurantFragment addNewGroupRestaurantFragment;

    private TextView tvInfoNumber, tvInfo, tvMenuNumber, tvMenu;

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
//        etGroupName = findViewById(R.id.et_group_name);
//        etGroupPhone = findViewById(R.id.et_group_phone);
//        etGroupLocation = findViewById(R.id.et_group_location);
//        etGroupOrderingTime = findViewById(R.id.et_group_ordering_time);
//        etGroupCollectionTime = findViewById(R.id.et_group_collection_time);
//        etGroupRestaurant = findViewById(R.id.et_group_restaurant);


        tvInfoNumber = findViewById(R.id.tv_info_number);
        tvInfo = findViewById(R.id.tv_group_info);
        tvMenuNumber = findViewById(R.id.tv_restaurant_number);
        tvMenu = findViewById(R.id.tv_group_restaurant);

        addNewGroupInfoFragment = new AddNewGroupInfoFragment();
        addNewGroupRestaurantFragment = new AddNewGroupRestaurantFragment();

        btnReturn.setOnClickListener(v -> finish());

        // 初次啟動載入 Info Fragment
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView, addNewGroupInfoFragment);
            transaction.commit();


        }
    }

    @Override
    public void onSwitchToGroupInfo() {
        replaceFragment(addNewGroupInfoFragment);
        updateStyle(true);

    }

    @Override
    public void onSwitchToGroupMenu(Bundle bundle) {
        addNewGroupRestaurantFragment.addArgument(bundle);
        replaceFragment(addNewGroupRestaurantFragment);
        updateStyle(false);

    }

    @Override
    public void onSwitchToGroup() {
        finish();
    }

//    @Override
//    public void onGroupTabSwitched(int index) {
//        if (index == 0) {
//            onSwitchToGroupInfo();
//            updateStyle(false);
//        } else if (index == 1) {
//            onSwitchToGroupMenu();
//            updateStyle(true);
//        }
//    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.commit();

    }

    private void updateStyle(boolean isInfo) {
        Log.d("erro", "no change");
        if (isInfo) {
            tvInfoNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_filled));
            tvInfoNumber.setTextColor(Color.parseColor("#FFFFFF"));
            tvInfo.setTextColor(Color.parseColor("#00BCD4"));

            tvMenuNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_border));
            tvMenuNumber.setTextColor(Color.parseColor("#000000"));
            tvMenu.setTextColor(Color.parseColor("#000000"));
        } else {
            tvMenuNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_filled));
            tvMenuNumber.setTextColor(Color.parseColor("#FFFFFF"));
            tvMenu.setTextColor(Color.parseColor("#00BCD4"));

            tvInfoNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_border));
            tvInfoNumber.setTextColor(Color.parseColor("#000000"));
            tvInfo.setTextColor(Color.parseColor("#000000"));
        }
    }
}
