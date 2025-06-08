package fcu.app.cyanbite.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.OrderGroupListAdapter;
import fcu.app.cyanbite.adapter.ShoppingCartAdapter;
import fcu.app.cyanbite.adapter.ShoppingCartGroupAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Group;
import fcu.app.cyanbite.model.Order;
import fcu.app.cyanbite.model.Restaurant;

public class ShoppingCartActivity extends AppCompatActivity {

    private ShoppingCartAdapter adapter;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_cart);

        Button btnReturn = findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(view ->  {
            finish();
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.rv_order_restaurant_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ShoppingCartAdapter(orderList);
        recyclerView.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUid = currentUser.getUid();

            db.collection("order")
                    .whereEqualTo("uid", currentUid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String id = doc.getId();
                                String food = doc.getString("food");
                                String group = doc.getString("group");
                                String restaurant = doc.getString("restaurant");
                                String imgFood = doc.getString("image");
                                Timestamp timestamp = doc.getTimestamp("time");
                                String timeString = "";  // 預設空字串，避免 null

                                if (timestamp != null) {
                                    Date date = timestamp.toDate();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                    timeString = sdf.format(date);
                                }
                                String uid = doc.getString("uid");

//                                orderList.add(new Order(id, food, group, restaurant, timeString, uid, 100, imgFood));
                                orderList.add(new Order(id, food, group, restaurant, timestamp, uid, 100, imgFood));
                            }
                            Collections.sort(orderList, (o1, o2) -> o2.getTime().compareTo(o1.getTime()));
                            adapter.notifyDataSetChanged();
                        }
                    });
            adapter.setOnDeleteClickListener((order, position) -> {
                db.collection("order").document(order.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // 從列表移除該筆資料並刷新 RecyclerView
                            orderList.remove(position);
                            Collections.sort(orderList, (o1, o2) -> o2.getTime().compareTo(o1.getTime()));
                            adapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            // 可做錯誤提示
                            Toast.makeText(this, "刪除失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });

        }
    }
}