package fcu.app.cyanbite.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.BuyerOrderAdapter;
import fcu.app.cyanbite.model.BuyerOrder;
import fcu.app.cyanbite.model.Order; // Ensure this is imported
import fcu.app.cyanbite.model.OrderItem; // Ensure this is imported

public class GroupShowBuyerActivity extends AppCompatActivity {

    private static final String TAG = "GroupShowBuyerActivity";

    private FirebaseFirestore db;
    private RecyclerView rvBuyerOrders;
    private BuyerOrderAdapter buyerOrderAdapter;
    private List<BuyerOrder> buyerOrderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_show_buyer);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        rvBuyerOrders = findViewById(R.id.rv_buyer_orders);
        rvBuyerOrders.setLayoutManager(new LinearLayoutManager(this));

        buyerOrderAdapter = new BuyerOrderAdapter(buyerOrderList);
        rvBuyerOrders.setAdapter(buyerOrderAdapter);

        // --- 修正開始 ---
        // 這裡改為從 "name" 鍵獲取群組 ID，與 GroupDetailActivity 的傳遞方式保持一致
        String groupId = getIntent().getStringExtra("name");
        // --- 修正結束 ---

        if (groupId == null || groupId.isEmpty()) {
            Toast.makeText(this, "無效的群組 ID，無法載入訂單。", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Group ID is null or empty. Cannot fetch orders.");
            finish();
            return;
        }

        fetchOrdersByGroup(groupId);
    }

    /**
     * Fetches individual 'Order' documents from Firebase Firestore for a specific group,
     * then groups them by buyer (UID) to create BuyerOrder objects.
     * This assumes:
     * 1. Individual orders are in a top-level collection named "orders".
     * 2. Each 'Order' document contains a 'uid' field to identify the buyer.
     * 3. 'Order' documents DO NOT contain a 'buyerName' field directly; names will be derived from UID.
     * 4. Each 'Order' document represents a quantity of 1 for the ordered item.
     *
     * @param groupId The Firestore document ID of the group.
     */
    private void fetchOrdersByGroup(String groupId) {
        // 這行 Logcat 訊息對除錯非常重要，請保留它
        Log.d(TAG, "fetchOrdersByGroup 接收到的群組ID: '" + groupId + "'");

        db.collection("order")
                .whereEqualTo("group", groupId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        buyerOrderList.clear(); // Clear existing data

                        // A map to temporarily store OrderItems, grouped by buyer UID
                        Map<String, List<OrderItem>> buyerItemsMap = new HashMap<>();
                        // A map to store buyer UIDs to their display names
                        Map<String, String> uidToNameMap = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Order order = document.toObject(Order.class);
                                order.setId(document.getId());

                                String uid = order.getUid();
                                if (uid == null || uid.trim().isEmpty()) {
                                    Log.w(TAG, "Order document " + document.getId() + " has no valid UID. Skipping.");
                                    continue;
                                }


                                String buyerName = "購買人 (" + uid.substring(0, Math.min(uid.length(), 4)) + "...)";
                                uidToNameMap.put(uid, buyerName); // Store or update the UID to name mapping

                                // Create an OrderItem from the current Order
                                OrderItem orderItem = new OrderItem(
                                        order.getFood(),    // Food name
                                        1,                  // Quantity is assumed to be 1 per Order document, as your 'Order' model doesn't have a 'quantity' field.
                                        order.getPrice()    // Price of this individual food item
                                );

                                // Add the OrderItem to the correct buyer's list in the map
                                buyerItemsMap.computeIfAbsent(uid, k -> new ArrayList<>()).add(orderItem);

                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing individual order document " + document.getId() + ": " + e.getMessage(), e);
                                Toast.makeText(GroupShowBuyerActivity.this, "載入部分訂單細項失敗。", Toast.LENGTH_SHORT).show();
                            }
                        }

                        // After processing all individual 'Order' documents,
                        // aggregate them into 'BuyerOrder' objects
                        for (Map.Entry<String, List<OrderItem>> entry : buyerItemsMap.entrySet()) {
                            String uid = entry.getKey();
                            List<OrderItem> itemsForBuyer = entry.getValue();
                            String buyerName = uidToNameMap.getOrDefault(uid, "未知購買人"); // Retrieve the determined buyer name

                            double totalForBuyer = 0.0;
                            for (OrderItem item : itemsForBuyer) {
                                totalForBuyer += (item.getPrice() * item.getQuantity());
                            }

                            // Create the BuyerOrder object and add it to the list for the adapter
                            buyerOrderList.add(new BuyerOrder(buyerName, totalForBuyer, itemsForBuyer));
                        }

                        buyerOrderAdapter.notifyDataSetChanged(); // Inform the RecyclerView adapter that data has changed

                        if (buyerOrderList.isEmpty()) {
                            Toast.makeText(GroupShowBuyerActivity.this, "此群組目前沒有任何訂單。", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "No orders found for group ID: " + groupId);
                        }
                    } else {
                        Log.e(TAG, "Error getting individual orders: " + task.getException().getMessage(), task.getException());
                        Toast.makeText(GroupShowBuyerActivity.this, "載入訂單失敗：" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}