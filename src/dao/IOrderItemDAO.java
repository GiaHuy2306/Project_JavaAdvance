package dao;

import model.OrderItem;
import java.sql.Connection;
import java.util.List;

public interface IOrderItemDAO {
    OrderItem save(Connection conn, OrderItem item) throws Exception;
    List<OrderItem> findByOrderId(Connection conn, int orderId);
    OrderItem findById(Connection conn, int itemId);
    boolean updateQuantity(Connection conn, int itemId, int quantity);
    boolean updateStatus(Connection conn, int itemId, String status);
    boolean delete(Connection conn, int itemId);
}