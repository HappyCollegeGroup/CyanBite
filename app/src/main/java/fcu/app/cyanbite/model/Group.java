package fcu.app.cyanbite.model;

import java.util.List;

public class Group {
    private String name;
    private String phone;
    private String location;
    private String orderingTime;
    private String collectionTime;
    private List<Restaurant> restaurantList;
    private int imageResId;

    public Group(String name, String phone, String location, String orderingTime, String collectionTime, List<Restaurant> restaurantList, int imageResId) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.orderingTime = orderingTime;
        this.collectionTime = collectionTime;
        this.restaurantList = restaurantList;
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
}
