package service.impl;

import dao.IOrderDAO;
import dao.impl.OrderDAO;
import model.Order;
import model.enums.OrderStatus;
import service.IOrderService;
import utils.DBConnection;

import java.sql.Connection;
import java.util.List;

public class OrderService implements IOrderService {

    private final IOrderDAO orderDAO = new OrderDAO();

    @Override
    public void createOrder(int customerId, int tableId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            Order active = orderDAO.findActiveByTable(conn, tableId);
            if (active != null) {
                throw new Exception("Bàn này đã có order đang hoạt động!");
            }

            Order order = new Order();
            order.setCustomerId(customerId);
            order.setTableId(tableId);
            order.setStatus(OrderStatus.PENDING);

            orderDAO.save(conn, order);
        }
    }

    @Override
    public List<Order> getActiveOrdersByCustomer(int customerId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            return orderDAO.findActiveByCustomer(conn, customerId);
        }
    }

    @Override
    public Order getActiveOrderByTableAndCustomer(int tableId, int customerId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            List<Order> list = orderDAO.findActiveByTableAndCustomer(conn, tableId, customerId);
            return list.isEmpty() ? null : list.get(0);
        }
    }

    @Override
    public void checkoutByTable(int customerId, int tableId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            Order order = getActiveOrderByTableAndCustomer(tableId, customerId);
            if (order == null) throw new Exception("Không tìm thấy order hợp lệ để thanh toán!");
            orderDAO.checkout(conn, order.getId());
        }
    }

    @Override
    public void updateStatus(int orderId, OrderStatus status) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            orderDAO.updateStatus(conn, orderId, status.name());
        }
    }
}