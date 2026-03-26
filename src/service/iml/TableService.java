package service.iml;

import model.Table;
import service.ITableService;

import java.util.List;

public class TableService implements ITableService {
    @Override
    public void add(Table table) throws Exception {

    }

    @Override
    public void update(Table table) throws Exception {

    }

    @Override
    public void delete(int id) throws Exception {

    }

    @Override
    public Table findById(int id) throws Exception {
        return null;
    }

    @Override
    public List<Table> findEmptyTables() throws Exception {
        return List.of();
    }

    @Override
    public List<Table> findAll() throws Exception {
        return List.of();
    }
}
