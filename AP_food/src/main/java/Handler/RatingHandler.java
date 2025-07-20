package Handler;

import DAO.FoodDAO;
import DAO.RatingDAO;
import DAO.UserDAO;
import DTO.RatingDTO;
import Exceptions.*;
import Model.Rating;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

public class RatingHandler implements HttpHandler {

    RatingDAO ratingDAO;
    FoodDAO foodDAO;
    UserDAO userDAO;

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
        } finally {

            send_Response(exchange, response);
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

                RatingDTO.Submit_Rating sub_requestt = new RatingDTO.Submit_Rating(jsonObject, phone, name, ratingDAO, foodDAO);
                http_code = 200;
                response = generate_msg("Rating submitted");
            } catch (OrangeException e) {

                response = generate_msg(e.getMessage());
                http_code = e.http_code;
            }
        }

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
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

                RatingDTO.Update_Rating_Req update_req = new RatingDTO.Update_Rating_Req(jsonObject, ratingDAO, comment_id);
                RatingDTO.Update_Rating_Response update_res = new RatingDTO.Update_Rating_Response(comment_id, ratingDAO);
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

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }

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
                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }

                RatingDTO.Get_Rating_for_item get_req = new RatingDTO.Get_Rating_for_item(item_id, foodDAO, ratingDAO);
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
                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }

                RatingDTO.Get_Rating_by_id get_res = new RatingDTO.Get_Rating_by_id(item_id, ratingDAO);
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
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }

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

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(http_code, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }

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

    public void send_Response(HttpExchange exchange, String response) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
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
