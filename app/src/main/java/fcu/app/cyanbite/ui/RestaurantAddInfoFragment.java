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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.model.Food;
import fcu.app.cyanbite.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantAddInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantAddInfoFragment extends Fragment implements ImageSelectListener  {

    private OnTabSwitchListener callback;
    private static final int PICK_IMAGE_REQUEST = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Restaurant restaurant;
    private String restaurantImageBase64;

    public RestaurantAddInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSelectImageRequested(Food food, ImageButton imageButton) {
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

    public static RestaurantAddInfoFragment newInstance(String param1, String param2) {
        RestaurantAddInfoFragment fragment = new RestaurantAddInfoFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_add_info, container, false);

        Bundle bundle = getArguments();

        // 2. 找元件
        EditText etName = view.findViewById(R.id.et_food_name);
        EditText etPhone = view.findViewById(R.id.et_phone);
        EditText etLocation = view.findViewById(R.id.et_location);

        if (bundle != null) {
            restaurant = (Restaurant) bundle.getSerializable("restaurant_new_data");

            etName.setText(restaurant.getName());
            etPhone.setText(restaurant.getPhone());
            etLocation.setText(restaurant.getLocation());
        } else {
            Toast.makeText(getActivity(), "nothing happen", Toast.LENGTH_SHORT).show();
        }

        Button btnToSecond = view.findViewById(R.id.btn_next);
        btnToSecond.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String location = etLocation.getText().toString().trim();

            // 假設你用這個變數保存圖片 base64 資料
            if (name.isEmpty() || phone.isEmpty() || location.isEmpty() || restaurantImageBase64 == null) {
                Toast.makeText(getContext(), "請完整填寫所有欄位並選擇圖片", Toast.LENGTH_SHORT).show();
                return; // 有缺就不繼續
            }

            if (restaurant != null) {
                restaurant.setName(etName.getText().toString());
                restaurant.setPhone(etPhone.getText().toString());
                restaurant.setLocation(etLocation.getText().toString());
            }

            if (callback != null) {
                callback.onSwitchToMenu(restaurant);  // 通知 Activity 切換
            }
        });

        ImageButton imgbtn = view.findViewById(R.id.img_btn_restaurant);
        imgbtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);  // 啟動圖片選擇器
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 確認請求碼與結果
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();  // 取得選擇的圖片 URI
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                ImageButton imgbtn = getView().findViewById(R.id.img_btn_restaurant);
                imgbtn.setImageBitmap(bitmap);  // 將選擇的圖片設置為 ImageButton 的圖片


                String base64String = encodeImageToDataUrl(imageUri);
                restaurantImageBase64 = base64String;

                if (restaurant != null) {
                    restaurant.setImage(base64String);  // 儲存到物件
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeImageToDataUrl(Uri imageUri) throws IOException {
        // 讀取圖片輸入流
        InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

        // 步驟一：縮小圖片（最多寬或高為800px）
        Bitmap resizedBitmap = resizeBitmap(originalBitmap, 800);

        // 步驟二：壓縮圖片為 JPEG（壓縮品質 70%）
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        // 步驟三：取得 mimeType 並組成 Data URL
        String mimeType = requireContext().getContentResolver().getType(imageUri);
        String base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        return "data:" + mimeType + ";base64," + base64;
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale = Math.min((float) maxSize / width, (float) maxSize / height);
        if (scale >= 1) return bitmap; // 如果太小，不需縮放

        int newWidth = Math.round(scale * width);
        int newHeight = Math.round(scale * height);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

}