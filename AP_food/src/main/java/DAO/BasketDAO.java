package DAO;

import Model.Bankinfo;
import Model.Basket;
import Model.Food;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class BasketDAO {

    final private  SessionFactory sessionFactory;
    public BasketDAO() {
        sessionFactory = new Configuration().configure().addAnnotatedClass(Basket.class).buildSessionFactory();
    }


    public void saveBasket(Basket basket) {

        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(basket);
            transaction.commit();
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to save basket",e);
        }
    }


    public void updateBasket(Basket basket) {
        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(basket);
            transaction.commit();
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to update basket",e);
        }
    }


    public void deleteBasket(String id) {
        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Basket basket = session.get(Basket.class, id);
            if(basket!=null) {
                session.remove(basket);
                transaction.commit();
            }
            else {
                throw new RuntimeException("basket not found");
            }
        }

        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to delete basket",e);
        }
    }


    public boolean existBasket(String id) {
        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Basket basket = session.get(Basket.class, id);
            if(basket!=null) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to find exist basket",e);
        }
    }



    public Basket getBasket(String id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Basket basket = session.get(Basket.class, id);
            if (basket != null) {
                return basket;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to get basket",e);
        }
    }


    public List<Basket> getAllBasket(){
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
            throw new UserDAO.DataAccessException("Failed to retrieve all baskets", e);
        }
    }
    public void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }





}
