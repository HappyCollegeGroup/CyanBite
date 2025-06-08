package fcu.app.cyanbite.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.OrderFoodListAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Group;
import fcu.app.cyanbite.model.Restaurant;

public class OrderGroupActivity extends AppCompatActivity {
    private ImageView ivGroup;
    private TextView tvGroupName, tvOrderTime, tvPhone, tvCollectionTime, tvPlace;
    private Button btnReturn;
    private FirebaseFirestore db;
    private String name;
    private OrderFoodListAdapter adapter;
    private List<Restaurant> restaurantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_group);

        ivGroup = findViewById(R.id.iv_group);
        tvGroupName = findViewById(R.id.tv_fragment_title);
        btnReturn = findViewById(R.id.btn_return);
        tvOrderTime = findViewById(R.id.tv_order_time);
        tvPhone = findViewById(R.id.tv_phone);
        tvCollectionTime = findViewById(R.id.tv_collection_time);
        tvPlace = findViewById(R.id.tv_place);

        db = FirebaseFirestore.getInstance();

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        name = getIntent().getStringExtra("name");
        db.collection("group")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String image = doc.getString("image");
                        String phone = doc.getString("phone");
                        String orderTime = doc.getString("orderTime");
                        String collectionTime = doc.getString("collectionTime");
                        String place = doc.getString("place");

                        ArrayList<DocumentReference> restaurants = (ArrayList<DocumentReference>) doc.get("restaurant");
                        for (DocumentReference restaurant : restaurants) {
                            restaurant.get()
                                    .addOnSuccessListener(restaurantDoc -> {
                                        String restaurantName = restaurantDoc.getString("name");
                                        List<Food> foodList = new ArrayList<>();
                                        ArrayList<Map<String, Object>> menu = (ArrayList<Map<String, Object>>) restaurantDoc.get("menu");
                                        for (Map<String, Object> food : menu) {
                                            foodList.add(new Food(
                                                    (String) food.get("name"),
                                                    (Long) food.get("price"),
                                                    (String) food.get("image")));
                                        }
                                        restaurantList.add(new Restaurant(restaurantName, "", "", foodList, -1));
                                        adapter.notifyDataSetChanged();
                                    });
                        }

                        Group group = new Group(name, "", phone, place, orderTime, collectionTime, restaurantList, image);
                        tvGroupName.setText(group.getName());
                        ivGroup.setImageBitmap(group.getImage());
                        tvOrderTime.setText("訂餐時間: " + group.getOrderingTime());
                        tvPhone.setText("連絡電話: " + group.getPhone());
                        tvCollectionTime.setText("領餐時間: " + group.getCollectionTime());
                        tvPlace.setText("領餐地址: " + group.getLocation());
                    }
                });

        RecyclerView recyclerView = findViewById(R.id.rv_food_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new OrderFoodListAdapter(this, name, restaurantList);
        recyclerView.setAdapter(adapter);
    }
}