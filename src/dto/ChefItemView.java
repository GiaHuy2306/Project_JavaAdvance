package dto;

import model.enums.FoodType;

public class ChefItemView {
    private int orderItemId;
    private String tableName;
    private String itemName;
    private FoodType type; // "FOOD" hoặc "DRINK"
    private int quantity;
    private String status;

    public ChefItemView(int orderItemId, String tableName, String itemName, FoodType type, int quantity, String status) {
        this.orderItemId = orderItemId;
        this.tableName = tableName;
        this.itemName = itemName;
        this.type = type;
        this.quantity = quantity;
        this.status = status;
    }

    public void setCategory(FoodType type) {
        this.type = type;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getOrderItemId() { return orderItemId; }
    public String getTableName() { return tableName; }
    public String getItemName() { return itemName; }
    public FoodType getType() { return type; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
}