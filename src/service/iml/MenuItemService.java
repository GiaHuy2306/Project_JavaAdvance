package service.iml;

import dao.iml.MenuItemDAO;
import model.MenuItem;
import model.enums.FoodType;
import model.enums.MenuStatus;
import service.IMenuItemService;

import java.awt.*;
import java.util.List;

public class MenuItemService implements IMenuItemService {
    private MenuItemDAO dao = new MenuItemDAO();
    @Override
    public void addMenu(String name, FoodType foodType, double price, int stock) throws Exception {
        if (name.isEmpty()) throw new Exception("Tên menu item không được trống");
        if (price <= 0) throw new Exception("Giá menu item phải lớn hơn 0");
        if (stock < 0) throw new Exception("Số lượng menu item phải lớn hơn 0");

        MenuStatus status = (stock > 0) ? MenuStatus.AVAILABLE : MenuStatus.OUT_OF_STOCK;

        MenuItem item = new MenuItem(0,name, price, foodType, stock, status);

        dao.insert(item);
    }

    @Override
    public void updateMenuItem(MenuItem item) throws Exception {
        MenuItem existing = dao.findById(item.getId());

        if (existing == null){
            throw new Exception("Không tìm thấy menu item với id này");
        }

        if (item.getName().isEmpty()) throw new Exception("Tên menu item không được trống");
        if (item.getPrice() <= 0) throw new Exception("Giá phải lớn hơn 0");
        if (item.getStock() < 0) throw new Exception("Số lượng phải >= 0");

        existing.setName(item.getName());
        existing.setFoodType(item.getFoodType());
        existing.setPrice(item.getPrice());
        existing.updateStock(item.getStock());
        if (existing.getStock() > 0) {
            existing.setStatus(MenuStatus.AVAILABLE);
        } else {
            existing.setStatus(MenuStatus.OUT_OF_STOCK);
        }
        dao.update(existing);
    }

    @Override
    public void delete(int id) throws Exception {
        MenuItem item = dao.findById(id);

        if (item == null){
            throw new Exception("Không tìm thấy menu item với id này");
        }
        dao.delete(id);
    }

    @Override
    public MenuItem findById(int id) throws Exception {
        return dao.findById(id);
    }

    @Override
    public List<MenuItem> findByType(FoodType foodType) throws Exception {
        return dao.findByType(foodType);
    }

    @Override
    public List<MenuItem> findAll() throws Exception {
        return dao.findAll();
    }
}
