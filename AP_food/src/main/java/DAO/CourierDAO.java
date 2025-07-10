package DAO;

import Model.Bankinfo;
import Model.Courier;
import Model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class CourierDAO {

    private final SessionFactory sessionFactory;
    public CourierDAO() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Courier.class)
                .addAnnotatedClass(Bankinfo.class)
                .addAnnotatedClass(User.class).configure().buildSessionFactory();
    }

    public void saveCourier(Courier courier) {
        Transaction transaction = null;

        try(Session session = sessionFactory.openSession()) {
        transaction = session.beginTransaction();
        session.persist(courier);
        transaction.commit();

        }
        catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving courier", e);
        }
    }


    public Courier getCourier(String phone) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
           Courier courier = session.get(Courier.class, phone);
            if (courier != null) {
                return courier;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to get courier",e);
        }
    }


    public List<Courier> getAllCouriers() {

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Courier> cq = cb.createQuery(Courier.class);
            Root<Courier> root = cq.from(Courier.class);
            cq.select(root);

            List<Courier> couriers = session.createQuery(cq).getResultList();
            transaction.commit();
            return couriers;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new UserDAO.DataAccessException("Failed to retrieve couriers", e);
        }
    }


    public void updateCourier(Courier courier) {
        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(courier);
            transaction.commit();
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to update courier",e);
        }
    }




}
