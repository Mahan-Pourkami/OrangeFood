package MainServer;

import Handler.AuthHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Server {
    public static void main(String[] args) {

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/auth" , new AuthHandler());


           server.start();
           System.out.println("MainServer.Server started at http://localhost:8080");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
