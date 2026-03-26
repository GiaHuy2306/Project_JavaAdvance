package service.iml;

import model.Order;
import service.IOrderService;

import java.util.List;

public class OrderService implements IOrderService {
    @Override
    public int createOrder(int tableId, int customerId) throws Exception {
        return 0;
    }

    @Override
    public Order findById(int id) throws Exception {
        return null;
    }

    @Override
    public List<Order> findByCustomer(int customerId) throws Exception {
        return List.of();
    }

    @Override
    public void closeOrder(int orderId) throws Exception {

    }
}
