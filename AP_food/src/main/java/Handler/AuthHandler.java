package Handler;

import Execptions.*;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.sql.Date;
import java.sql.SQLException;



public class AuthHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {


        String request = exchange.getRequestMethod();
        String [] paths = exchange.getRequestURI().getPath().split("/");
        String response = "";

        try {

            switch (request) {

                case "GET":
                    System.out.println("GET request received");
                    //TODO
                    break;

                case "POST":
                    //TODO
                    break;

                case "PUT":
                    //TODO
                    break;

                default:
                    response = "Invalid request";
                    exchange.sendResponseHeaders(405, response.length());
                    break;
            }
        }
        catch (Exception e) {

            response = "MainServer.Server Error";
            exchange.sendResponseHeaders(500, response.length());
            e.printStackTrace();

        }

        finally {

            sendResponse(exchange, response);
        }
    }


    private String handleGetRequest(HttpExchange exchange) throws IOException {

        String response = "";


        return exchange.getRequestURI().getPath();
    }







    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }

}
