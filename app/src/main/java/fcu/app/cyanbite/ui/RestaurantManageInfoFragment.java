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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnTabSwitchListener callback;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RestaurantManageInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantManageInfoFragment.
     */
    // TODO: Rename and change types and number of parameters

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
        if (bundle != null) {
            Restaurant restaurant = (Restaurant) bundle.getSerializable("restaurant_data");

            if (restaurant != null) {
                // 2. 找元件
                EditText etName = view.findViewById(R.id.et_name);
                EditText etPhone = view.findViewById(R.id.et_phone);
                EditText etLocation = view.findViewById(R.id.et_location);
                ImageButton imgButton = view.findViewById(R.id.img_btn_restaurant);

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
            if (callback != null) {
                callback.onSwitchToMenu();  // 通知 Activity 切換
            }
        });

        return view;
    }
}