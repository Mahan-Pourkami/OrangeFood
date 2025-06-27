package DAO;

import Model.Coupon;
import Model.Seller;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class CouponDAO {

    private final SessionFactory sessionFactory;

    public CouponDAO() {

            this.sessionFactory = new Configuration()
                    .addAnnotatedClass(Coupon.class)
                    .configure()
                    .buildSessionFactory();
    }

    public void saveCoupon(Coupon coupon) {

        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(coupon);
            transaction.commit();
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to save coupon",e);
        }
    }

    public void updateCoupon(Coupon coupon) {
        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(coupon);
            transaction.commit();
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to update coupon",e);
        }
    }

    public void deleteCoupon(long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Seller seller = session.get(Seller.class, id);
            if (seller != null) {
                session.remove(seller);
                transaction.commit();
            } else {
                throw new RuntimeException("seller not found");
            }
        }
    }

}
