package fcu.app.cyanbite.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import java.io.Serializable;

public class Food implements Serializable {
    private String name;
    private int price;
    private int imageResId;
    private String image;

    public Food(String name, int price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public Food(String name, Long price, String image) {
        this.name = name;
        this.price = Math.toIntExact(price);
        this.image = image;
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
