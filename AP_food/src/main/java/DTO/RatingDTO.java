package DTO;

import DAO.FoodDAO;
import DAO.RatingDAO;
import Exceptions.InvalidInputException;
import Exceptions.NosuchItemException;
import Exceptions.UnsupportedMediaException;
import Model.Rating;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List ;

public class RatingDTO {


    public static class Submit_Rating {

        private FoodDAO foodDAO;

        private RatingDAO ratingDAO;

        private long item_id ;

        private int rating;

        private String comment;

        private String author_phone;

        private String author_name ;

        private List<String> imageBase64 ;

        public Submit_Rating(JSONObject jsonObject, String author_phone , String author_name, RatingDAO ratingDAO , FoodDAO foodDAO) throws IOException {

            String []required = {"item_id","rating","comment"};

            for(String requiredItem : required) {
                if(!jsonObject.has(requiredItem)) {
                    throw new InvalidInputException(requiredItem);
                }
            }
            this.ratingDAO = ratingDAO;
            this.foodDAO = foodDAO;
            this.item_id = jsonObject.getLong("item_id");
            this.rating = jsonObject.getInt("rating");
            this.comment = jsonObject.getString("comment");
            this.author_phone = author_phone;
            this.author_name = author_name;

            if(jsonObject.has("imageBase64")) {
                JSONArray jsonArray = jsonObject.getJSONArray("imageBase64");
                this.imageBase64 = new ArrayList<>();
                for(int i = 0; i < jsonArray.length(); i++) {
                    this.imageBase64.add(jsonArray.getString(i));
                }

                for(String imageBase64Item : this.imageBase64) {
                    if(!imageBase64Item.endsWith(".png") && !imageBase64Item.endsWith(".jpg") && !imageBase64Item.endsWith(".jpeg")) {
                        throw new UnsupportedMediaException();
                    }
                }
            }
            else {
                this.imageBase64 = new ArrayList<>();
            }

            if(foodDAO.getFood(item_id) == null) {

                throw new NosuchItemException();
            }

            Rating rating = new Rating(this.author_phone,this.author_name,this.item_id,this.rating,this.comment,this.imageBase64);
            ratingDAO.saveRating(rating);
        }
    }

    public static class Get_Rating_for_item {

        private FoodDAO foodDAO;
        private RatingDAO ratingDAO;
        private String response ;

        public Get_Rating_for_item(long itemid ,FoodDAO foodDAO, RatingDAO ratingDAO) {

            this.foodDAO = foodDAO;
            this.ratingDAO = ratingDAO;

            List <Rating> ratings = ratingDAO.getRatingsByitemId(itemid);

            double avg = ratingDAO.calculate_avg_rating(itemid);

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("avg_rating", avg);

            for(Rating rating : ratings) {

                JSONObject temp = new JSONObject();
                temp.put("id",rating.getItem_id());
                temp.put("item_id",rating.getItem_id());
                temp.put("rating",rating.getRating());
                temp.put("comment",rating.getComment());

                JSONArray array_temp = new JSONArray();
                for (String photo : rating.getImageBase64() ) {
                    array_temp.put(photo);
                }
                temp.put("imageBase64",array_temp);
                temp.put("user_name",rating.getAuthor_name());
                temp.put("created_at",rating.getDate_added());
                jsonArray.put(temp);
            }
            jsonObject.put("comments", jsonArray);

            this.response = jsonObject.toString();
        }
        public String getResponse() {
            return response;
        }

    }

    public static class Get_Rating_by_id {

        private RatingDAO ratingDAO;
        private String response ;

        public Get_Rating_by_id(long comment_id ,RatingDAO ratingDAO) throws IOException {

            this.ratingDAO = ratingDAO;
            Rating rating = ratingDAO.getRating(comment_id);
            if(rating == null) {
                throw new NosuchItemException();
            }
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            jsonObject.put("id", comment_id);
            jsonObject.put("item_id", rating.getItem_id());
            jsonObject.put("rating", rating.getRating());
            jsonObject.put("comment", rating.getComment());

            for (String photo : rating.getImageBase64() ) {
                jsonArray.put(photo);
            }
            jsonObject.put("imageBase64",jsonArray);
            jsonObject.put("user_name",rating.getAuthor_name());
            jsonObject.put("created_at",rating.getDate_added());
            this.response = jsonObject.toString();
        }
        public String getResponse() {
            return response;
        }
    }

}
