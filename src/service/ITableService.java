package service;

import model.Table;

import java.util.List;

public interface ITableService {
    void add(Table table) throws Exception;

    void update(Table table) throws Exception;

    void delete(int id) throws Exception;

    List<Table> findAll() throws Exception;

    Table findById(int id) throws Exception;

    List<Table> findEmptyTables() throws Exception;
}
