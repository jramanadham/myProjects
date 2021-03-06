package org.japp.foodanalyzer;

import java.util.HashMap;

/**
 * Created by xaviea on 3/16/15.
 * Follows the http://ndb.nal.usda.gov/ndb/doc/index  API documentation to retrieve food list and their details
 * via implementing the HTTP Request and Response
 */
public class FoodItem extends Object {
    private String food_description;
    private String food_id;//stackoverflow.com/questions/18249834/pointers-are-replaced-with-what-in-java
    private HashMap<String, String> nutrient_values;

    public  static String HTTP_REQUEST_GET_FOOD_LIST = "http://api.nal.usda.gov/usda/ndb/list/?format=json&api_key=             &location=Denver+CO&lt=f&sort=id&offset=3000&max=15";
    public  static String HTTP_REQUEST_GET_FOOD_ITEM_DETAILS = "http://api.nal.usda.gov/usda/ndb/reports/?format=json&type=b&api_key=           &ndbno=";



    public FoodItem(){
        this.food_description = null;
        this.food_id = null;
    }

    public FoodItem(String food_description, String food_id) {

        this.food_description = food_description;
        this.food_id = food_id;
        this.nutrient_values = new HashMap<String, String>();
    }

    // returns the food item ID
    public String getFoodId() {
        return food_id;
    }

    // returns the Food item name
    public String getFoodDescription() {

        return food_description;
    }

    // sets the nutrient name and value. Future use.
    public void setFoodNutientValue(String nutrient_name, String nutrient_value) {
        nutrient_values.put(nutrient_name, nutrient_value);
    }

    // returns all the nutrient details.
    public HashMap getFood_Nutrient_values() {
        return nutrient_values;
    }

}
