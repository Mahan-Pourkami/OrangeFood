package DAO;

import Model.Rating;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class RatingDAO {

    private final SessionFactory sessionFactory;

    public RatingDAO() {
        sessionFactory = new Configuration().addAnnotatedClass(Rating.class).configure().buildSessionFactory();
    }

    public void saveRating(Rating rating) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(rating);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public Rating getRating(long id) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();
            Rating rating = (Rating) session.get(Rating.class, id);
            transaction.commit();

            if (rating != null) {
                return rating;
            } else return null;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("failed to get Rating", e);
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

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(rating);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("failed to update rating", e);
        }
    }

    public void deleteCRating(long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Rating rating = session.get(Rating.class, id);
            if (rating != null) {
                session.remove(rating);
                transaction.commit();
            } else {
                throw new RuntimeException("Rating not found");
            }
        }
    }

    public List<Rating> getRatingsByitemId(long itemId) {

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "FROM Rating r WHERE r.item_id = :itemIdParam";

            List<Rating> result = session.createQuery(hql, Rating.class)
                    .setParameter("itemIdParam", itemId)
                    .getResultList();

            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to fetch ratings for item ID: " + itemId, e);
        }
    }


    public double calculate_avg_rating(long item_id) {

        List<Rating> ratings = getRatingsByitemId(item_id);
        double avg_rating = 0;
        if(ratings.isEmpty()) {
            return 0;
        }
        for (Rating rating : ratings) {
            avg_rating += rating.getRating();
        }
        avg_rating /= ratings.size();
        return avg_rating;
    }
}
