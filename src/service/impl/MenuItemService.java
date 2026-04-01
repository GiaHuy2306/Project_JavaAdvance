package service.impl;

import dao.impl.MenuItemDAO;
import model.MenuItem;
import model.enums.FoodType;
import model.enums.MenuStatus;
import service.IMenuItemService;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuItemService implements IMenuItemService {
    private MenuItemDAO dao = new MenuItemDAO();
    @Override
    public void addMenu(String name, FoodType foodType, double price, int stock) throws Exception {
        if (name.isEmpty()) throw new Exception("Tên menu item không được trống");
        if (price <= 0) throw new Exception("Giá menu item phải lớn hơn 0");
        if (stock < 0) throw new Exception("Số lượng menu item không được âm 0");

        MenuStatus status = (stock > 0) ? MenuStatus.AVAILABLE : MenuStatus.OUT_OF_STOCK;

        MenuItem item = new MenuItem(0,name.trim(), foodType, price, stock, status);

        try {
            dao.insert(item);
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể thêm món ăn mới vào CSDL (" + e.getMessage() + ")");
        }
    }

    @Override
    public void updateMenuItem(MenuItem item) throws Exception {

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                MenuItem existing = dao.findById(conn, item.getId());

                if (existing == null){
                    throw new Exception("Không tìm thấy menu item với ID này");
                }

                if (item.getName() == null || item.getName().trim().isEmpty()) {
                    throw new Exception("Tên menu item không được trống");
                }
                if (item.getPrice() <= 0) throw new Exception("Giá phải lớn hơn 0");
                if (item.getStock() < 0) throw new Exception("Số lượng phải >= 0");

                existing.setName(item.getName().trim());
                existing.setFoodType(item.getFoodType());
                existing.setPrice(item.getPrice());

                existing.updateStock(item.getStock());

                if (existing.getStock() > 0) {
                    existing.setStatus(MenuStatus.AVAILABLE);
                } else {
                    existing.setStatus(MenuStatus.OUT_OF_STOCK);
                }

                existing.setStatus(existing.getStock() > 0 ? MenuStatus.AVAILABLE : MenuStatus.OUT_OF_STOCK);

                dao.update(conn, existing);
                conn.commit();

            } catch (SQLException e) {
                try {
                    if (conn != null) conn.rollback();
                } catch (Exception rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
                throw new Exception("Lỗi hệ thống: Không thể cập nhật vào cơ sở dữ liệu (" + e.getMessage() + ")");
            }
        }
    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            MenuItem item = dao.findById(conn, id);

            if (item == null){
                throw new Exception("Không tìm thấy món ăn có ID " + id + " để xóa!");
            }
            dao.delete(conn, id);
        }catch (SQLException e){
            throw new Exception("Lỗi hệ thống: Không thể xóa món ăn do ràng buộc dữ liệu!");
        }
    }

    @Override
    public MenuItem findById(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            MenuItem item = dao.findById(conn, id);
            if (item == null) {
                throw new Exception("Lỗi: Không tìm thấy món ăn có ID: " + id);
            }
            return item;
        }catch (SQLException e){
            throw new Exception("Lỗi hệ thống: Quá trình kết nối database bị gián đoạn!");
        }
    }

    @Override
    public List<MenuItem> findByType(FoodType foodType) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            List<MenuItem> result = dao.findByType(conn, foodType);
            if (result.isEmpty()) {
                throw new Exception("Hiện không có món nào thuộc loại " + foodType);
            }
            return result;
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lọc món ăn theo loại!");
        }
    }

    @Override
    public List<MenuItem> findAvailableMenus() throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            List<MenuItem> allMenus = dao.findAll();
            List<MenuItem> availableMenus = new ArrayList<>();

            for (MenuItem item : allMenus) {
                if (item.getStatus() == MenuStatus.AVAILABLE && item.getStock() > 0) {
                    availableMenus.add(item);
                }
            }
            return availableMenus;
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống khi tải Menu.");
        }
    }

    @Override
    public List<MenuItem> findAll() throws Exception {
        try {
            return dao.findAll();
        } catch (SQLException e) {
            throw new Exception("Lỗi hệ thống: Không thể lấy danh sách thực đơn!");
        }
    }
}
