package com.geca.lostfound.service;

import com.geca.lostfound.dao.ItemDAO;
import com.geca.lostfound.model.Item;
import com.geca.lostfound.model.User;

import java.util.Date;
import java.util.List;

public class ItemService {

    private ItemDAO itemDAO = new ItemDAO();

    public void postItem(String title,
                         String description,
                         String category,
                         String location,
                         Date itemDate,
                         String type,
                         User user) {

        Item item = new Item();

        item.setTitle(title);
        item.setDescription(description);
        item.setCategory(category);
        item.setLocation(location);
        item.setItemDate(itemDate);
        item.setType(type);
        item.setStatus("OPEN");
        item.setUser(user);
        item.setCreatedAt(new java.util.Date());

        itemDAO.save(item);
    }

    public List<Item> getAllItems() {
        return itemDAO.getAllOpenItems();
    }

    public Item getById(Long id) {
        return itemDAO.findById(id);
    }

    public List<Item> search(String keyword) {
        return itemDAO.search(keyword);
    }

    public void closeItem(Long id) {
        Item item = itemDAO.findById(id);

        if (item != null) {
            item.setStatus("CLOSED");
            itemDAO.update(item);
        }
    }

    public void delete(Long id) {
        itemDAO.delete(id);
    }

    public List<Item> getArchive() {
        return itemDAO.getAllItemsArchive();
    }

    public List<Item> getByType(String type) {
        return itemDAO.getByType(type);
    }
}