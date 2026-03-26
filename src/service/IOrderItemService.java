package service;

import model.OrderItem;

import java.util.List;

public interface IOrderItemService {
    void addItem(int orderId, int menuId, int quantity) throws Exception;

    void updateStatus(int orderItemId, String status) throws Exception;

    List<OrderItem> findByOrder(int orderId) throws Exception;

    List<OrderItem> findByStatus(String status) throws Exception;

    void cancelItem(int orderItemId) throws Exception;
}
