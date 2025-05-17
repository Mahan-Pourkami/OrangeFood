package Model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;

public class Admin{
    private String id;
    private String password;
    private HashSet<User> users;
    private HashSet<Restaurant> restaurants;
    private HashSet<Restaurant> waitingRestaurants;
    public Admin(String id, String password){
        if(isNullOrEmpty(id) || isNullOrEmpty(password)){
            throw new IllegalArgumentException("id or password cannot be null or empty");
        }
        this.id = id;
        this.password = password;
        this.waitingRestaurants = new HashSet<>();
        this.users = new HashSet<>();
        this.restaurants = new HashSet<>();
    }
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    public String getId() {
        return id;
    }
    public String getPassword() {
        return password;
    }
    public void showStatus(){}


    public void askToConfirmRestaurant(Restaurant res){ //seller asks admin to confirm the restaurant by adding it to waiting list
        if (res == null) return;
        waitingRestaurants.add(res);
    }
    public void confirmRestaurant(String name){ //admin confirms the restaurant by making confirmation true and removing from list
        if (name == null || name.trim().isEmpty()) return;
        Iterator<Restaurant> iterator = waitingRestaurants.iterator();
        while(iterator.hasNext()){
            Restaurant res = iterator.next();
            if(res.getName().equals(name)){
                res.setConfirmed(true);
                iterator.remove(); // safe remove
                restaurants.add(res);
                break;
            }
        }

    }
    public void confirmRestaurant (Restaurant res){ //admin confirms the restaurant by making confirmation true and removing from list
        if(res == null) return;
        res.setConfirmed(true);
        restaurants.add(res);
        waitingRestaurants.remove(res);
    }
    public void rejectRestaurant(String name){ //seller asks admin to reject the restaurant (with name) by adding it to waiting list
        for(Restaurant r : waitingRestaurants){
            if(r.getName().equals(name)){
                r.setConfirmed(false);
                waitingRestaurants.remove(r);
                break;
            }
        }
    }
    public void rejectRestaurant(Restaurant res){ //admin rejects the restaurant by making confirmation false and removing it from waiting list
        if (res == null) return;
        res.setConfirmed(false);
        waitingRestaurants.remove(res);
    }
    public void addUser(User user){
        if(user == null) return;
        users.add(user);
    }
    public void removeUser(User user){
        if (user == null) return;
        users.remove(user);
    }
    public void removeUser(String phone){
        if (phone == null || phone.trim().isEmpty()) return;
        Iterator<User> iterator = users.iterator();
        while(iterator.hasNext()){
            User user = iterator.next();
            if(phone.equals(user.getPhone())){
                iterator.remove();
            }
        }
    }
}