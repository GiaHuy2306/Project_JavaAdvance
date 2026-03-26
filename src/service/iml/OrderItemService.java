package service.iml;

import model.OrderItem;
import service.IOrderItemService;

import java.util.List;

public class OrderItemService implements IOrderItemService {
    @Override
    public void addItem(int orderId, int menuId, int quantity) throws Exception {

    }

    @Override
    public void updateStatus(int orderItemId, String status) throws Exception {

    }

    @Override
    public void cancelItem(int orderItemId) throws Exception {

    }

    @Override
    public List<OrderItem> findByOrder(int orderId) throws Exception {
        return List.of();
    }

    @Override
    public List<OrderItem> findByStatus(String status) throws Exception {
        return List.of();
    }
}
