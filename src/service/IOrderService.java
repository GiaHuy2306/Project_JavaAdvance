package service;

import model.Order;

import java.util.List;

public interface IOrderService {
    int createOrder(int tableId, int customerId) throws Exception;

    Order findById(int id) throws Exception;

    List<Order> findByCustomer(int customerId) throws Exception;

    void closeOrder(int orderId) throws Exception;
}
