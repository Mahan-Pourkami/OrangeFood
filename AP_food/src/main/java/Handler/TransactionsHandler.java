package Handler;

import DAO.*;
import Exceptions.*;
import Model.*;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Base64;
import java.util.List;

//فرض میکنیم همه پرداخت های موفق اند
public class TransactionsHandler implements HttpHandler {

    TransactionTDAO transactionTDAO;

    public TransactionsHandler(TransactionTDAO transactionTDAO) {
        this.transactionTDAO = transactionTDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String response = "";
        String methode = exchange.getRequestMethod();
        String[] paths = exchange.getRequestURI().getPath().split("/");
        int http_code = 200; // Default success code

        try {
            switch (methode) {
                case "GET":
                    response = handleGetRequest(exchange, paths);
                    break;

                default:
                    http_code = 405; // Method Not Allowed
                    response = generate_error("Method not supported");
                    break;
            }
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

    private String handleGetRequest(HttpExchange exchange, String[] paths) throws IOException, OrangeException {

        String token = JwtUtil.get_token_from_server(exchange);
        String response = "";

        if (paths.length == 2) {
            if (!JwtUtil.validateToken(token)) {
                throw new InvalidTokenexception();
            }
            if (!JwtUtil.extractRole(token).equals("buyer")) {
                throw new ForbiddenroleException();
            }

            String user_id = JwtUtil.extractSubject(token);

            List<TransactionT> transactionTList = transactionTDAO.getTransactionsByUserId(user_id);
            JSONArray transactionjsonArray = new JSONArray();
            for (TransactionT transactionT : transactionTList) {
                JSONObject jsontransactiont = getTransactionJsonObject(transactionT);
                transactionjsonArray.put(jsontransactiont);
            }
            response = transactionjsonArray.toString();
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