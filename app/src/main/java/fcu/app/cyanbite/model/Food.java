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

    public void setImage(String image) {
        this.image = image;
    }

    public int getImageResId() {
        return imageResId;
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

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    public void setPrice(Long price) {
        this.price = Math.toIntExact(price);
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
