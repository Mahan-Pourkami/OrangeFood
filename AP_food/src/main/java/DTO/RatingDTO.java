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
}
