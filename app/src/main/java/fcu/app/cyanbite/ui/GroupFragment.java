package fcu.app.cyanbite.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);

        Button btnShoppingCart = view.findViewById(R.id.btn_shopping_cart);
        btnShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShoppingCartActivity.class);
                startActivity(intent);
            }
        });


        List<GroupName> groupNameList = new ArrayList<>();
        groupNameList.add(new GroupName("逢甲一起訂1"));
        groupNameList.add(new GroupName("逢甲一起訂2"));
        groupNameList.add(new GroupName("逢甲一起訂3"));
        groupNameList.add(new GroupName("逢甲一起訂4"));
        groupNameList.add(new GroupName("逢甲一起訂5"));
        groupNameList.add(new GroupName("逢甲一起訂6"));
        groupNameList.add(new GroupName("逢甲一起訂7"));
        groupNameList.add(new GroupName("逢甲一起訂8"));
        groupNameList.add(new GroupName("逢甲一起訂9"));
        groupNameList.add(new GroupName("逢甲一起訂10"));
        groupNameList.add(new GroupName("逢甲一起訂11"));
        groupNameList.add(new GroupName("逢甲一起訂12"));
        groupNameList.add(new GroupName("逢甲一起訂13"));
        groupNameList.add(new GroupName("逢甲一起訂14"));
        groupNameList.add(new GroupName("逢甲一起訂15"));
        groupNameList.add(new GroupName("逢甲一起訂16"));

        RecyclerView recyclerView = view.findViewById(R.id.rv_order_group_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        OrderGroupNameListAdapter adapter = new OrderGroupNameListAdapter(getActivity(), groupNameList);
        recyclerView.setAdapter(adapter);


        return view;



    }
}