package service;

import model.Table;
import model.enums.TableStatus;

import java.util.List;

public interface ITableService {
    void addTable(Table table) throws Exception;

    void updateTable(Table table) throws Exception;

    void deleteTable(int id) throws Exception;

    List<Table> findAll() throws Exception;

    Table findById(int id) throws Exception;

    List<Table> findStatusTable(TableStatus status) throws Exception;

    boolean existsByName(String name);
}
