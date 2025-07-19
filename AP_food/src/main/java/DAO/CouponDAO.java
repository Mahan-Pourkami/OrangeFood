package DAO;

import Exceptions.InvalidInputException;
import Model.Coupon;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

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

    public Coupon getCoupon(long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Coupon coupon = session.get(Coupon.class, id);
            if (coupon != null) {
                return coupon;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to get coupon",e);
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
            Coupon coupon= session.get(Coupon.class, id);
            if (coupon != null) {
                session.remove(coupon);
                transaction.commit();
            } else {
                throw new RuntimeException("coupon not found");
            }
        }
    }


    public List<Coupon> getAllCoupons(){

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Coupon> cq = cb.createQuery(Coupon.class);
            Root<Coupon> root = cq.from(Coupon.class);
            cq.select(root);

            List<Coupon> coupons = session.createQuery(cq).getResultList();
            transaction.commit();
            return coupons;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new UserDAO.DataAccessException("Failed to retrieve all coupons", e);
        }
    }

    public Coupon findCouponByCode(String code) {
        List<Coupon> coupons = getAllCoupons();
        for (Coupon coupon : coupons) {
            if (coupon.getCode().equals(code)) {
                return coupon;
            }
        }
        return null;
    }

    public void use_Coupon(long id) throws InvalidInputException {
        Coupon coupon = getCoupon(id);
        if(coupon.getUser_counts()<=0){
            throw new InvalidInputException("Coupon");
        }
        coupon.setUser_counts(coupon.getUser_counts() - 1);
        updateCoupon(coupon);

    }
    public void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
