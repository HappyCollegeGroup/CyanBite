package fcu.app.cyanbite.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantManageInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantManageInfoFragment extends Fragment {


    private OnTabSwitchListener callback;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Restaurant restaurant;

    public RestaurantManageInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTabSwitchListener) {
            callback = (OnTabSwitchListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnTabSwitchListener");
        }
    }

    public static RestaurantManageInfoFragment newInstance(String param1, String param2) {
        RestaurantManageInfoFragment fragment = new RestaurantManageInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_restaurant_manage_info, container, false);

        Bundle bundle = getArguments();

        // 2. 找元件
        EditText etName = view.findViewById(R.id.et_name);
        EditText etPhone = view.findViewById(R.id.et_phone);
        EditText etLocation = view.findViewById(R.id.et_location);
        ImageButton imgButton = view.findViewById(R.id.img_btn_restaurant);

        if (bundle != null) {
            restaurant = (Restaurant) bundle.getSerializable("restaurant_data");

            if (restaurant != null) {
                // 3. 設定元件值
                etName.setText(restaurant.getName());
                etPhone.setText(restaurant.getPhone());
                etLocation.setText(restaurant.getLocation());
                imgButton.setImageBitmap(restaurant.getImageBitmap());
            }
        } else {
            Toast.makeText(getActivity(), "nothing happen", Toast.LENGTH_SHORT).show();
        }

        Button btnToInfo = view.findViewById(R.id.btn_next);
        btnToInfo.setOnClickListener(v -> {
            if (restaurant != null) {
                restaurant.setName(etName.getText().toString());
                restaurant.setPhone(etPhone.getText().toString());
                restaurant.setLocation(etLocation.getText().toString());
            }

            if (callback != null) {
                callback.onSwitchToMenu(restaurant);  // 通知 Activity 切換
            }
        });

        return view;
    }
}