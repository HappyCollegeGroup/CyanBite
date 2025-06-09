package fcu.app.cyanbite.ui;

import static android.content.Context.MODE_PRIVATE;
import static fcu.app.cyanbite.util.Util.navigateTo;
import static fcu.app.cyanbite.util.Util.setStatusBar;
import static fcu.app.cyanbite.util.Util.slideTo;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import fcu.app.cyanbite.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout btnProfile, btnAccount, btnLogout;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
        setStatusBar(getActivity(), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        initView(view);
        setupListener();

        return view;
    }

    private void initView(View view) {
        btnProfile = view.findViewById(R.id.btn_profile);
        btnAccount = view.findViewById(R.id.btn_account);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void setupListener() {
        btnProfile.setOnClickListener(view ->  {
            slideTo(getActivity(), ProfileActivity.class);
        });

        btnAccount.setOnClickListener(view ->  {
            slideTo(getActivity(), AccountActivity.class);
        });

        btnLogout.setOnClickListener(view ->  {
            SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();;
            navigateTo(getActivity(), LoginActivity.class);
        });
    }
}