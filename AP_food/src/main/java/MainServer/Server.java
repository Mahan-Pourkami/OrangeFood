package MainServer;

import DAO.*;
import Handler.*;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {


    private static final int THREAD_POOL_SIZE = 50;

    public static void main(String[] args) {

        try {

            UserDAO userDAO = new UserDAO();
            SellerDAO sellerDAO = new SellerDAO();
            CourierDAO courierDAO = new CourierDAO();
            CouponDAO couponDAO = new CouponDAO();
            BuyerDAO buyerDAO = new BuyerDAO();
            RestaurantDAO restaurantDAO = new RestaurantDAO();
            FoodDAO foodDAO = new FoodDAO();
            RatingDAO ratingDAO = new RatingDAO();
            BasketDAO basketDAO = new BasketDAO();
            TransactionTDAO transactionTDAO = new TransactionTDAO();
            BuyerDAO buyerDao = new BuyerDAO();



            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            server.setExecutor(executor);

            
            server.createContext("/auth" , new AuthHandler(courierDAO,sellerDAO,buyerDAO,userDAO,restaurantDAO));
            server.createContext("/restaurants",new RestaurantsHandler(sellerDAO,restaurantDAO,foodDAO));
            server.createContext("/favorites" , new FavoriteHandler(buyerDAO,restaurantDAO));
            server.createContext("/admin",new AdminHandler(userDAO, sellerDAO, courierDAO, couponDAO, restaurantDAO));
            server.createContext("/rating" , new RatingHandler(ratingDAO,foodDAO,userDAO));
            server.createContext("/wallet" , new WalletHandler(buyerDAO));
            server.createContext("/coupon" , new CouponHandler(couponDAO));
            server.createContext("/vendors" , new VendorHandler(restaurantDAO,foodDAO));
            server.createContext("/items" , new ItemsHandler(foodDAO));
            server.createContext("/orders" , new OrderHandler(userDAO,couponDAO,basketDAO, restaurantDAO, foodDAO));
            server.createContext("/payment" , new PaymentHandler(basketDAO,userDAO,foodDAO,restaurantDAO,transactionTDAO,buyerDao));


            server.start();

           System.out.println("MainServer.Server started at http://localhost:8080");

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}