package com.geca.lostfound.util;

import com.geca.lostfound.dao.ItemDAO;
import com.geca.lostfound.model.Item;

import java.util.List;
import java.util.TimerTask;

public class ExpiryJob extends TimerTask {

    private ItemDAO itemDAO = new ItemDAO();

    @Override
    public void run() {

        List<Item> items = itemDAO.getAllOpenItems();

        for (Item item : items) {

            if (item.getItemDate() != null) {

                long diff =
                        System.currentTimeMillis()
                        - item.getItemDate().getTime();

                long days = diff / (1000 * 60 * 60 * 24);

                if (days > 30) {
                    item.setStatus("CLOSED");
                    itemDAO.update(item);
                }
            }
        }

        System.out.println("ExpiryJob executed.");
    }
}