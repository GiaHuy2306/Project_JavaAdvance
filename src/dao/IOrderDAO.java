package dao;

import model.Order;
import java.sql.Connection;
import java.util.List;

public interface IOrderDAO {
    Order save(Connection conn, Order order) throws Exception;
    Order findById(Connection conn, int orderId);
    Order findActiveByTableAndCustomer(Connection conn, int tableId, int customerId);
    List<Order> findActiveByCustomer(Connection conn, int customerId);
    boolean updateStatus(Connection conn, int orderId, String status);
    boolean updateTotal(Connection conn, int orderId, double total);
    boolean checkout(Connection conn, int orderId);
}