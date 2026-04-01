package service;

import model.Order;
import java.util.List;

public interface IOrderService {
    Order createOrder(int customerId, int tableId) throws Exception;
    Order getActiveByTableAndCustomer(int tableId, int customerId) throws Exception;
    List<Order> getActiveOrdersByCustomer(int customerId) throws Exception;

    Order getActiveOrderByTable(int tableId) throws Exception;

    void checkoutByTable(int customerId, int tableId) throws Exception;
}