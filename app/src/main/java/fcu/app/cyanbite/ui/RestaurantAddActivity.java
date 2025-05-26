package fcu.app.cyanbite.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Restaurant;

public class RestaurantAddActivity extends AppCompatActivity implements OnTabSwitchListener  {

    private TextView tvInfoNumber, tvInfo, tvMenuNumber, tvMenu;

    @Override
    public void onSwitchToMenu(Restaurant restaurant) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_add_restaurant, new RestaurantAddMenuFragment())
                .commit();
        updateStyle(false);
    }

    @Override
    public void onSwitchToInfo() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_add_restaurant, new RestaurantAddInfoFragment())
                .commit();
        updateStyle(true);
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
        setContentView(R.layout.activity_restaurant_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvInfoNumber = findViewById(R.id.tv_info_number);
        tvInfo = findViewById(R.id.tv_info);
        tvMenuNumber = findViewById(R.id.tv_menu_number);
        tvMenu = findViewById(R.id.tv_menu);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_add_restaurant, new RestaurantAddInfoFragment())
                    .commit();
        }
    }
}