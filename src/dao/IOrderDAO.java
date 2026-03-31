package dao;

import model.Order;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IOrderDAO {
    // CRUD cơ bản
    Order save(Connection conn, Order order) throws Exception;
    Order findById(Connection conn, int orderId);
    Order findByIdAndCustomer(Connection conn, int orderId, int customerId);

    List<Order> findByCustomerId(int customerId);

    List<Order> findActiveByCustomer(Connection conn, int customerId) throws SQLException ;

    // nâng cao
    Order findActiveByTable(Connection conn, int tableId);
    boolean updateStatus(Connection conn, int orderId, String status);

    boolean updateTotal(Connection conn, int orderId, double total);

    // checkout
    boolean checkout(Connection conn, int orderId);

    List<Order> findActiveByTableAndCustomer(Connection conn, int tableId, int customerId);

    // admin
    List<Order> findAll();
}
