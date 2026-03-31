package service;

import model.Order;
import model.enums.OrderStatus;

import java.sql.Connection;
import java.util.List;

public interface IOrderService {

    void createOrder(int customerId, int tableId) throws Exception;

    List<Order> getActiveOrdersByCustomer(int customerId) throws Exception;

    Order getActiveOrderByTableAndCustomer(int tableId, int customerId) throws Exception;

    void checkoutByTable(int customerId, int tableId) throws Exception;

    void updateStatus(int orderId, OrderStatus status) throws Exception;
}
