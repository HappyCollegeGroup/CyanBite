package fcu.app.cyanbite.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.OrderGroupListAdapter;
import fcu.app.cyanbite.adapter.ShoppingCartGroupAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Group;
import fcu.app.cyanbite.model.Restaurant;

public class ShoppingCartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<Food> foodList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            foodList.add(new Food("部隊鍋泡麵", 120, R.drawable.image));
        }

        List<Restaurant> restaurantList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            restaurantList.add(new Restaurant("逢甲一起訂" + i, "0900-000-000", "逢甲大學", foodList, R.drawable.image));
        }

        List<Group> groupList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            groupList.add(new Group("逢甲一起訂" + i, "0900-000-000", "逢甲大學", "9:00~10:00", "12:00", restaurantList, R.drawable.image));
        }

        // RecyclerView 設定
        RecyclerView recyclerView = findViewById(R.id.rv_order_restaurant_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShoppingCartGroupAdapter adapter = new ShoppingCartGroupAdapter(this, groupList);
        recyclerView.setAdapter(adapter);
    }
}