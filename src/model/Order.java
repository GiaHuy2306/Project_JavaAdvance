package model;

import model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int id;
    private int tableId;
    private int customerId;
    private OrderStatus status;
    private LocalDateTime createAt;
    private LocalDateTime checkOutAt;
    private double totalAmount;

    public Order() {
    this.createAt = LocalDateTime.now();
}

    public Order(int id, int customerId, int tableId, OrderStatus status, LocalDateTime createAt, LocalDateTime checkOutAt, double totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.tableId = tableId;
        this.status = status;
        this.createAt = createAt;
        this.checkOutAt = checkOutAt;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public LocalDateTime getCheckOutAt() {
        return checkOutAt;
    }

    public void setCheckOutAt(LocalDateTime checkOutAt) {
        this.checkOutAt = checkOutAt;
    }

    public LocalDateTime getCreatedAt() {
        return createAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
