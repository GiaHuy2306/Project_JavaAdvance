package dao.impl;

import dao.ITableDAO;
import model.Table;
import model.enums.TableStatus;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableDAO implements ITableDAO {
    @Override
    public List<Table> findAll(Connection conn) throws SQLException {
        List<Table> list = new ArrayList<>();
        String sqlGetAll = "SELECT table_id, name, capacity, status FROM Tables WHERE status != 'DELETED'";
        try (PreparedStatement ps = conn.prepareStatement(sqlGetAll);
             ResultSet rs = ps.executeQuery();) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public Table findById(Connection conn, int id) throws SQLException {
        String sqlSearchId = "SELECT table_id, name, capacity, status FROM Tables WHERE table_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sqlSearchId);) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }
        }
        return null;
    }

    @Override
    public List<Table> findStatusTable(TableStatus status) throws SQLException {
        String sqlSearchType = "SELECT table_id, name, capacity, status FROM Tables WHERE status = ?";
        List<Table> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlSearchType);
        ){
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public void insert(Table table) throws SQLException {
        String sqlInsert = "INSERT INTO Tables (name, capacity, status) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert);) {

            ps.setString(1, table.getName());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, table.getStatus().name());

            ps.executeUpdate();
        }
    }

    @Override
    public void update(Table table) throws SQLException {
        String sqlUpdate = "UPDATE Tables SET name=?, capacity=?, status=? WHERE table_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpdate);) {
            ps.setString(1, table.getName());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, table.getStatus().name());
            ps.setInt(4, table.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public boolean updateStatus(Connection conn, int tableId, String status) {
        String sql = "UPDATE tables SET status = ? WHERE table_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, tableId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sqlDelete = "UPDATE Tables SET status = ? WHERE table_id = ? AND status != 'DELETED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlDelete)){

            ps.setString(1, TableStatus.DELETED.name());
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Không tìm thấy bàn với ID này");
            }
        }
    }

    private Table map(ResultSet rs) throws SQLException{
        return new Table(
                rs.getInt( "table_id"),
                rs.getString("name"),
                rs.getInt("capacity"),
                TableStatus.fromString(rs.getString("status"))
        );
    }
}
