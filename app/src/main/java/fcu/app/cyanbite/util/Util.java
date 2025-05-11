package fcu.app.cyanbite.util;

import android.app.Activity;
import android.content.Intent;
import android.view.Window;

import androidx.core.view.WindowInsetsControllerCompat;

import fcu.app.cyanbite.R;

public class Util {
    public static void setStatusBar(Activity activity, boolean darkText) {
        Window window = activity.getWindow();
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(window, window.getDecorView());
        insetsController.setAppearanceLightStatusBars(darkText);
    }

    public static void navigateTo(Activity activity, Class<?> destination) {
        Intent intent = new Intent(activity, destination);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void open(Activity activity, Class<?> destination) {
        Intent intent = new Intent(activity, destination);
        activity.startActivity(intent);
    }

    public static void slideTo(Activity activity, Class<?> destination) {
        Intent intent = new Intent(activity, destination);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void slideBack(Activity activity) {
        if (!activity.isFinishing()) {
            activity.finish();
        }
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
