package com.geca.lostfound.dao;

import com.geca.lostfound.model.Item;
import com.geca.lostfound.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ItemDAO {

    public void save(Item item) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.save(item);

        tx.commit();
        session.close();
    }

    public List<Item> getAllOpenItems() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        List<Item> list = session.createQuery(
                "from Item where status='OPEN' order by id desc",
                Item.class).list();

        session.close();
        return list;
    }

    public Item findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Item item = session.get(Item.class, id);
        session.close();
        return item;
    }

    public List<Item> search(String keyword) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Query<Item> query = session.createQuery(
                "from Item where title like :k or category like :k or location like :k",
                Item.class);

        query.setParameter("k", "%" + keyword + "%");

        List<Item> list = query.list();

        session.close();
        return list;
    }

    public void update(Item item) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.update(item);

        tx.commit();
        session.close();
    }

    public void delete(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Item item = session.get(Item.class, id);
        if (item != null) session.delete(item);

        tx.commit();
        session.close();
    }
}