package Handler;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class AdminHandler implements HttpHandler {

    @Override
    public void handle (HttpExchange exchange) throws IOException {

    String response = "";
    String method = exchange.getRequestMethod();
    String []paths = exchange.getRequestURI().getPath().split("/");

        try{
            switch (method) {
                case "GET":
                    break;

                case "POST":
                    break;

                case "PUT":
                    break;

                case "DELETE":
                    break;

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        finally {

            send_Response(exchange,response);
        }

    }


    public void send_Response(HttpExchange exchange, String response) throws IOException {
        try(OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        exchange.close();
    }

    public String generate_error(String error) {

        JSONObject errorJson = new JSONObject();
        errorJson.put("error", error);
        return errorJson.toString();
    }
}
