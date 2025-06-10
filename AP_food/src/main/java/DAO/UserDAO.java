package DAO;

import Model.Bankinfo;
import Model.User;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;
import java.util.function.Consumer;

public class UserDAO {

    private final SessionFactory sessionFactory;

    public UserDAO() {
        try {
            this.sessionFactory = new Configuration()
                    .configure() // loads hibernate.cfg.xml
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Bankinfo.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new DataAccessException("Failed to initialize Hibernate SessionFactory", ex);
        }
    }


    // Helper method for transactions
    private void executeInTransaction(Consumer<Session> operation) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            operation.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Database operation failed", e);
        }
    }

    // Helper method for queries


    public void saveUser(User user) {
        // Check if user exists first
        if (getUserByPhone(user.getPhone()) != null ){
            throw new DataAccessException("Phone number " + user.getPhone() + " already exists");
        }

        executeInTransaction(session -> session.persist(user));
    }

    public User getUserByPhone(String phone) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, phone);
            if (user != null) {
                return user;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to get user",e);
        }
    }
    public User getUserByPhoneAndPass(String phone, String password) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            System.out.println("try to get user");
            User user = session.find(User.class, phone);
            System.out.println("user found");
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to get user",e);
        }
    }

    // it creates a new user if it doesn't exist
    public void updateUser(User user) {
        executeInTransaction(session -> session.merge(user));
    }

    public void deleteUser(String phone) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, phone);
            if (user == null) {
                throw new DataAccessException("User with phone " + phone + " not found");
            }
            session.remove(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Failed to delete user", e);
        }
    }

    public List<User> getAllUsers() {

            Transaction transaction = null;
            try (Session session = sessionFactory.openSession()) {
                transaction = session.beginTransaction();

                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<User> cq = cb.createQuery(User.class);
                Root<User> root = cq.from(User.class);
                cq.select(root);

                List<User> users = session.createQuery(cq).getResultList();
                transaction.commit();
                return users;

            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new DataAccessException("Failed to retrieve users", e);
            }
            }

    public User getUserByEmail(String email) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            String hql = "FROM User U WHERE U.email = :email";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult(); // Attempts to get a single result, or null if none
            transaction.commit(); // Commit the read transaction
            return user;
        } catch (NoResultException e) {
            // No user found with the given email
            if(transaction!=null && transaction.isActive()) transaction.rollback();
            return null;
        } catch (Exception e) {
            // General exception during database operation
            if(transaction!=null && transaction.isActive()) transaction.rollback();
            throw new DataAccessException("Failed to get user by email", e);
        }
    }


    public void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    public static class DataAccessException extends RuntimeException {
        public DataAccessException(String message) {
            super(message);
        }

        public DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}