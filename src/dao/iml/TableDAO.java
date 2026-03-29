package dao.iml;

import dao.ITableIDAO;
import model.MenuItem;
import model.Table;
import model.enums.FoodType;
import model.enums.MenuStatus;
import model.enums.TableStatus;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableDAO implements ITableIDAO {
    @Override
    public List<Table> findAll() throws SQLException {
        List<Table> list = new ArrayList<>();
        String sqlGetAll = "SELECT table_id, name, capacity, status FROM Tables";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlGetAll);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(map(rs));
        }
        return list;
    }

    @Override
    public Table findById(int id) throws SQLException {
        String sqlSearchId = "SELECT table_id, name, capacity, status FROM Tables WHERE table_id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlSearchId);

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return map(rs);
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

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlInsert);
        ps.setString(1, table.getName());
        ps.setInt(2, table.getCapacity());
        ps.setString(3, table.getStatus().name());

        ps.executeUpdate();
    }

    @Override
    public void update(Table table) throws SQLException {
        String sqlUpdate = "UPDATE Tables SET name=?, capacity=?, status=? WHERE table_id=?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlUpdate);

        ps.setString(1, table.getName());
        ps.setInt(2, table.getCapacity());
        ps.setString(3, table.getStatus().name());
        ps.setInt(4, table.getId());

        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sqlDelete = "DELETE FROM Tables WHERE table_id = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlDelete);

        ps.setInt(1, id);

        ps.executeUpdate();
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
