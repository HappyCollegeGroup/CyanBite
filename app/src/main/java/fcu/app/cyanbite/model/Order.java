package fcu.app.cyanbite.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.sql.Time;

public class Order implements Serializable {
    private String food;
    private String group;
    private String restaurant;
    private Timestamp time;
    private String uid;
    private String image;
    private int price;
    private String id;

    public Order() {

    }

    public Order(String id, String food, String group, String restaurant, Timestamp time, String uid, int price, String image) {
        this.id = id;
        this.food = food;
        this.group = group;
        this.restaurant = restaurant;
        this.time = time;
        this.uid = uid;
        this.image = image;
        this.price = price;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Bitmap getImageBitmap() {
        Bitmap bitmap = null;

        if (image == null || image.trim().isEmpty()) {
            return null;  // 若 image 為 null 或空字串，直接回傳 null
        }

        try {
            // 去除 data URI 的前綴（如有）
            if (image.contains(",")) {
                image = image.substring(image.indexOf(",") + 1);
            }

            byte[] decodedBytes = Base64.decode(image, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();  // 解碼失敗或 image 為 null
        }

        return bitmap;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
