package DAO;

import Model.Coupon;
import Model.Rating;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class RatingDAO {

    private final SessionFactory sessionFactory;
    public RatingDAO() {
        sessionFactory = new Configuration().addAnnotatedClass(Rating.class).configure().buildSessionFactory();
    }

    public void saveRating(Rating rating) {
        Transaction transaction = null ;
        try(Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            session.persist(rating);
            transaction.commit();
        }
        catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public Rating getRating(long id) {

        Transaction transaction = null ;

        try(Session session = sessionFactory.openSession()){

            transaction = session.beginTransaction();
            Rating rating = (Rating) session.get(Rating.class, id);
            transaction.commit();

            if(rating != null) {
                return rating;
            }
            else return null;
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to get Rating",e);
        }
    }

    public List<Rating> getAllRatings() {

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Rating> cq = cb.createQuery(Rating.class);
            Root<Rating> root = cq.from(Rating.class);
            cq.select(root);

            List<Rating> ratings = session.createQuery(cq).getResultList();
            transaction.commit();
            return ratings;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new UserDAO.DataAccessException("Failed to retrieve all ratings", e);
        }
    }

    public void updateRating(Rating rating) {

        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(rating);
            transaction.commit();
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to update rating",e);
        }
    }

    public void deleteCRating(long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Coupon coupon= session.get(Coupon.class, id);
            if (coupon != null) {
                session.remove(coupon);
                transaction.commit();
            } else {
                throw new RuntimeException("Rating not found");
            }
        }
    }

    public List<Rating> getRatingsByitemId(long itemId) {

        List<Rating> ratings = getAllRatings();
        List<Rating> results = new ArrayList<Rating>();

        for(Rating rating : ratings) {
            if(rating.getItem_id().equals(itemId)) {
                results.add(rating);
            }
        }
        return results;
    }
}
