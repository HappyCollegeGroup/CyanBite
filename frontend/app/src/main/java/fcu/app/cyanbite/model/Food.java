package fcu.app.cyanbite.model;

import java.io.Serializable;

public class Food  implements Serializable {
    private String name;
    private int price;
    private int imageResId;

    public Food(String name, int price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

}
