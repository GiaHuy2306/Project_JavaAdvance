package service;

import dto.ChefItemView;
import dto.OrderItemView;
import java.util.List;

public interface IOrderItemService {
    void addItemByTable(int customerId, int tableId, int menuItemId, int quantity) throws Exception;
    List<OrderItemView> getByTable(int customerId, int tableId) throws Exception;
    void cancelItem(int customerId, int orderItemId) throws Exception;
    List<ChefItemView> getChefPendingItems() throws Exception;
    void advanceItemStatus(int orderItemId) throws Exception;

    void approveItem(int orderItemId) throws Exception;

    List<OrderItemView> getByTableForManager(int tableId) throws Exception;
}