package dao.impl;

import dao.IOrderDAO;
import model.Order;
import model.enums.OrderStatus;
import model.enums.TableStatus;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO implements IOrderDAO {

    @Override
    public Order save(Connection conn, Order order) throws Exception {
        String sql = "INSERT INTO orders(customer_id, table_id, status) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getCustomerId());
            ps.setInt(2, order.getTableId());
            ps.setString(3, order.getStatus().name());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) order.setId(rs.getInt(1));
            return order;
        }
    }

    @Override
    public Order findById(Connection conn, int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return map(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public Order findActiveByTableAndCustomer(Connection conn, int tableId, int customerId) {
        String sql = "SELECT * FROM orders WHERE table_id=? AND customer_id=? AND status!=? ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ps.setInt(2, customerId);
            ps.setString(3, OrderStatus.DONE.name());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return map(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Order> findActiveByCustomer(Connection conn, int customerId) {
        List<Order> list = new ArrayList<>();
        String sql = """
            SELECT o.*, t.name AS table_name, t.status AS table_status
            FROM orders o
            JOIN tables t ON o.table_id = t.table_id
            WHERE o.customer_id = ?
              AND o.status != ?
              AND t.status != ?
            ORDER BY o.created_at DESC;
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, OrderStatus.DONE.name());
            ps.setString(3, TableStatus.DELETED.name());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updateStatus(Connection conn, int orderId, String status) {
        String sql = "UPDATE orders SET status=? WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean updateTotal(Connection conn, int orderId, double total) {
        String sql = "UPDATE orders SET total_amount=? WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, total);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean checkout(Connection conn, int orderId) {
        String sql = "UPDATE orders SET status=?, checked_out_at=NOW() WHERE order_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, OrderStatus.DONE.name());
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private Order map(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("order_id"),
                rs.getInt("table_id"),
                rs.getInt("customer_id"),
                OrderStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                rs.getTimestamp("checked_out_at") != null ? rs.getTimestamp("checked_out_at").toLocalDateTime() : null,
                rs.getDouble("total_amount")
        );
    }
}