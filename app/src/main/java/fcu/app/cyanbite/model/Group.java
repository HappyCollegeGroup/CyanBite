package fcu.app.cyanbite.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.List;

public class Group {
    private String name;
    private String phone;
    private String location;
    private String orderingTime;
    private String collectionTime;
    private List<Restaurant> restaurantList;
    private int imageResId;
    private String image;
    private String description;

    public Group(String name, String phone, String location, String orderingTime, String collectionTime, List<Restaurant> restaurantList, int imageResId) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.orderingTime = orderingTime;
        this.collectionTime = collectionTime;
        this.restaurantList = restaurantList;
        this.imageResId = imageResId;
    }

    public Group(String name, String description, String phone, String location, String orderingTime, String collectionTime, List<Restaurant> restaurantList, String image) {
        this.name = name;
        this.description = description;
        this.phone = phone;
        this.location = location;
        this.orderingTime = orderingTime;
        this.collectionTime = collectionTime;
        this.restaurantList = restaurantList;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() { return description; }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public String getOrderingTime() {
        return orderingTime;
    }

    public String getCollectionTime() {
        return collectionTime;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public int getImageResId() {
        return imageResId;
    }

    public Bitmap getImage() {
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
}
