package DAO;

import Model.TransactionT;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class TransactionTDAO {

    private final SessionFactory sessionFactory;

    public TransactionTDAO() {
        this.sessionFactory = new Configuration()
                .addAnnotatedClass(TransactionT.class)
                .configure()
                .buildSessionFactory();
    }

    public void saveTransaction(TransactionT transactionT) {

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(transactionT);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to save transaction", e);
        }
    }

    public TransactionT getTransaction(long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TransactionT transactionT = session.get(TransactionT.class, id);
            transaction.commit();
            return transactionT;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to get transaction", e);
        }
    }

    public void updateTransaction(TransactionT transactionT) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(transactionT);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to update transaction", e);
        }
    }

    public void deleteTransaction(long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TransactionT transactionT = session.get(TransactionT.class, id);
            if (transactionT != null) {
                session.remove(transactionT);
                transaction.commit();
            } else {
                throw new RuntimeException("Transaction not found");
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to delete transaction", e);
        }
    }

    public List<TransactionT> getAllTransactions() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TransactionT> cq = cb.createQuery(TransactionT.class);
            Root<TransactionT> root = cq.from(TransactionT.class);
            cq.select(root);

            List<TransactionT> transactions = session.createQuery(cq).getResultList();
            transaction.commit();
            return transactions;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to retrieve all transactions", e);
        }
    }

    public List<TransactionT> getTransactionsByUserId(String userId) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TransactionT> cq = cb.createQuery(TransactionT.class);
            Root<TransactionT> root = cq.from(TransactionT.class);
            cq.select(root).where(cb.equal(root.get("userId"), userId));

            List<TransactionT> transactions = session.createQuery(cq).getResultList();
            transaction.commit();
            return transactions;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to retrieve transactions for userId: " + userId, e);
        }
    }


    public void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
