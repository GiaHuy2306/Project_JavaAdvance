package service;

import dto.OrderItemView;
import model.OrderItem;

import java.sql.Connection;
import java.util.List;

public interface IOrderItemService {


    void addItemByTable(int customerId, int tableId, int menuItemId, int quantity) throws Exception;

    List<OrderItemView> getByTable(int customerId, int tableId) throws Exception;

    void cancelItem(int customerId, int orderItemId) throws Exception;
}
