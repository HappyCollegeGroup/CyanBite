package fcu.app.cyanbite.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Restaurant;

public class AddNewGroupActivity extends AppCompatActivity implements OnGroupSwitchListener, OnTabSwitchListener {

    private Button btnReturn;
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
        tvInfoNumber = findViewById(R.id.tv_info_number);
        tvInfo = findViewById(R.id.tv_group_info);
        tvMenuNumber = findViewById(R.id.tv_restaurant_number);
        tvMenu = findViewById(R.id.tv_group_restaurant);

        addNewGroupInfoFragment = new AddNewGroupInfoFragment();
        addNewGroupRestaurantFragment = new AddNewGroupRestaurantFragment();

        btnReturn.setOnClickListener(v -> finish());

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

    private Restaurant restaurant;
    @Override
    public void onSwitchToMenu(Restaurant restaurant) {
        this.restaurant = restaurant;  // 儲存餐廳物件
        Fragment nextFragment = new RestaurantShowMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("restaurant_data", restaurant);
        nextFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, nextFragment)
                .addToBackStack(null)
                .commit();
    }



    @Override
    public void onSwitchToInfo() {
        if (restaurant != null) {
            RestaurantShowInfoFragment infoFragment = new RestaurantShowInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurant_data", restaurant);
            infoFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_manage_restaurant, infoFragment)
                    .addToBackStack(null)
                    .commit();
            updateStyle(true);
        }
    }


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
