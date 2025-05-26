package fcu.app.cyanbite.ui;

import static android.view.View.VISIBLE;
import static fcu.app.cyanbite.util.Util.open;
import static fcu.app.cyanbite.util.Util.setStatusBar;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.OrderGroupListAdapter;
import fcu.app.cyanbite.model.Group;

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

    public static final String COLLECTION_PROFILE = "profile";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_DISTRICT = "district";
    private Button btnShoppingCart;
    private LinearLayout btnSelected, btnHot, btnDrink, btnFood;
    private SearchBar etOrderSearch;
    private RecyclerView rvSelected, rvHot, rvDrink, rvFood;
    private OrderGroupListAdapter selectedAdapter, hotAdapter, drinkAdapter, foodAdapter;
    private ScrollView svMain;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;
    private String city, district;
    private List<Group> selectedList = new ArrayList<>();
    private List<Group> hotList = new ArrayList<>();
    private List<Group> drinkList = new ArrayList<>();
    private List<Group> foodList = new ArrayList<>();

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
    public void onResume() {
        super.onResume();
        setStatusBar(getActivity(), false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        initFirebase();
        initView(view);
        setupListener();
        setupRecyclerViews();
        loadProfile();
//        loadGroupData();

        return view;
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uid = user.getUid();
        db = FirebaseFirestore.getInstance();
    }

    private void initView(View view) {
        btnShoppingCart = view.findViewById(R.id.btn_shopping_cart);
        btnSelected = view.findViewById(R.id.btn_selected);
        btnHot = view.findViewById(R.id.btn_hot);
        btnDrink = view.findViewById(R.id.btn_drink);
        btnFood = view.findViewById(R.id.btn_food);
        etOrderSearch = view.findViewById(R.id.et_search);
        rvSelected = view.findViewById(R.id.rv_selected);
        rvHot = view.findViewById(R.id.rv_hot);
        rvDrink = view.findViewById(R.id.rv_drink);
        rvFood = view.findViewById(R.id.rv_food);
        svMain = view.findViewById(R.id.sv_main);
    }

    private void setupListener() {
        btnShoppingCart.setOnClickListener(view ->  {
            open(getActivity(), ShoppingCartActivity.class);
        });

        btnFood.setOnClickListener(view -> {
            svMain.post(() -> svMain.smoothScrollTo(0, rvFood.getTop() - 80));
        });

        btnDrink.setOnClickListener(view -> {
            svMain.post(() -> svMain.smoothScrollTo(0, rvDrink.getTop() - 80));
        });

        btnHot.setOnClickListener(view -> {
            svMain.post(() -> svMain.smoothScrollTo(0, rvHot.getTop() - 80));
        });

        btnSelected.setOnClickListener(view -> {
            svMain.post(() -> svMain.smoothScrollTo(0, rvSelected.getTop() - 80));
        });

        etOrderSearch.setOnClickListener(view -> {
            open(getActivity(), SearchActivity.class);
        });
    }

    private void setupRecyclerViews() {
        rvSelected.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        selectedAdapter = new OrderGroupListAdapter(getActivity(), selectedList, true);
        rvSelected.setAdapter(selectedAdapter);

        rvHot.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        hotAdapter = new OrderGroupListAdapter(getActivity(), hotList, true);
        rvHot.setAdapter(hotAdapter);

        rvDrink.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        drinkAdapter = new OrderGroupListAdapter(getActivity(), drinkList);
        rvDrink.setAdapter(drinkAdapter);

        rvFood.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        foodAdapter = new OrderGroupListAdapter(getActivity(), foodList);
        rvFood.setAdapter(foodAdapter);
    }

    private void loadGroupData() {
        findGroupData("selected", selectedList, selectedAdapter, 5);
        findGroupData("hot", hotList, hotAdapter, 5);
        findGroupData("drink", drinkList, drinkAdapter, 10);
        findGroupData("food", foodList, foodAdapter, 10);
    }

    private void findGroupData(String tag, List<Group> list, OrderGroupListAdapter adapter, int limit) {
        db.collection("group")
                .whereArrayContains("tag", tag)
                .whereEqualTo("city", city)
                .whereEqualTo("district", district)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();

                    Collections.shuffle(docs);
                    for (int i = 0; i < Math.min(limit, docs.size()); i++) {
                        DocumentSnapshot doc = docs.get(i);
                        String name = doc.getString("name");
                        String description = doc.getString("description");
                        String image = doc.getString("image");
                        list.add(new Group(name, description, "", "", "", "", null, image));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void loadProfile() {
        db.collection(COLLECTION_PROFILE).document(uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    city = (String)querySnapshot.get(FIELD_CITY);
                    district = (String)querySnapshot.get(FIELD_DISTRICT);

                    loadGroupData();
                });
    }
}