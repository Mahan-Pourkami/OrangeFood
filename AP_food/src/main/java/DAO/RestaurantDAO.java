package DAO;

import Model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class RestaurantDAO {

    private final SessionFactory sessionFactory;
    public RestaurantDAO() {

    this.sessionFactory = new Configuration().configure()
            .addAnnotatedClass(Restaurant.class)
            .addAnnotatedClass(Food.class)
            .addAnnotatedClass(Seller.class)
            .addAnnotatedClass(Bankinfo.class)
            .addAnnotatedClass(Basket.class)
            .buildSessionFactory();
    }

    public void saveRestaurant(Restaurant restaurant) {

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(restaurant);
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving restaurant", e);
        }
    }
}
