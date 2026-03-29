package dao;

import model.MenuItem;
import model.Table;
import model.enums.FoodType;
import model.enums.TableStatus;

import java.sql.SQLException;
import java.util.List;

public interface ITableIDAO {
    void insert (Table item) throws SQLException;
    Table findById(int id) throws SQLException;
    List<Table> findAll() throws SQLException;
    List<Table> findStatusTable(TableStatus status) throws SQLException;
    void update(Table table) throws SQLException;
    void delete(int id) throws SQLException;

}
