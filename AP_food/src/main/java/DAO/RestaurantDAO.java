package DAO;

import Model.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void updateRestaurant(Restaurant restaurant) {
        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(restaurant);
            transaction.commit();
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to update Restaurant",e);
        }
    }

    public Restaurant get_restaurant(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Restaurant res = session.get(Restaurant.class, id);
            if (res != null) {
                return res;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to get seller",e);
        }
    }


    public List<Restaurant> getAllRestaurants() {

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Restaurant> cq = cb.createQuery(Restaurant.class);
            Root<Restaurant> root = cq.from(Restaurant.class);
            cq.select(root);

            List<Restaurant> vendors = session.createQuery(cq).getResultList();
            transaction.commit();
            return vendors;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new UserDAO.DataAccessException("Failed to retrieve Restaurants", e);
        }
    }

    public Set<Restaurant> findbyfilters(String name) {

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "SELECT DISTINCT r FROM Restaurant r " +
                    "WHERE LOWER(r.name) LIKE LOWER(:searchTerm) " +
                    "OR LOWER(r.address) LIKE LOWER(:searchTerm)";

            Set<Restaurant> result = new HashSet<>(
                    session.createQuery(hql, Restaurant.class)
                            .setParameter("searchTerm", "%" + name + "%")
                            .getResultList()
            );

            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

}
