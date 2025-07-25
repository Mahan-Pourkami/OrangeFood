package Handler;

import DAO.*;
import Exceptions.*;
import Model.*;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDateTime;

public class PaymentHandler implements HttpHandler {

    BasketDAO basketDAO;
    UserDAO userDAO;
    FoodDAO foodDAO;
    RestaurantDAO restaurantDAO;
    TransactionTDAO transactionTDAO;
    BuyerDAO buyerDAO;
    CouponDAO couponDAO;

    public PaymentHandler(BasketDAO basketDAO, UserDAO userDAO, FoodDAO foodDAO, RestaurantDAO restaurantDAO, TransactionTDAO transactionTDAO, BuyerDAO buyerDAO, CouponDAO couponDAO) {
        this.basketDAO = basketDAO;
        this.userDAO = userDAO;
        this.foodDAO = foodDAO;
        this.restaurantDAO = restaurantDAO;
        this.transactionTDAO = transactionTDAO;
        this.buyerDAO = buyerDAO;
        this.couponDAO = couponDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String response = "";
        String methode = exchange.getRequestMethod();
        String[] paths = exchange.getRequestURI().getPath().split("/");
        int http_code = 200; // Default success code

        try {
            switch (methode) {
                case "POST":
                    response = handlePostRequest(exchange, paths);
                    break;

                default:
                    http_code = 405; // Method Not Allowed
                    response = generate_error("Method not supported");
                    break;
            }
        } catch (ArithmeticException e) {
            http_code = 403;
            response = generate_error("Not enough money");
        } catch (OrangeException e) {
            http_code = e.http_code;
            response = generate_error(e.getMessage());
        } catch (Exception e) {
            http_code = 500; // Internal Server Error
            response = generate_error("An internal server error occurred.");
            e.printStackTrace();
        } finally {
            send_Response(exchange, response, http_code);
        }
    }

    private String handlePostRequest(HttpExchange exchange, String[] paths) throws IOException, OrangeException {

        String token = JwtUtil.get_token_from_server(exchange);
        String response = "";

        if (paths.length == 3 && paths[2].equals("online")) {
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }

            JSONObject jsonobject = getJsonObject(exchange);
            String user_id = JwtUtil.extractSubject(token);
            if (invalidInputItems(jsonobject).isEmpty()) {
                Long orderId = ((Number) jsonobject.get("order_id")).longValue();

                if (!basketDAO.existBasket(orderId)) {
                    throw new NosuchItemException();
                }
                Basket basket = basketDAO.getBasket(orderId);
                if (basket==null || !(basket.getStateofCart() == StateofCart.waiting)) {
                    throw new NosuchItemException();
                }

                if(!basket.getBuyerPhone().equals(user_id)) {
                    throw  new ForbiddenroleException();
                }

                if (jsonobject.get("method").equals("wallet")) {
                    try {
                        Buyer buyer = buyerDAO.getBuyer(user_id);
                        buyer.discharge(basket.getPayPrice(restaurantDAO, foodDAO, couponDAO));
                        buyerDAO.updateBuyer(buyer);
                        if(buyer.getchargevalue()-basket.getPayPrice(restaurantDAO,foodDAO,couponDAO)<-100) {
                            throw new ArithmeticException();
                        }
                    } catch (ArithmeticException e) {
                        throw new ArithmeticException();
                    }
                }

                TransactionT transaction = new TransactionT(
                        orderId,
                        user_id,
                        (String) jsonobject.get("method"),
                        "success"
                );

                transactionTDAO.saveTransaction(transaction);
                basket.setStateofCart(StateofCart.payed);
                basket.setUpadated_at(LocalDateTime.now().toString());
                if(basket.getCoupon_id()!=0 && basket.getCoupon_id()!=null) {
                    Coupon coupon = couponDAO.getCoupon(basket.getCoupon_id());
                    coupon.setUser_counts(coupon.getUser_counts() - 1);
                    couponDAO.updateCoupon(coupon);
                }
                basketDAO.updateBasket(basket);
                response = getTransactionJsonObject(transaction).toString();
            } else {
                response = generate_error("Invalid " + invalidInputItems(jsonobject));
                throw new OrangeException(response, 400);
            }
            return response;
        } else {
            throw new OrangeException("endpoint not supported", 404);
        }
    }

    private String invalidInputItems(JSONObject jsonObject) throws OrangeException {
        String[] requiredFields = {"order_id", "method"};

        if (jsonObject.length() != 2) {
            return "fields.";
        }

        for (String field : requiredFields) {
            if (!jsonObject.has(field)) {
                return field;
            }
        }

        try {
            // Check 'search' is a string (can be empty)
            Object method = jsonObject.get("method");
            if (!(method instanceof String)) {
                return "method";
            }
            if (!(method.equals("wallet") || method.equals("online"))) {
                return "method";
            }

            Object orderId = jsonObject.get("order_id");
            if (!(orderId instanceof Integer)) {
                return "Price";
            }


        } catch (Exception e) {
            return "types";
        }

        return "";
    }

    private static JSONObject getJsonObject(HttpExchange exchange) throws IOException {
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

    private String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }

    private String generate_msg(String msg) {
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }

    public JSONObject getTransactionJsonObject(TransactionT transactionT) {
        JSONObject basketJson = new JSONObject();
        basketJson.put("id", transactionT.getId());
        basketJson.put("order_id", transactionT.getOrderId());
        basketJson.put("user_id", transactionT.getUserId()); //تو yaml نوشته باید int باشه ولی فعلا string میفرستیم
        basketJson.put("method", transactionT.getMethod());
        basketJson.put("status", transactionT.getStatus());
        return basketJson;
    }

    public void send_Response(HttpExchange exchange, String response, int http_code) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        long responseLength = (response == null || response.isEmpty()) ? -1 : response.getBytes().length;
        exchange.sendResponseHeaders(http_code, responseLength);

        if (responseLength > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            exchange.getResponseBody().close();
        }
    }
}
