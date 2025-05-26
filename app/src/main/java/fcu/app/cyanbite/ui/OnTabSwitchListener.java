package fcu.app.cyanbite.ui;

import fcu.app.cyanbite.model.Restaurant;

public interface OnTabSwitchListener {
    void onSwitchToMenu(Restaurant restaurant);  // 當 InfoFragment 中點選按鈕時呼叫
    void onSwitchToInfo();  // 當 MenuFragment 中點選按鈕時呼叫
}