package com.geca.lostfound.util;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {

            Configuration configuration = new Configuration();

            File localConfig =
                new File("src/main/resources/hibernate.local.cfg.xml");

            if (localConfig.exists()) {

                System.out.println(
                    "Using local Hibernate config: hibernate.local.cfg.xml"
                );

                configuration.configure(localConfig);

            } else {

                System.out.println(
                    "Using default Hibernate config: hibernate.cfg.xml"
                );

                configuration.configure("hibernate.cfg.xml");
            }

            sessionFactory =
                configuration.buildSessionFactory();

        } catch (Throwable ex) {

            System.out.println("Hibernate Error: " + ex);

            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}