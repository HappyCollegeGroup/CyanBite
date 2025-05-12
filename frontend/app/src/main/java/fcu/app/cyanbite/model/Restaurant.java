package fcu.app.cyanbite.model;

import java.io.Serializable;
import java.util.List;

public class Restaurant implements Serializable {
    private String name;
    private String phone;
    private String location;
    private List<Food> foodList;
    private int imageResId;

    public Restaurant(String name, String phone, String location, List<Food> foodList, int imageResId) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.foodList = foodList;
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

    public void setFoodList(List<Food> foodList) { // ← 加上這個
        this.foodList = foodList;
    }

    public int getImageResId() {
        return imageResId;
    }
}
