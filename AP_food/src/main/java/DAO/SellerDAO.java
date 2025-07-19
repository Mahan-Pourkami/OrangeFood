package DAO;

import Model.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import java.util.List;


public class SellerDAO {

   final private  SessionFactory sessionFactory;
   public SellerDAO() {
       sessionFactory = new Configuration().configure()
               .addAnnotatedClass(Seller.class)
               .addAnnotatedClass(Bankinfo.class)
               .addAnnotatedClass(User.class)
               .addAnnotatedClass(Restaurant.class)
               .addAnnotatedClass(Food.class)
               .addAnnotatedClass(Basket.class)
               .buildSessionFactory();
   }


   public void saveSeller(Seller seller) {

       Transaction transaction = null ;
       try (Session session = sessionFactory.openSession()) {
           transaction = session.beginTransaction();
           session.persist(seller);
           transaction.commit();
       }
       catch (Exception e) {
           if(transaction!=null)transaction.rollback();
           throw new RuntimeException("failed to save seller",e);
       }
   }


   public void updateSeller(Seller seller) {
       Transaction transaction = null ;
       try (Session session = sessionFactory.openSession()) {
           transaction = session.beginTransaction();
           session.merge(seller);
           transaction.commit();
       }
       catch (Exception e) {
           if(transaction!=null)transaction.rollback();
           throw new RuntimeException("failed to update seller",e);
       }
   }


   public void deleteSeller(String phone) {
       Transaction transaction = null ;
       try (Session session = sessionFactory.openSession()) {
           transaction = session.beginTransaction();
           Seller seller = session.get(Seller.class, phone);
           if(seller!=null) {
               session.remove(seller);
               transaction.commit();
           }
           else {
               throw new RuntimeException("seller not found");
           }
       }

       catch (Exception e) {
           e.printStackTrace();
           if(transaction!=null)transaction.rollback();
           throw new RuntimeException("failed to delete seller",e);
       }
   }


   public boolean existSeller(String phone) {
       Transaction transaction = null ;
       try (Session session = sessionFactory.openSession()) {
           transaction = session.beginTransaction();
           Seller seller = session.get(Seller.class, phone);
           if(seller!=null) {
               return true;
           }
           else {
               return false;
           }
       }
       catch (Exception e) {
           if(transaction!=null)transaction.rollback();
           throw new RuntimeException("failed to find exist seller",e);
       }
   }



   public Seller getSeller(String phone) {
       Transaction transaction = null;
       try (Session session = sessionFactory.openSession()) {
           transaction = session.beginTransaction();
           Seller seller = session.get(Seller.class, phone);
           if (seller != null) {
               return seller;
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


   public List<Seller> getAllSellers(){
       Transaction transaction = null;
       try (Session session = sessionFactory.openSession()) {
           transaction = session.beginTransaction();

           CriteriaBuilder cb = session.getCriteriaBuilder();
           CriteriaQuery<Seller> cq = cb.createQuery(Seller.class);
           Root<Seller> root = cq.from(Seller.class);
           cq.select(root);

           List<Seller> sellers = session.createQuery(cq).getResultList();
           transaction.commit();
           return sellers;

       } catch (Exception e) {
           if (transaction != null && transaction.isActive()) {
               transaction.rollback();
           }
           throw new UserDAO.DataAccessException("Failed to retrieve all Sellers", e);
       }
    }
}

