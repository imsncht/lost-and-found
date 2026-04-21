package com.geca.lostfound.dao;

import com.geca.lostfound.model.Claim;
import com.geca.lostfound.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ClaimDAO {

    public void save(Claim claim) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.save(claim);

        tx.commit();
        session.close();
    }

    public List<Claim> getAllPendingClaims() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        List<Claim> list = session.createQuery(
                "from Claim where status='PENDING'",
                Claim.class).list();

        session.close();
        return list;
    }

    public Claim findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Claim claim = session.get(Claim.class, id);

        session.close();
        return claim;
    }

    public void update(Claim claim) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.update(claim);

        tx.commit();
        session.close();
    }
}