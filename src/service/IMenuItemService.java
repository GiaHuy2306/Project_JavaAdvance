package service;

import model.MenuItem;

import java.util.List;

public interface IMenuItemService {
    void add(MenuItem item) throws Exception;

    void update(MenuItem item) throws Exception;

    void delete(int id) throws Exception;

    MenuItem findById(int id) throws Exception;

    List<MenuItem> findAll() throws Exception;
}
