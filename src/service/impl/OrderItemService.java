package service.impl;

import dao.IOrderItemDAO;
import dao.impl.OrderItemDAO;
import model.OrderItem;
import model.enums.OrderItemStatus;
import service.IOrderItemService;
import service.IOrderService;
import utils.DBConnection;

import java.sql.Connection;
import java.util.List;

public class OrderItemService implements IOrderItemService {

    private final IOrderItemDAO orderItemDAO = new OrderItemDAO();
    private final IOrderService orderService = new OrderService();

    @Override
    public void addItemByTable(int customerId, int tableId, int menuItemId, int quantity) throws Exception {

        try (Connection conn = DBConnection.getConnection()) {
            var order = orderService.getActiveOrderByTableAndCustomer(tableId, customerId);
            if (order == null) throw new Exception("Không có quyền: bàn chưa có order hợp lệ!");
            if (order.getStatus() == null || order.getStatus().name().equals("DONE") || order.getStatus().name().equals("CANCEL")) {
                throw new Exception("Không có quyền: order đã thanh toán hoặc bị hủy!");
            }

            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setMenuId(menuItemId);
            item.setQuantity(quantity);
            item.setStatus(OrderItemStatus.PENDING);

            orderItemDAO.save(conn, item);
        }catch (Exception e){
            throw new Exception("Không tìm thấy bàn");
        }
    }

    @Override
    public List<OrderItem> getByTable(int customerId, int tableId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            var order = orderService.getActiveOrderByTableAndCustomer(tableId, customerId);
            if (order == null) throw new Exception("Không có order hợp lệ!");
            return orderItemDAO.findByOrderId(conn, order.getId());
        }catch (Exception e) {
            throw new Exception("Không tìm thấy bàn");
        }

    }

    @Override
    public void cancelItem(int customerId, int orderItemId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            OrderItem item = orderItemDAO.findById(conn, orderItemId);
            if (item == null) throw new Exception("Không tìm thấy món!");
            if (item.getStatus() == OrderItemStatus.READY || item.getStatus() == OrderItemStatus.SERVED) {
                throw new Exception("Không thể hủy món đã sẵn sàng hoặc đã phục vụ!");
            }

            var order = orderService.getActiveOrderByTableAndCustomer(item.getOrderId(), customerId);
            if (order == null) throw new Exception("Không có quyền hủy món trên order này!");

            orderItemDAO.updateStatus(conn, orderItemId, OrderItemStatus.CANCEL.name());
        }

    }
}