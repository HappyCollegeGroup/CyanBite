package fcu.app.cyanbite.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Restaurant;

public class RestaurantShowInfoFragment extends Fragment {

    private OnTabSwitchListener callback;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public RestaurantShowInfoFragment() {
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

    public static RestaurantShowInfoFragment newInstance(String param1, String param2) {
        RestaurantShowInfoFragment fragment = new RestaurantShowInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_restaurant_show_info, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Restaurant restaurant = (Restaurant) bundle.getSerializable("restaurant_data");

            if (restaurant != null) {
                // 原本是 EditText，現在改為 TextView
                TextView tvName = view.findViewById(R.id.et_name);
                TextView tvPhone = view.findViewById(R.id.et_phone);
                TextView tvLocation = view.findViewById(R.id.et_location);
                ImageButton imgButton = view.findViewById(R.id.img_btn_restaurant);

                // 設定顯示內容
                tvName.setText(restaurant.getName());
                tvPhone.setText(restaurant.getPhone());
                tvLocation.setText(restaurant.getLocation());
                imgButton.setImageResource(restaurant.getImageResId());
            }
        } else {
            Toast.makeText(getActivity(), "nothing happen", Toast.LENGTH_SHORT).show();
        }

        Button btnToInfo = view.findViewById(R.id.btn_next);
        btnToInfo.setOnClickListener(v -> {
            if (callback != null) {
                callback.onSwitchToMenu(null);  // 通知 Activity 切換頁面
            }
        });

        return view;
    }
}
