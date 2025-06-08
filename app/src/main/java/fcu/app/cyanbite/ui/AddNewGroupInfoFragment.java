package fcu.app.cyanbite.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.CheckBox; // Import CheckBox
// Remove RadioButton and RadioGroup imports as they are no longer used
// import android.widget.RadioButton;
// import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import fcu.app.cyanbite.model.Group;
import fcu.app.cyanbite.R;

public class AddNewGroupInfoFragment extends Fragment {

    private OnGroupSwitchListener callback;
    private EditText etGroupName;
    private EditText etetGroupPhone;
    private EditText etGroupLocation;
    private EditText etGroupOrderingTime;
    private EditText etGroupCollectionTime;
    private EditText etGroupCity;
    private EditText etGroupDistrict;
    private EditText etGroupDescription;
    // Changed from RadioGroup to CheckBoxes
    private CheckBox checkboxBeverage;
    private CheckBox checkboxBento;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String groupImageBase64;
    private ImageButton imgButton;
    private Group group;

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
        etetGroupPhone = view.findViewById(R.id.et_group_phone);
        etGroupLocation = view.findViewById(R.id.et_group_location);
        etGroupOrderingTime = view.findViewById(R.id.et_group_ordering_time);
        etGroupCollectionTime = view.findViewById(R.id.et_group_collection_time);
        etGroupCity = view.findViewById(R.id.et_group_city);
        etGroupDistrict = view.findViewById(R.id.et_group_district);
        etGroupDescription = view.findViewById(R.id.et_group_description);
        imgButton = view.findViewById(R.id.img_btn_pic);
        // Initialize CheckBoxes instead of RadioGroup
        checkboxBeverage = view.findViewById(R.id.checkbox_beverage);
        checkboxBento = view.findViewById(R.id.checkbox_bento);

        Button btnNext = view.findViewById(R.id.btn_submit_move_to_restaurant);
        btnNext.setOnClickListener(v -> {
            String groupName = etGroupName.getText().toString().trim();
            String groupPhone = etetGroupPhone.getText().toString().trim();
            String groupLocation = etGroupLocation.getText().toString().trim();
            String orderingTime = etGroupOrderingTime.getText().toString().trim();
            String collectionTime = etGroupCollectionTime.getText().toString().trim();
            String groupCity = etGroupCity.getText().toString().trim();
            String groupDistrict = etGroupDistrict.getText().toString().trim();
            String groupDescription = etGroupDescription.getText().toString().trim();

            // --- Collect selected tags from CheckBoxes ---
            List<String> groupTags = new ArrayList<>();
            if (checkboxBeverage.isChecked()) {
                groupTags.add("drink");
            }
            if (checkboxBento.isChecked()) {
                groupTags.add("food");
            }

            // Validation: Ensure at least one tag is selected
            if (groupTags.isEmpty()) {
                Toast.makeText(getActivity(), "請選擇至少一種團購類型 (飲料或便當)", Toast.LENGTH_SHORT).show();
                return;
            }
            // --- End of tag collection ---

            if (groupName.isEmpty() || groupPhone.isEmpty() || groupLocation.isEmpty()
                    || orderingTime.isEmpty() || collectionTime.isEmpty() || groupCity.isEmpty()
                    || groupDistrict.isEmpty() || groupDescription.isEmpty()) {
                Toast.makeText(getActivity(), "請填寫所有欄位", Toast.LENGTH_SHORT).show();
                return;
            }


            Bundle groupData = new Bundle();
            groupData.putString("groupName", groupName);
            groupData.putString("groupPhone", groupPhone);
            groupData.putString("groupLocation", groupLocation);
            groupData.putString("orderingTime", orderingTime);
            groupData.putString("collectionTime", collectionTime);
            groupData.putString("groupCity", groupCity);
            groupData.putString("groupDistrict", groupDistrict);
            groupData.putString("groupDescription", groupDescription);
            groupData.putString("groupImage", groupImageBase64);
            groupData.putStringArrayList("groupTags", (ArrayList<String>) groupTags); // Put the list of tags

            if(callback != null){
                callback.onSwitchToGroupMenu(groupData);
            }
        });

        imgButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                if (imgButton != null) {
                    imgButton.setImageBitmap(bitmap);
                }

                String base64String = encodeImageToDataUrl(imageUri);
                groupImageBase64 = base64String;

                if (group != null) {
                    group.setImage(base64String);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeImageToDataUrl(Uri imageUri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

        Bitmap resizedBitmap = resizeBitmap(originalBitmap, 800);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        String mimeType = requireContext().getContentResolver().getType(imageUri);
        String base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        return "data:" + mimeType + ";base64," + base64;
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale = Math.min((float) maxSize / width, (float) maxSize / height);
        if (scale >= 1) return bitmap;

        int newWidth = Math.round(scale * width);
        int newHeight = Math.round(scale * height);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}