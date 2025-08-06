package Handler;

import DAO.FoodDAO;
import DAO.RatingDAO;
import DAO.UserDAO;
import Controller.RatingController;
import Exceptions.*;
import Model.Rating;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;



public class RatingHandler implements HttpHandler {

    RatingDAO ratingDAO;
    FoodDAO foodDAO;
    UserDAO userDAO;

    private static final int  MAX_IN_MEMORY_SIZE = 1024 * 1024;
    private static final int CHUNK_SIZE = 8192;

    public RatingHandler(RatingDAO ratingDAO, FoodDAO foodDAO, UserDAO userDAO) {
        this.ratingDAO = ratingDAO;
        this.foodDAO = foodDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String response = "";
        String method = exchange.getRequestMethod();
        String[] paths = exchange.getRequestURI().getPath().split("/");

        try {
            switch (method) {
                case "GET":
                    System.out.println("GET request received");
                    response = handleGetRequest(exchange, paths);
                    break;

                case "POST":
                    System.out.println("POST request received");
                    response = handlePostRequest(exchange, paths);
                    break;

                case "PUT":
                    System.out.println("PUT request received");
                    response = handlePutRequest(exchange, paths);
                    break;

                case "DELETE":
                    System.out.println("DELETE request received");
                    response = handleDeleteRequest(exchange, paths);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String handlePostRequest(HttpExchange exchange, String[] paths) throws IOException {

        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);
        JSONObject jsonObject = getJsonObject(exchange);
        int http_code = 200;

        if (paths.length == 2) {

            try {
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }

                String phone = JwtUtil.extractSubject(token);
                String name = userDAO.getUserByPhone(phone).getfullname();

                RatingController.Submit_Rating sub_requestt = new RatingController.Submit_Rating(jsonObject, phone, name, ratingDAO, foodDAO);
                http_code = 200;
                response = generate_msg("Rating submitted");
            } catch (OrangeException e) {

                response = generate_msg(e.getMessage());
                http_code = e.http_code;
            }
        }

        send_Response(exchange,http_code,response);
        return response;
    }


    private String handlePutRequest(HttpExchange exchange, String[] paths) throws IOException {
        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);
        JSONObject jsonObject = getJsonObject(exchange);
        int http_code = 200;

        if (paths.length == 3) {

            Long comment_id = Long.parseLong(paths[2]);
            Rating rating = ratingDAO.getRating(comment_id);

            try {
                if (rating == null) {
                    throw new NosuchItemException();
                }

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }

                String phone = JwtUtil.extractSubject(token);

                if (!rating.getAuthor_phone().equals(phone)) {
                    throw new InvalidTokenexception();
                }

                RatingController.Update_Rating_Req update_req = new RatingController.Update_Rating_Req(jsonObject, ratingDAO, comment_id);
                RatingController.Update_Rating_Response update_res = new RatingController.Update_Rating_Response(comment_id, ratingDAO);
                response = update_res.getResponse();
                http_code = 200;
            } catch (IllegalArgumentException e) {
                response = generate_error("Invalid comment id");
                http_code = 400;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }

        send_Response(exchange,http_code,response);
        return response;
    }


    private String handleGetRequest(HttpExchange exchange, String[] paths) throws IOException {

        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);
        int http_code = 200;
        if (paths.length == 4 && paths[2].equals("items")) {

            try {
                Long item_id = Long.parseLong(paths[3]);

                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }

                RatingController.Get_Rating_for_item get_req = new RatingController.Get_Rating_for_item(item_id, foodDAO, ratingDAO,userDAO, JwtUtil.extractSubject(token));
                response = get_req.getResponse();
                http_code = 200;

            } catch (IllegalArgumentException e) {
                response = generate_error("Invalid item id");
                http_code = 400;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        } else if (paths.length == 3) {
            try {
                Long item_id = Long.parseLong(paths[2]);
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }


                RatingController.Get_Rating_by_id get_res = new RatingController.Get_Rating_by_id(item_id, ratingDAO);
                response = get_res.getResponse();
                http_code = 200;
            } catch (IllegalArgumentException e) {
                response = generate_error("Invalid item id");
                http_code = 400;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }
        send_Response(exchange,http_code,response);

        return response;
    }

    private String handleDeleteRequest(HttpExchange exchange, String[] paths) throws IOException {

        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);
        int http_code = 200;

        if (paths.length == 3) {

            try {
                Long comment_id = Long.parseLong(paths[2]);
                Rating rating = ratingDAO.getRating(comment_id);

                if (rating == null) {
                    throw new NosuchItemException("Rating not found");
                }
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }

                String phone = JwtUtil.extractSubject(token);

                if (!rating.getAuthor_phone().equals(phone)) {
                    throw new InvalidTokenexception();
                }

                ratingDAO.deleteCRating(comment_id);
                response = generate_msg("Comment deleted");
                http_code = 200;
            } catch (IllegalArgumentException e) {
                response = generate_error("Invalid item id");
                http_code = 400;
            } catch (OrangeException e) {
                response = generate_error(e.getMessage());
                http_code = e.http_code;
            }
        }
        send_Response(exchange,http_code,response);

        return response;
    }


    public static JSONObject getJsonObject(HttpExchange exchange) throws IOException {

        try (InputStream requestBody = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return new JSONObject(body.toString());
        }
    }

    public void send_Response(HttpExchange exchange,int http_code,String response) throws IOException {

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        if (responseBytes.length <= MAX_IN_MEMORY_SIZE) {
            exchange.sendResponseHeaders(http_code, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
        else {
            exchange.sendResponseHeaders(http_code, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                int offset = 0;
                while (offset < responseBytes.length) {
                    int length = Math.min(CHUNK_SIZE, responseBytes.length - offset);
                    os.write(responseBytes, offset, length);
                    offset += length;
                }
            }
        }
    }
    public String generate_msg(String msg) {
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }

    public String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }
}
