package org.japp.foodanalyzer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SplashScreenActivity extends Activity {
    public static final String FOOD_LIST_DES_STRING_INTENT = "Food_List_Description_intent" ;
    public static final String FOOD_LIST_ID_STRING_INTENT = "Food_List_Id_intent" ;

    HTTPGetFoodListTask httpGetFoodListTask = new HTTPGetFoodListTask();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // HTTP GET to get the list of Food Items.
         httpGetFoodListTask.execute(FoodItem.HTTP_REQUEST_GET_FOOD_LIST);

    }

    private class HTTPGetFoodListTask extends AsyncTask<String, Void, List<FoodItem>> {

        AndroidHttpClient httpClient = AndroidHttpClient.newInstance("");

        @Override
        protected List<FoodItem> doInBackground(String... urls) {

            if (urls.length == 0) {
                Log.e("HTTPGetJSONTask", "Insufficient length");
                return null;
            }
            HttpGet request = new HttpGet(urls[0]);
            JSONResponseHandler responseHandler = new JSONResponseHandler();
            try {
                return httpClient.execute(request, responseHandler);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<FoodItem> foodItems) {

            ArrayList<String> food_list_des_array = new ArrayList<String>();
            ArrayList<String> food_list_id_array = new ArrayList<String>();

            String result = new String();
            super.onPostExecute(foodItems);
            if (null != httpClient)
                httpClient.close();

            if(foodItems != null)
                 for (int index = 0; index < foodItems.size(); index++) {
                    result += foodItems.get(index).getFoodId();
                    result += foodItems.get(index).getFoodDescription();
                    result += "****\n";

                    food_list_des_array.add(index, foodItems.get(index).getFoodDescription());
                    food_list_id_array.add(index,foodItems.get(index).getFoodId());

                }
                Log.d("SPlashScreen ", result);




            // Launch the Main Activity to display the list of food items.
            Intent mainIntent = new Intent(SplashScreenActivity.this, MainScreenActivity.class);
            mainIntent.putStringArrayListExtra(FOOD_LIST_DES_STRING_INTENT, food_list_des_array);
            mainIntent.putStringArrayListExtra(FOOD_LIST_ID_STRING_INTENT,food_list_id_array);
            SplashScreenActivity.this.startActivity(mainIntent);
            SplashScreenActivity.this.finish();
        }

        private class JSONResponseHandler implements ResponseHandler<List<FoodItem>> {

            private static final String VERSION_NUMBER_SR = "sr";
            private static final String VERSION_NUMBER = "27";
            private static final String ITEM_ID = "id";
            private static final String ITEM_TAG = "item";
            private static final String ITEM_NAME = "name";
            private static final String LIST_ELEMENT = "list";

            // parse the JSON received in the HTTP Response
            @Override
            public List<FoodItem> handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {

                List<FoodItem> food_list_obtained = null;

                // parse the HTTP Response
                String JSONResponse = new BasicResponseHandler().handleResponse(httpResponse);
                try {
                    // parsing the JSON list details
                    // Get the top level JSON Object
                    JSONObject response_object = new JSONObject(JSONResponse);
                    JSONObject list_object = response_object.getJSONObject(LIST_ELEMENT);
                    String sr_version = list_object.getString(VERSION_NUMBER_SR);
                    if (sr_version.equals(VERSION_NUMBER)) {
                        food_list_obtained = new ArrayList<FoodItem>();

                        JSONArray item_array = list_object.getJSONArray(ITEM_TAG);
                        // loop through the array and get the details
                        for (int index = 0; index < item_array.length(); index++) {

                            JSONObject item_details = (JSONObject) item_array.get(index);
                            FoodItem new_item = new FoodItem(item_details.getString(ITEM_NAME), item_details.getString(ITEM_ID));
                            food_list_obtained.add(new_item);
                        }
                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }


                return food_list_obtained;
            }
        }


    }
}
