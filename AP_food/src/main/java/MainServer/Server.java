package MainServer;

import Handler.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {


    private static final int THREAD_POOL_SIZE = 50;

    public static void main(String[] args) {

        try {

            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            server.setExecutor(executor);

            
            server.createContext("/auth" , new AuthHandler());
            server.createContext("/restaurants",new RestaurantsHandler());
            server.createContext("/favorites" , new FavoriteHandler());
            server.createContext("/admin",new AdminHandler());
            server.createContext("/rating" , new RatingHandler());
            server.createContext("/wallet" , new WalletHandler());
            server.createContext("/coupon" , new CouponHandler());
            server.createContext("/vendors" , new VendorHandler());



            server.start();

           System.out.println("MainServer.Server started at http://localhost:8080");

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}