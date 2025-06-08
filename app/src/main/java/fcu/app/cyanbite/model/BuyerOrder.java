package fcu.app.cyanbite.model;

import java.util.List;

public class BuyerOrder {
    private String buyerName;
    private double totalPrice;
    private List<OrderItem> items; // This list will hold the individual food items ordered by this buyer

    public BuyerOrder() {
        // A no-argument constructor is needed for Firebase Firestore,
        // even though this specific model is assembled in your app code, not directly from Firestore.
    }

    /**
     * Constructs a BuyerOrder object.
     * @param buyerName The name of the buyer.
     * @param totalPrice The total price for all items ordered by this buyer.
     * @param items A list of OrderItem objects representing the individual items the buyer ordered.
     */
    public BuyerOrder(String buyerName, double totalPrice, List<OrderItem> items) {
        this.buyerName = buyerName;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    // --- Getter Methods ---
    public String getBuyerName() {
        return buyerName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    // --- Setter Methods ---
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}