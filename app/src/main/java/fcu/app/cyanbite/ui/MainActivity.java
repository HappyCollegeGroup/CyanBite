package fcu.app.cyanbite.ui;

import static fcu.app.cyanbite.util.Util.navigateTo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;

import fcu.app.cyanbite.R;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Fragment orderFragment, groupFragment, restaurantFragment, accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initFirebase();
        checkIsLogin();
        initBottomNavigationView();
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void initBottomNavigationView() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        orderFragment = OrderFragment.newInstance("", "");
        groupFragment = GroupFragment.newInstance("", "");
        restaurantFragment = RestaurantFragment.newInstance("", "");
        accountFragment = AccountFragment.newInstance("", "");

        setCurrentFragment(orderFragment);

        bottomNav.setOnItemSelectedListener(item ->  {
            if(item.getItemId() == R.id.menu_order) {
                setCurrentFragment(orderFragment);
            } else if (item.getItemId() == R.id.menu_group) {
                setCurrentFragment(groupFragment);
            } else if (item.getItemId() == R.id.menu_restaurant) {
                setCurrentFragment(restaurantFragment);
            }  else if (item.getItemId() == R.id.menu_account) {
                setCurrentFragment(accountFragment);
            }
            return true;
        });
    }

    private void checkIsLogin() {
        if (mAuth.getCurrentUser() == null) {
            navigateTo(this, LoginActivity.class);
        }
    }
}