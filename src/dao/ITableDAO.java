package dao;

import model.Table;
import model.enums.TableStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ITableDAO {
    void insert (Table item) throws SQLException;
    Table findById(Connection conn, int id) throws SQLException;
    List<Table> findAll(Connection conn) throws SQLException;
    List<Table> findStatusTable(TableStatus status) throws SQLException;
    void update(Table table) throws SQLException;
    boolean updateStatus(Connection conn, int tableId, String status);
    void delete(int id) throws SQLException;

}
