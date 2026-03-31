package dto;

public class OrderItemView {
    private int id;
    private int orderId;
    private String name;
    private double priceAtOrder;
    private int quantity;
    private String status;

    public OrderItemView(int id, int orderId, String name, double priceAtOrder, int quantity, String status) {
        this.id = id;
        this.orderId = orderId;
        this.name = name;
        this.priceAtOrder = priceAtOrder;
        this.quantity = quantity;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public double getPriceAtOrder() {
        return priceAtOrder;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }
}