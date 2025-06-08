package fcu.app.cyanbite.model;

public class OrderItem {
    private String foodName; // 品項名稱 (例如：經典卡布奇諾(熱))
    private int quantity;    // 數量 (例如：1)
    private double price;    // 單價 (例如：69.0)

    public OrderItem() {
        // Firebase Firestore 需要一個無參數的建構函式，用於反序列化。
        // 雖然這個模型主要是在應用程式內部從 Order 物件轉換而來，
        // 但如果未來有將其直接作為 Firestore 陣列中的物件儲存的需求，這個建構函式是必要的。
    }

    /**
     * 建構一個訂單品項的物件。
     * @param foodName 食物品項的名稱。
     * @param quantity 該品項的數量。
     * @param price 該品項的單價。
     */
    public OrderItem(String foodName, int quantity, double price) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.price = price;
    }

    // --- Getter Methods ---
    public String getFoodName() {
        return foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    // 注意：Firebase 數字類型可能是 Long 或 Double，
    // 這裡使用 double 以確保處理浮點數。
    public double getPrice() {
        return price;
    }

    // --- Setter Methods ---
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}