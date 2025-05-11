package fcu.app.cyanbite.model;

import java.util.List;

public class Restaurant {
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

    public int getImageResId() {
        return imageResId;
    }
}
