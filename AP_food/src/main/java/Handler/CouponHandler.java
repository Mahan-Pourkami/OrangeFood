package Handler;

import DAO.CouponDAO;
import Exceptions.*;
import Model.Coupon;
import Utils.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;

public class CouponHandler implements HttpHandler {

    CouponDAO couponDAO;

    public CouponHandler(CouponDAO couponDAO) {
        this.couponDAO = couponDAO;
    }

    @Override
    public void handle (HttpExchange exchange) throws IOException {

        String response = "";
        String methode = exchange.getRequestMethod();
        String []paths = exchange.getRequestURI().getPath().split("/");

        try{
            switch (methode) {
                case "GET":
                    System.out.println("GET Request Received");
                    response = handleGetRequest(exchange, paths);
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


    private String handleGetRequest(HttpExchange exchange , String [] paths) throws IOException {

        String response = "";
        String token = JwtUtil.get_token_from_server(exchange);
        JSONObject jsonObject = getJsonObject(exchange);
        int httpCode = 200;

        if(paths.length==3 ){

           try {
                if (!JwtUtil.validateToken(token)) {
                    throw new InvalidTokenexception();
                }
                if (!JwtUtil.extractRole(token).equals("buyer")) {
                    throw new ForbiddenroleException();
                }

                //"coupon_code="

                String couponCode = paths[2].substring("coupon_code=".length());
                Coupon coupon = couponDAO.findCouponByCode(couponCode);

                if (coupon == null) {
                    throw new NosuchItemException("coupon");
                }

                if (!coupon.is_valid(coupon.getMin_price())) {
                    throw new InvalidInputException("coupon");
                }

                JSONObject responsejson = new JSONObject();
                responsejson.put("id", coupon.getId());
                responsejson.put("coupon_code", couponCode);
                responsejson.put("type", coupon.getType().toString());
                responsejson.put("value", coupon.getValue());
                responsejson.put("min_price", coupon.getMin_price());
                responsejson.put("start_date", coupon.getStart_time());
                responsejson.put("end_date", coupon.getEnd_time());
                response = responsejson.toString();
                httpCode = 200;
            }

           catch (IllegalArgumentException e) {
               response = generate_error("Invalid coupon_code");
           }
           catch (OrangeException e){
               response = generate_error(e.getMessage());
               httpCode = e.http_code;
           }
        }

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(httpCode, response.length());

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

    public String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }

    public String generate_msg(String msg){
        JSONObject msgJson = new JSONObject();
        msgJson.put("message", msg);
        return msgJson.toString();
    }



    public void send_Response(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }

}
