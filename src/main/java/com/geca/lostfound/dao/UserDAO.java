package com.geca.lostfound.dao;

import com.geca.lostfound.model.User;
import com.geca.lostfound.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class UserDAO {

    public void save(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.save(user);

        tx.commit();
        session.close();
    }

    public User findByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Query<User> query = session.createQuery(
                "from User where email=:email", User.class);

        query.setParameter("email", email);

        User user = query.uniqueResult();

        session.close();
        return user;
    }

    public User findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        User user = session.get(User.class, id);
        session.close();
        return user;
    }
}