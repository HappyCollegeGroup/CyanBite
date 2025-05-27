//package fcu.app.cyanbite.ui;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import fcu.app.cyanbite.R;
//import fcu.app.cyanbite.model.Restaurant;
//
//public class RestaurantShowActivity extends AppCompatActivity implements OnTabSwitchListener, OnGroupSwitchListener {
//    private TextView tvInfoNumber, tvInfo, tvMenuNumber, tvMenu;
//    private Restaurant restaurant;
//    @Override
//    public void onSwitchToMenu(Restaurant restaurants) {
//        if (restaurant != null) {
//            RestaurantShowMenuFragment showmenufragment = new RestaurantShowMenuFragment();
//
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("restaurant_data", restaurant);
//            showmenufragment.setArguments(bundle);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_manage_restaurant, showmenufragment)
//                    .addToBackStack(null)
//                    .commit();
//            updateStyle(false);
//        }
//    }
//
//    @Override
//    public void onSwitchToInfo() {
//        if (restaurant != null) {
//            RestaurantShowInfoFragment infoFragment = new RestaurantShowInfoFragment();
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("restaurant_data", restaurant);
//            infoFragment.setArguments(bundle);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_manage_restaurant, infoFragment)
//                    .addToBackStack(null)
//                    .commit();
//            updateStyle(true);
//        }
//    }
//
//    private void updateStyle(boolean isInfo) {
//        if (isInfo) {
//            tvInfoNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_filled));
//            tvInfoNumber.setTextColor(Color.parseColor("#FFFFFF"));
//            tvInfo.setTextColor(Color.parseColor("#00BCD4"));
//
//            tvMenuNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_border));
//            tvMenuNumber.setTextColor(Color.parseColor("#000000"));
//            tvMenu.setTextColor(Color.parseColor("#000000"));
//        } else {
//            tvMenuNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_filled));
//            tvMenuNumber.setTextColor(Color.parseColor("#FFFFFF"));
//            tvMenu.setTextColor(Color.parseColor("#00BCD4"));
//
//            tvInfoNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_border));
//            tvInfoNumber.setTextColor(Color.parseColor("#000000"));
//            tvInfo.setTextColor(Color.parseColor("#000000"));
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_restaurant_show);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        tvInfoNumber = findViewById(R.id.tv_info_number);
//        tvInfo = findViewById(R.id.tv_info);
//        tvMenuNumber = findViewById(R.id.tv_menu_number);
//        tvMenu = findViewById(R.id.tv_menu);
//
//        if (savedInstanceState == null) {
//            // ✅ 從 Intent 取得傳進來的資料
//            restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant_data");
//
//            if (restaurant != null) {
//                RestaurantShowInfoFragment infoFragment = new RestaurantShowInfoFragment();
//
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("restaurant_data", restaurant);
//                infoFragment.setArguments(bundle);
//
//                // 切換 Fragment 畫面
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_manage_restaurant, infoFragment)
//                        .addToBackStack(null)
//                        .commit();
//            }
//        }
//    }
//
//    @Override
//    public void onSwitchToGroupInfo() {
//
//    }
//
//    @Override
//    public void onSwitchToGroupMenu(Bundle bundle) {
//
//    }
//
//    @Override
//    public void onSwitchToGroup() {
//        // 根據需求切換 fragment 或 finish Activity
//        // 例如顯示上一個 Fragment 或返回團購資訊頁面
//        Toast.makeText(this, "返回團購資訊畫面", Toast.LENGTH_SHORT).show();
//
//        // 或是切回某個 Fragment
//        getSupportFragmentManager()
//                .popBackStack(); // 回上一層
//    }
//
//}