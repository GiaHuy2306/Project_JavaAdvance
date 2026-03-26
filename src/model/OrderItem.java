package model;

import model.enums.OrderItemStatus;

public class OrderItem {
    private int id;
    private int orderId;
    private int menuId;
    private int quantity;
    private OrderItemStatus status;

    public OrderItem(int id, int menuId, int orderId, int quantity, OrderItemStatus status) {
        this.id = id;
        this.menuId = menuId;
        this.orderId = orderId;
        this.quantity = quantity;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuItem() {
        return menuId;
    }

    public void setMenuItem(int menuId) {
        this.menuId = menuId;
    }

    public int getOrder() {
        return orderId;
    }

    public void setOrder(int orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }
}
