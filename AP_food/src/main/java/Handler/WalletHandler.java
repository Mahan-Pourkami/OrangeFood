package Handler;

import DAO.BuyerDAO;
import Exceptions.ForbiddenroleException;
import Exceptions.InvalidInputException;
import Exceptions.InvalidTokenexception;
import Exceptions.OrangeException;
import Model.Buyer;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

public class WalletHandler implements HttpHandler {

    BuyerDAO buyerDAO ;

    public WalletHandler(BuyerDAO buyerDAO) {
        this.buyerDAO = buyerDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String response = "";
        String methode = exchange.getRequestMethod();
        String []paths = exchange.getRequestURI().getPath().split("/");

        try{
            switch (methode) {
                case "POST":
                    handlePostRequest(exchange,paths);
                    break;

                default:
                    response = generate_error("Method not supported");
                    exchange.getResponseHeaders().add("Content-Type", "text/plain");
                    exchange.sendResponseHeaders(405, response.length());
                    break;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            send_Response(exchange, response);
        }
    }

    private String handlePostRequest(HttpExchange exchange , String [] paths) throws IOException {

        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);
        JSONObject jsonObject = getJsonObject(exchange);
        int http_code = 200 ;

        if(paths.length == 3 && paths[2].equals("top-up")){

           try {
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }
                if (!jsonObject.has("amount")) {
                    throw new InvalidInputException("amount");
                }

                String phone = JwtUtil.extractSubject(token);
                Buyer buyer = buyerDAO.getBuyer(phone);
                int amount = jsonObject.getInt("amount");

                if (amount < 1) {
                    throw new InvalidInputException("amount");
                }

                buyer.charge(amount);
                buyerDAO.updateBuyer(buyer);
                http_code = 200;
                response = generate_msg("Your wallet toned up successfully");
            }
           catch(OrangeException e){
               response = generate_error(e.getMessage());
               http_code = e.http_code;
           }
        }

        else {

            response = generate_error("endpoint not supported");
            http_code = 404;
        }

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }

        return response;
    }


    private static JSONObject getJsonObject(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody)))
        {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return new JSONObject(body.toString());
        }
    }

    private String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }

    private String generate_msg(String msg){
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }

    public void send_Response(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
