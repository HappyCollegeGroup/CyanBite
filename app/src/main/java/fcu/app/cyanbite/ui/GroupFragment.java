package fcu.app.cyanbite.ui;

import static fcu.app.cyanbite.util.Util.setStatusBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference; // Import DocumentReference
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.OrderGroupNameListAdapter;
import fcu.app.cyanbite.model.GroupName;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton addNewGroup;
    private FirebaseFirestore db;
    private ActivityResultLauncher<Intent> addGroupLauncher;

    private List<GroupName> groupNameList;
    private OrderGroupNameListAdapter adapter;


    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addGroupLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadGroupData();
                    }
                }
        );

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        setStatusBar(getActivity(), true);
        loadGroupData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.rv_order_group_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupNameList = new ArrayList<>();
        adapter = new OrderGroupNameListAdapter(getActivity(), groupNameList);
        recyclerView.setAdapter(adapter);



        addNewGroup = view.findViewById(R.id.btn_add_new_group);
        addNewGroup.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddNewGroupActivity.class);
            addGroupLauncher.launch(intent);
        });

        return view;
    }

    private void loadGroupData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();

        db.collection("group")
                .whereEqualTo("uid", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    groupNameList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String name = doc.getString("name");
                        if (name != null) {
                            groupNameList.add(new GroupName(name));
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->{
                    // Log error for debugging
                    Log.e("GroupFragment", "Error loading groups: " + e.getMessage());
                    Toast.makeText(getContext(), "無法載入群組資料: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        adapter.setOnItemClickListener(new OrderGroupNameListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GroupName group) {
                String groupName = group.getGroupName();

                db.collection("group")
                        .whereEqualTo("name", groupName)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                                String image = doc.getString("image");
                                String phone = doc.getString("phone");
                                String location = doc.getString("place");
                                String orderingTime = doc.getString("orderTime");
                                String collectionTime = doc.getString("collectionTime");

                                String groupCity = doc.getString("city");
                                String groupDistrict = doc.getString("district");
                                String groupDescription = doc.getString("description");

                                List<DocumentReference> restaurantRefs = (List<DocumentReference>) doc.get("restaurant");

                                ArrayList<String> restaurantPathsToPass = new ArrayList<>();
                                if (restaurantRefs != null) {
                                    for (DocumentReference ref : restaurantRefs) {
                                        restaurantPathsToPass.add(ref.getPath());
                                    }
                                }

                                Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
                                intent.putExtra("groupImage", image);
                                intent.putExtra("groupName", groupName);
                                intent.putExtra("groupPhone", phone);
                                intent.putExtra("groupLocation", location);
                                intent.putExtra("orderingTime", orderingTime);
                                intent.putExtra("collectionTime", collectionTime);

                                intent.putExtra("groupCity", groupCity);
                                intent.putExtra("groupDistrict", groupDistrict);
                                intent.putExtra("groupDescription", groupDescription);

                                intent.putStringArrayListExtra("restaurantPaths", restaurantPathsToPass);

                                startActivity(intent);

                            } else {
                                // Log error if group not found
                                Log.w("GroupFragment", "Group not found for detail view: " + groupName);
                                Toast.makeText(getContext(), "找不到該群組的詳細資料。", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Log and show error for query failure
                            Log.e("GroupFragment", "Error fetching group details: " + e.getMessage());
                            Toast.makeText(getContext(), "獲取群組詳細資料失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }
}