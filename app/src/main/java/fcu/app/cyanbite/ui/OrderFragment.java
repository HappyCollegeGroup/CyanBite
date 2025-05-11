package fcu.app.cyanbite.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.OrderGroupListAdapter;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Group;
import fcu.app.cyanbite.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    private List<Group> groupList = new ArrayList<>();
    private OrderGroupListAdapter adapter;

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        db = FirebaseFirestore.getInstance();

        Button btnShoppingCart = view.findViewById(R.id.btn_shopping_cart);
        btnShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShoppingCartActivity.class);
                startActivity(intent);
            }
        });

        EditText etOrderSearch = view.findViewById(R.id.et_group_search);
        etOrderSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadGroupData(etOrderSearch.getText().toString());
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.rv_order_group_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new OrderGroupListAdapter(getActivity(), groupList);
        recyclerView.setAdapter(adapter);

        loadGroupData("");

        return view;
    }

    private void loadGroupData(String query) {
        db.collection("group")
                .whereArrayContains("searchKeywords", query)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    groupList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
//                        List<Restaurant> restaurantList = new ArrayList<>();
//                        ArrayList<DocumentReference> restaurants = (ArrayList<DocumentReference>) doc.get("restaurant");
//                        for (DocumentReference restaurant : restaurants) {
//                            restaurant.get()
//                                    .addOnSuccessListener(restaurantDoc -> {
//                                        String restaurantName = restaurantDoc.getString("name");
//                                        String restaurantPhone = restaurantDoc.getString("phone");
//                                        String restaurantAddress = restaurantDoc.getString("address");
//                                        List<Food> foodList = new ArrayList<>();
//                                        ArrayList<Map<String, Object>> menu = (ArrayList<Map<String, Object>>) restaurantDoc.get("menu");
//                                        for (Map<String, Object> food : menu) {
//                                            foodList.add(new Food(
//                                                    (String) food.get("name"),
//                                                    (Long) food.get("price"),
//                                                    (String) food.get("image")));
//                                        }
//                                        restaurantList.add(new Restaurant(restaurantName, restaurantPhone, restaurantAddress, foodList, -1));
//                                        adapter.notifyDataSetChanged();
//                                    });
//                        }

                        groupList.add(new Group(name, doc.getString("description"), "", "", "", "", null, doc.getString("image")));
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.cyan));
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(window, window.getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
    }
}