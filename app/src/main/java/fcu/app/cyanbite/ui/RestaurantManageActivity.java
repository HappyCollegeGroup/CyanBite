package fcu.app.cyanbite.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Restaurant;

public class RestaurantManageActivity extends AppCompatActivity  implements OnTabSwitchListener   {

    private TextView tvInfoNumber, tvInfo, tvMenuNumber, tvMenu;
    private Restaurant restaurant;
    @Override
    public void onSwitchToMenu(Restaurant updatedRestaurant) {
        restaurant = updatedRestaurant; // 儲存使用者修改過的資訊

        RestaurantManageMenuFragment menufragment = new RestaurantManageMenuFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("restaurant_data", updatedRestaurant);
        menufragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_manage_restaurant, menufragment)
                .commit();
        updateStyle(false);
    }

    @Override
    public void onSwitchToInfo() {
        if (restaurant != null) {
            RestaurantManageInfoFragment infoFragment = new RestaurantManageInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurant_data", restaurant);
            infoFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_manage_restaurant, infoFragment)
                    .commit();
            updateStyle(true);
        }
    }

    private void updateStyle(boolean isInfo) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_manage);

        Button btnReturn = findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(view ->  {
            finish();
        });

        tvInfoNumber = findViewById(R.id.tv_info_number);
        tvInfo = findViewById(R.id.tv_info);
        tvMenuNumber = findViewById(R.id.tv_menu_number);
        tvMenu = findViewById(R.id.tv_menu);

        if (savedInstanceState == null) {
            // ✅ 從 Intent 取得傳進來的資料
            restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant_data");

            if (restaurant != null) {
                RestaurantManageInfoFragment infoFragment = new RestaurantManageInfoFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("restaurant_data", restaurant);
                infoFragment.setArguments(bundle);

                // 切換 Fragment 畫面
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_manage_restaurant, infoFragment)
                        .commit();
            }
        }
    }
}