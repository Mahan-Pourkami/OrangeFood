package DAO;

import Exceptions.ForbiddenroleException;
import Model.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class FoodDAO {

    final private  SessionFactory sessionFactory;
    public FoodDAO() {
        sessionFactory = new Configuration().configure().
                addAnnotatedClass(Food.class)
                .addAnnotatedClass(Basket.class)
                .addAnnotatedClass(Restaurant.class)
                .addAnnotatedClass(Seller.class)
                .addAnnotatedClass(Bankinfo.class)
                .buildSessionFactory();
    }


    public void saveFood(Food food) {

        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(food);
            transaction.commit();
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to save food",e);
        }
    }

    public void updateFood(Food food) {
        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(food);
            transaction.commit();
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to update food",e);
        }
    }

    public void deleteFood(Long id) {
        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Food food = session.get(Food.class, id);
            if(food!=null) {
                session.remove(food);
                transaction.commit();
            }
            else {
                throw new RuntimeException("food not found");
            }
        }

        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to delete food",e);
        }
    }

    public boolean existFood(Long id) {
        Transaction transaction = null ;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Food food = session.get(Food.class, id);
            if(food!=null) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to find exist food",e);
        }
    }

    public Food getFood(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Food food = session.get(Food.class, id);
            if (food != null) {
                return food;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            if(transaction!=null)transaction.rollback();
            throw new RuntimeException("failed to get food",e);
        }
    }


    public List<Food> getAllFoods(){
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Food> cq = cb.createQuery(Food.class);
            Root<Food> root = cq.from(Food.class);
            cq.select(root);

            List<Food> foods = session.createQuery(cq).getResultList();
            transaction.commit();
            return foods;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new UserDAO.DataAccessException("Failed to retrieve all foods", e);
        }
    }

    public List<Food> getFoodsByRestaurantId(Long restaurantId) {
        List<Food> result = new ArrayList<Food>();
        List<Food> foods = getAllFoods();
        for (Food food : foods) {
            if(food.getRestaurant().getId().equals(restaurantId)) {
                result.add(food);
            }
        }
        return result;
    }

    public List<Food> getFoodsByMenu(Long restaurantId, String menu_title) {
        List <Food> result = new ArrayList<>();
        List<Food> foods = getAllFoods();
        for (Food food : foods) {
            if(food.getRestaurant().getId().equals(restaurantId) && food.getMenuTitle().contains(menu_title)) {
                result.add(food);
            }
        }
        return result;
    }


    public Food findFoodByName(String name,long restaurantId) {
        List<Food> foods = getAllFoods();

        for (Food food : foods) {
            if(food.getName().equals(name) && food.getRestaurantId().equals(restaurantId)) {
                return food;
            }
        }
        return null;
    }

    public void delet_from_menu(String menu_title , long restaurantId) {
        List<Food> foods = getAllFoods();
        for (Food food : foods) {
            if(food.getMenuTitle()!=null && food.getMenuTitle().contains(menu_title) && food.getRestaurantId().equals(restaurantId)) {
                food.removeMenuTitle(menu_title);
                updateFood(food);
            }
        }
    }

    public void add_to_cart(long foodid , int quantity) throws ForbiddenroleException {

        Food food = getFood(foodid);

        if(food.getSupply() - quantity < 0) {
            throw new ForbiddenroleException("Insufficient food supply");
        }

        food.setSupply(food.getSupply()-quantity);
        updateFood(food);
    }


    public void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }







}
