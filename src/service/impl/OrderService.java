package service.impl;

import dao.IOrderDAO;
import dao.impl.OrderDAO;
import model.Order;
import model.enums.OrderStatus;
import utils.DBConnection;
import service.IOrderService;

import java.sql.Connection;
import java.util.List;

public class OrderService implements IOrderService {

    private final IOrderDAO orderDAO = new OrderDAO();

    @Override
    public Order createOrder(int customerId, int tableId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            Order order = new Order();
            order.setCustomerId(customerId);
            order.setTableId(tableId);
            order.setStatus(OrderStatus.PENDING);
            order = orderDAO.save(conn, order);
            conn.commit();
            return order;
        } catch (Exception e) {
            throw new Exception("Tạo order thất bại: " + e.getMessage());
        }
    }

    @Override
    public Order getActiveByTableAndCustomer(int tableId, int customerId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            Order order = orderDAO.findActiveByTableAndCustomer(conn, tableId, customerId);
            if (order != null && order.getStatus().equals(OrderStatus.DELETE)) {
                return null;
            }
            return  order;
        } catch (Exception e) {
            throw new Exception("Của bạn không có bàn này");
        }
    }

    @Override
    public List<Order> getActiveOrdersByCustomer(int customerId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            return orderDAO.findActiveByCustomer(conn, customerId);
        } catch (Exception e) {
            throw new Exception("Của bạn không có Order này");
        }
    }

    @Override
    public void checkoutByTable(int customerId, int tableId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            Order order = getActiveByTableAndCustomer(tableId, customerId);
            if(order == null) throw new Exception("Không tìm thấy order để thanh toán");
            orderDAO.checkout(conn, order.getId());
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Thanh toán thất bại: " + e.getMessage());
        }
    }
}