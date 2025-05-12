package fcu.app.cyanbite.ui;

public interface OnGroupSwitchListener {

        /**
         * 切換到 Info Fragment 時呼叫
         */
        void onSwitchToGroupInfo();

        /**
         * 切換到 Menu Fragment 時呼叫
         */
        void onSwitchToGroupMenu();

        /**
         * 通用切換 tab 事件，可根據 index 判斷要切去哪個 Fragment
         * @param tabIndex 目前切換到的 tab index，0 為 Info，1 為 Menu
         */
        void onGroupTabSwitched(int tabIndex);

}
