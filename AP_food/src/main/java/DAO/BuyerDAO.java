package DAO;

import Model.Bankinfo;
import Model.Basket;
import Model.Buyer;
import Model.Food;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * @author : Mahan Pourkami
 * @data : 11:49 am ~ 16/05/2025
 *
 */


public class BuyerDAO {

    private final SessionFactory sessionFactory;

    public BuyerDAO() {
        this.sessionFactory = new Configuration()
                .configure()
                 .addAnnotatedClass(Buyer.class)
                .addAnnotatedClass(Food.class)
                .addAnnotatedClass(Basket.class)
                .addAnnotatedClass(Bankinfo.class)
                .buildSessionFactory();
    }

    /**
     * @param buyer
     * @Save : create a new object and add it to the tables
     *
     *
     */
    public void saveBuyer(Buyer buyer) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(buyer);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to save buyer", e);
        }
    }


    /**
     * @param buyer
     * @operation : update the informations of a buyer in the tables
     *
     */
    public void updateBuyer(Buyer buyer) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(buyer);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to update buyer", e);
        }
    }


    /**
     * @param phone
     * @operation : delete the buyers from buyer's table & user's table if it is existed
     *
     *
     */

    public void deleteBuyer(String phone) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Buyer buyer = session.get(Buyer.class, phone);
            if (buyer != null) {
                session.remove(buyer);
                transaction.commit();

            }
            transaction.rollback();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to delete buyer", e);
        }
    }
    /**
     * @param phone
     * @return true if the buyers is existed
     *
     */
    public boolean buyerExists(String phone) {
        Buyer buyer = sessionFactory.getCurrentSession().get(Buyer.class, phone);
        return buyer != null;
    }

    public Buyer getBuyer(String phone) {
        Buyer buyer = sessionFactory.getCurrentSession().get(Buyer.class, phone);
        return buyer;
    }

    public List<Buyer> getAllBuyers() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Buyer> cq = cb.createQuery(Buyer.class);
            Root<Buyer> root = cq.from(Buyer.class);
            cq.select(root);

            List<Buyer> buyers = session.createQuery(cq).getResultList();
            transaction.commit();
            return buyers;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new UserDAO.DataAccessException("Failed to retrieve all buyers", e);
        }
    }

    /**
     * @operation : close the object that we have made from DAO
     *
     */
    public void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
