package fcu.app.cyanbite.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fcu.app.cyanbite.R;

public class AddNewGroupInfoFragment extends Fragment {

    private OnGroupSwitchListener callback;
    private EditText etGroupName;
    private EditText etGroupPhone;
    private EditText etGroupLocation;
    private EditText etGroupOrderingTime;
    private EditText etGroupCollectionTime;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnGroupSwitchListener) {
            callback = (OnGroupSwitchListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnGroupSwitchListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_group_info, container, false);

        etGroupName = view.findViewById(R.id.et_group_name);
        etGroupPhone = view.findViewById(R.id.et_group_phone);
        etGroupLocation = view.findViewById(R.id.et_group_location);
        etGroupOrderingTime = view.findViewById(R.id.et_group_ordering_time);
        etGroupCollectionTime = view.findViewById(R.id.et_group_collection_time);

        Button btnNext = view.findViewById(R.id.btn_submit_move_to_restaurant);  // Change the button ID accordingly
        btnNext.setOnClickListener(v -> {
            String groupName = etGroupName.getText().toString().trim();
            String groupPhone = etGroupPhone.getText().toString().trim();
            String groupLocation = etGroupLocation.getText().toString().trim();
            String orderingTime = etGroupOrderingTime.getText().toString().trim();
            String collectionTime = etGroupCollectionTime.getText().toString().trim();

            if (groupName.isEmpty() || groupPhone.isEmpty() || groupLocation.isEmpty()
                    || orderingTime.isEmpty() || collectionTime.isEmpty()) {
                Toast.makeText(getActivity(), "請填寫所有欄位", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle groupData = new Bundle();
            groupData.putString("groupName", groupName);
            groupData.putString("groupPhone", groupPhone);
            groupData.putString("groupLocation", groupLocation);
            groupData.putString("orderingTime", orderingTime);
            groupData.putString("collectionTime", collectionTime);

            AddNewGroupRestaurantFragment fragment = new AddNewGroupRestaurantFragment();
            fragment.setArguments(groupData);

            // Switch to the next fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
