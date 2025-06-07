package fcu.app.cyanbite.ui;

import android.widget.ImageButton;

import fcu.app.cyanbite.model.Food;

public interface ImageSelectListener {
    void onSelectImageRequested(Food food, ImageButton targetImageButton);
}
