package service.impl;

import dao.impl.TableDAO;
import model.Table;
import model.enums.TableStatus;
import service.ITableService;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TableService implements ITableService {
    private TableDAO dao = new TableDAO();
    @Override
    public void addTable(Table table) throws Exception {
        String name = table.getName().trim();
        table.setName(name);

        if (name.isEmpty()) {
            throw new Exception("Tên bàn không được để trống");
        }

        if (table.getCapacity() <= 0) {
            throw new Exception("Số lượng chỗ ngồi phải > 0");
        }

        if (existsByName(name)) {
            throw new RuntimeException("Tên bàn đã tồn tại");
        }

        dao.insert(table);

    }

    @Override
    public void updateTable(Table table) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            Table existing = dao.findById(conn, table.getId());

            if (existing == null) {
                throw new Exception("Không tìm thấy bàn với ID này");
            }

            if (!existing.getName().equalsIgnoreCase(table.getName())
                    && existsByName(table.getName())) {
                throw new RuntimeException("Tên bàn đã tồn tại");
            }

            if (table.getName().isEmpty()) throw new Exception("Tên bàn không được để trống");
            if (table.getCapacity() <= 0) throw new Exception("Số lượng chỗ ngồi phải > 0");

            existing.setName(table.getName());
            existing.setCapacity(table.getCapacity());
            existing.setStatus(table.getStatus());

            dao.update(existing);
        }catch (Exception e){
            throw new Exception("Cập nhật thất bại");
        }

    }

    @Override
    public void deleteTable(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            Table table = dao.findById(conn, id);

            if (table == null) {
                throw new Exception("Không tìm thấy bàn với ID này");
            }

            if (table.getStatus() == TableStatus.DELETED) {
                throw new Exception("Bàn đã bị xóa");
            }

            if (table.getStatus() == TableStatus.FULL){
                throw new Exception("Bàn đang có khách, không thể xóa");
            }
            dao.delete(id);
        }catch (Exception e){
            throw new Exception("Xóa thất bại");
        }
    }

    @Override
    public Table findById(int id) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            return dao.findById(conn, id);
        }catch (Exception e){
            throw new Exception("Tìm kiếm thất bại");
        }
    }

    @Override
    public List<Table> findStatusTable(TableStatus status) throws Exception {
        return dao.findStatusTable(status);
    }

    @Override
    public List<Table> findAll() throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            return dao.findAll(conn);
        }catch (Exception e){
            throw new Exception("Tìm kiếm thất bại");
        }
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM tables WHERE LOWER(TRIM(name)) = LOWER(TRIM(?))";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
