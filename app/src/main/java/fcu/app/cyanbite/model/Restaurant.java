package fcu.app.cyanbite.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.util.List;

import fcu.app.cyanbite.R;

public class Restaurant implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String location;
    private List<Food> foodList;
    private String image;
    private int imageResId;

    public Restaurant(String name, String phone, String location, List<Food> foodList, int imageResId) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.foodList = foodList;
        this.imageResId = imageResId;
    }

    public Restaurant(String id, String name, String phone, String location, List<Food> foodList, String image) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.foodList = foodList;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public Bitmap getImageBitmap() {
        Bitmap bitmap = null;

        // 去除開頭的 data URI（如果有）
        if (image.contains(",")) {
            image = image.substring(image.indexOf(",") + 1);
        }

        try {
            byte[] decodedBytes = Base64.decode(image, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
    }
}
