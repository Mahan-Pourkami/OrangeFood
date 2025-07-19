package DAO;

import Model.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BasketDAO implements AutoCloseable {

    private final SessionFactory sessionFactory;

    public static class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }

        public DataAccessException(String message) {
            super(message);
        }
    }

    public BasketDAO() {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Basket.class)
                    .addAnnotatedClass(Food.class)
                    .addAnnotatedClass(Restaurant.class)
                    .addAnnotatedClass(Seller.class)
                    .addAnnotatedClass(Bankinfo.class)
                    .buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Error initializing SessionFactory: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public void saveBasket(Basket basket) {
        Objects.requireNonNull(basket, "Basket to save cannot be null.");

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(basket);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Failed to save basket: " + e.getMessage(), e);
        }
    }

    public void updateBasket(Basket basket) {
        Objects.requireNonNull(basket, "Basket to update cannot be null.");
        Objects.requireNonNull(basket.getId(), "Basket ID must not be null for update operation.");

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(basket);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Failed to update basket: " + e.getMessage(), e);
        }
    }

    public void deleteBasket(Long id) {
        Objects.requireNonNull(id, "Basket ID to delete cannot be null.");

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Basket basket = session.get(Basket.class, id);
            if (basket != null) {
                session.remove(basket);
                transaction.commit();
            } else {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new DataAccessException("Basket with ID " + id + " not found for deletion.");
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Failed to delete basket with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public boolean existBasket(Long id) {
        Objects.requireNonNull(id, "Basket ID to check existence cannot be null.");

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<Long> query = session.createQuery("SELECT COUNT(b.id) FROM Basket b WHERE b.id = :basketId", Long.class);
            query.setParameter("basketId", id);
            Long count = query.uniqueResult();
            transaction.commit();
            return count != null && count > 0;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Failed to check existence of basket with ID " + id + ": " + e.getMessage(), e);
        }
    }

    public Basket getBasket(Long id) {
        Objects.requireNonNull(id, "Basket ID to retrieve cannot be null.");

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Basket basket = session.get(Basket.class, id);
            // Force initialization of lazy collection
            basket.getItems().size(); // or use Hibernate.initialize(basket.getItems());
            transaction.commit();
            return basket;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Failed to retrieve basket with ID " + id + ": " + e.getMessage(), e);
        }
    }


    public List<Basket> getAllBasket() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Basket> cq = cb.createQuery(Basket.class);
            Root<Basket> root = cq.from(Basket.class);
            cq.select(root);

            List<Basket> baskets = session.createQuery(cq).getResultList();
            transaction.commit();
            return baskets;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Failed to retrieve all baskets: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getBasketIdAndPhone() {
        Transaction transaction = null;
        Session session = null;

        try {
            session = sessionFactory.openSession(); // manually open session
            transaction = session.beginTransaction(); // manually begin transaction

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<Basket> root = cq.from(Basket.class);

            // Use entity field names, not column names
            cq.multiselect(root.get("id"), root.get("buyerPhone"));

            List<Object[]> results = session.createQuery(cq).getResultList();

            transaction.commit(); // commit transaction
            return results;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback(); // rollback safely
                } catch (IllegalStateException ise) {
                    System.err.println("Rollback failed: " + ise.getMessage());
                }
            }
            throw new DataAccessException("Failed to retrieve basket data: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // always close session
            }
        }
    }

    public List<Basket> getBasketsByState(StateofCart state) {
        Objects.requireNonNull(state, "StateofCart must not be null.");

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "SELECT DISTINCT b FROM Basket b LEFT JOIN FETCH b.items WHERE b.stateofCart = :state";
            List<Basket> baskets = session.createQuery(hql, Basket.class)
                    .setParameter("state", state)
                    .getResultList();

            transaction.commit();
            return baskets;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Failed to retrieve baskets by state: " + e.getMessage(), e);
        }
    }

    public boolean is_in_the_order (long item_id ) {

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "SELECT COUNT(b) > 0 FROM Basket b " +
                    "WHERE :itemId IN (KEY(b.items)) " +
                    "AND b.stateofCart NOT IN (:excludedStates)";

            List<StateofCart> excludedStates = Arrays.asList(
                    StateofCart.acceptedbycourier,
                    StateofCart.rejected,
                    StateofCart.delivered
            );

            Boolean result = session.createQuery(hql, Boolean.class)
                    .setParameter("itemId", item_id)
                    .setParameter("excludedStates", excludedStates)
                    .getSingleResult();

            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }

    }


    public List<Basket> getBasketforvendor(Long vendorId) {

        List<Basket> baskets = getAllBasket();
        List <Basket> result = new ArrayList<>();

        for (Basket basket : baskets) {
            if (basket.getRes_id() == vendorId && (basket.getStateofCart().equals(StateofCart.waiting) || basket.getStateofCart().equals(StateofCart.received) || basket.getStateofCart().equals(StateofCart.accepted))) {
                result.add(basket);
            }
        }
        return result;

    }


    @Override
    public void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}