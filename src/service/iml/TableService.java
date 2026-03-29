package service.iml;

import dao.iml.TableDAO;
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
        Table existing = dao.findById(table.getId());

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
    }

    @Override
    public void deleteTable(int id) throws Exception {
        Table table = dao.findById(id);

        if (table == null) {
            throw new Exception("Không tìm thấy bàn với ID này");
        }
        dao.delete(id);
    }

    @Override
    public Table findById(int id) throws Exception {
        return dao.findById(id);
    }

    @Override
    public List<Table> findStatusTable(TableStatus status) throws Exception {
        return dao.findStatusTable(status);
    }

    @Override
    public List<Table> findAll() throws Exception {
        return dao.findAll();
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
