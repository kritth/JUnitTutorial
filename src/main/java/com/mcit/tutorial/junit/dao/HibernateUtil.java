package com.mcit.tutorial.junit.dao;

import com.mcit.tutorial.junit.config.HibernateConfiguration;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;

/**
 * Hibernate utility class
 *
 * @author Kasidit
 *
 * @param <T> entity
 * @param <U> id type
 */
public abstract class HibernateUtil<T, U extends Serializable> {

    private static Session getSession() {
        return HibernateConfiguration.getSessionFactory().openSession();
    }

    @SuppressWarnings("unchecked")
    List<T> findAll(Class<T> clazz) {
        try (Session session = getSession()) {
            return (List<T>) session.createQuery("FROM " + clazz.getName()).list();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    T findById(Class<T> clazz, U id) {
        try (Session session = getSession()) {
            T obj = session.load(clazz, id);
            Hibernate.initialize(obj);
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    T save(T obj) {
        Session session = getSession();
        Transaction t = session.beginTransaction();
        try {
            session.saveOrUpdate(obj);
            t.commit();
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            t.rollback();
            return null;
        } finally {
            session.close();
        }
    }

    public T delete(T obj) {
        Session session = getSession();
        Transaction t = session.beginTransaction();
        try {
            session.delete(obj);
            t.commit();
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            t.rollback();
            return null;
        } finally {
            session.close();
        }
    }

    public void deleteAll(Class<T> clazz) {
        Session session = getSession();
        Transaction t = session.beginTransaction();
        try {
            session.createQuery("DELETE FROM " + clazz.getName()).executeUpdate();
            t.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            t.rollback();
        } finally {
            session.close();
        }
    }
}