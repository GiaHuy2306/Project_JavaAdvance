package dao;

import model.Order;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IOrderDAO {
    Order save(Connection conn, Order order) throws Exception;
    Order findById(Connection conn, int orderId) throws SQLException;
    Order findActiveByTableAndCustomer(Connection conn, int tableId, int customerId) throws SQLException;
    List<Order> findActiveByCustomer(Connection conn, int customerId) throws SQLException;

    Order findActiveByTable(Connection conn, int tableId) throws SQLException;

    boolean updateStatus(Connection conn, int orderId, String status) throws SQLException;
    boolean updateTotal(Connection conn, int orderId, double total) throws SQLException;
    boolean checkout(Connection conn, int orderId) throws SQLException;
}