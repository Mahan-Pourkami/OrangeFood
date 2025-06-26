package DTO;

import Model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class AdminDTO {


    public static class Getusersresponse {

        private String response ;

       public Getusersresponse(List<User> users) {

            JSONArray jsonArray = new JSONArray();

            for (User user : users) {

                JSONObject jsonObject = new JSONObject();
                JSONObject bankinfo = new JSONObject();

                bankinfo.put("bank_name" , user.getBankinfo().getBankName());
                bankinfo.put("account_number" , user.getBankinfo().getAccountNumber());

                jsonObject.put("id", user.getId());
                jsonObject.put("full_name",user.getfullname());
                jsonObject.put("phone",user.getPhone());
                jsonObject.put("email",user.getEmail());
                jsonObject.put("role",user.role.toString());
                jsonObject.put("address",user.getAddress());
                jsonObject.put("profileImageBase64",user.getProfile());
                jsonObject.put("bankinfo",bankinfo);
                jsonArray.put(jsonObject);

            }

            this.response = jsonArray.toString();
        }

        public String getResponse() {
            return response;
        }

    }

}
