package com.mitchellbosecke.seniorcommander.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2017-01-28.
 */
public class TransactionManager {

    private static SessionFactory sessionFactory;

    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    private TransactionManager() {

    }

    public static void initiate(SessionFactory sessionFactory) {
        TransactionManager.sessionFactory = sessionFactory;
    }

    public static void runInTransaction(TransactionalRunnable runnable) {
        Session session = TransactionManager.sessionFactory.getCurrentSession();
        session.beginTransaction();
        try {
            runnable.run(session);
            session.getTransaction().commit();
        } catch (Exception ex) {
            logger.error("Transaction rolled back", ex);
            session.getTransaction().rollback();

            throw ex;
        } finally {
            session.close();
        }
    }

    public static Session getCurrentSession() {
        Session session = sessionFactory.getCurrentSession();
        if (!session.isJoinedToTransaction()) {
            throw new RuntimeException("Getting session outside of a transaction");
        }
        return session;
    }

    public static void shutdown(){
        sessionFactory.close();
    }

    @FunctionalInterface
    public interface TransactionalRunnable {
        void run(Session session);
    }
}
