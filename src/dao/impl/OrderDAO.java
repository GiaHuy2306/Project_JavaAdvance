package dao.impl;

import dao.IOrderDAO;
import model.Order;
import model.enums.OrderStatus;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO implements IOrderDAO {

    // ================= CREATE =================
    @Override
    public Order save(Connection conn, Order order) throws Exception {
        String sql = "INSERT INTO orders(customer_id, table_id, status) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getCustomerId());
            ps.setInt(2, order.getTableId());
            ps.setString(3, order.getStatus().name());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Không thể tạo order");

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) order.setId(rs.getInt(1));

            return order;
        }
    }

    // ================= READ =================
    @Override
    public Order findById(Connection conn, int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapToOrder(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Order findByIdAndCustomer(Connection conn, int orderId, int customerId) {
        String sql = "SELECT * FROM orders WHERE order_id = ? AND customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapToOrder(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Order> findByCustomerId(int customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY created_at DESC";
        return getOrders(sql, customerId);
    }

    // ================= ACTIVE ORDERS =================
    @Override
    public List<Order> findActiveByCustomer(Connection conn, int customerId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = """
            SELECT * FROM orders
            WHERE customer_id = ?
            AND status NOT IN (?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, OrderStatus.DONE.name());
            ps.setString(3, OrderStatus.CANCEL.name());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapToOrder(rs));
        }
        return list;
    }

    @Override
    public Order findActiveByTable(Connection conn, int tableId) {
        String sql = """
            SELECT * FROM orders
            WHERE table_id = ?
            AND status NOT IN (?, ?)
            ORDER BY created_at DESC
            LIMIT 1
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ps.setString(2, OrderStatus.DONE.name());
            ps.setString(3, OrderStatus.CANCEL.name());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapToOrder(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Order> findActiveByTableAndCustomer(Connection conn, int tableId, int customerId) {
        List<Order> list = new ArrayList<>();
        String sql = """
            SELECT * FROM orders
            WHERE table_id = ?
            AND customer_id = ?
            AND status NOT IN (?, ?)
            ORDER BY created_at DESC
            LIMIT 1
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ps.setInt(2, customerId);
            ps.setString(3, OrderStatus.DONE.name());
            ps.setString(4, OrderStatus.CANCEL.name());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapToOrder(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        return getOrders(sql);
    }

    // ================= UPDATE =================
    @Override
    public boolean updateStatus(Connection conn, int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateTotal(Connection conn, int orderId, double total) {
        String sql = "UPDATE orders SET total_amount = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, total);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= CHECKOUT =================
    @Override
    public boolean checkout(Connection conn, int orderId) {
        String sql = """
            UPDATE orders
            SET status = ?,
                checked_out_at = NOW()
            WHERE order_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, OrderStatus.DONE.name());
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= HELPER =================
    private List<Order> getOrders(String sql, Object... params) {
        List<Order> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapToOrder(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private Order mapToOrder(ResultSet rs) throws SQLException {
        Timestamp ca = rs.getTimestamp("created_at");
        Timestamp coa = rs.getTimestamp("checked_out_at");

        return new Order(
                rs.getInt("order_id"),
                rs.getInt("table_id"),
                rs.getInt("customer_id"),
                OrderStatus.fromString(rs.getString("status")),
                ca != null ? ca.toLocalDateTime() : null,
                coa != null ? coa.toLocalDateTime() : null,
                rs.getDouble("total_amount")
        );
    }
}