package org.japp.foodanalyzer;

import android.app.Activity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.List;

/**
 * Created by xaviea on 3/16/15.
 */
public class MainScreenActivity extends Activity {
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String>  food_list_desc_store = null;
    private ArrayList<String> food_list_id_store = null;
    private String food_item_details = null;
    private boolean item_selected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button ok_button;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // get the Food item Description  and the food item  ID from the intent
        food_list_desc_store = getIntent().getStringArrayListExtra(SplashScreenActivity.FOOD_LIST_DES_STRING_INTENT);
        food_list_id_store = getIntent().getStringArrayListExtra(SplashScreenActivity.FOOD_LIST_ID_STRING_INTENT);

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.listViewDetails );

        // add the Food items list to the adapter
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        for(int idx =0; idx < food_list_desc_store.size(); idx++){
            listAdapter.add( food_list_desc_store.get(idx) );
        }

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );

        // set the listener for item selection
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("ITEM SELECTED", food_list_id_store.get(position));
                Log.d("ITEM DESCRIPTION", food_list_desc_store.get(position));
                item_selected = true;

                // Initiate the Asych task to get the food details from the server.
                new HTTPGetFoodItemDetailsTask().execute(food_list_id_store.get(position));
            }

        });


        // set the onclick Listener to the button
        ok_button = (Button) findViewById(R.id.button_get_details);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if( !item_selected){
                    Toast.makeText(MainScreenActivity.this,"Please select an Food item first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Launch the Details Activity.
                Intent details_Intent = new Intent(MainScreenActivity.this, DetailsActivity.class);
                // send the resultant Food item details to the Details activity.
                details_Intent.putExtra("Result", food_item_details);
                MainScreenActivity.this.startActivity(details_Intent);

            }
        });




    }

// Async task to perform the http get to the food item nutrient details.
    private class HTTPGetFoodItemDetailsTask extends AsyncTask<String, Void, String>{

        AndroidHttpClient httpClient = AndroidHttpClient.newInstance("");

        @Override
        protected String doInBackground(String... food_item_id) {

            if (food_item_id.length == 0) {
                Log.e("HTTPGetFoodItemDetailsTask", "Insufficient length");
                return null;
            }

            // Generate the HTTP Request at run time based on the food item selected
            String requestUrl = FoodItem.HTTP_REQUEST_GET_FOOD_ITEM_DETAILS;
            requestUrl += food_item_id[0];

            Log.d("HTTPGetFoodItemDetailsTask",food_item_id[0]);
            Log.d("HTTPGetFoodItemDetailsTask",requestUrl);

            HttpGet request = new HttpGet(requestUrl);
            FoodDetailsJSONResponseHandler responseHandler = new FoodDetailsJSONResponseHandler();
            // perform the HTTP Get to obtain the JSON Response.
            try {
                return httpClient.execute(request,responseHandler);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String food_item_details_local) {
            super.onPostExecute(food_item_details);
            if (null != httpClient)
                httpClient.close();


            // stores the Food items Nutrient details to be displayed to the user.
            food_item_details = new String(food_item_details_local);


        }


    // Parses the JSON response received from the server
        private class FoodDetailsJSONResponseHandler implements ResponseHandler<String> {

            private static final String VERSION_NUMBER_SR = "sr";
            private static final String VERSION_NUMBER = "27";
            private static final String REPORT_TAG = "report";
            private static final String FOOD_TAG = "food";
            private static final String NUTRIENTS_TAG = "nutrients";
            private static final String NAME_TAG = "name";
            private static final String NUTRIENT_PROTEIN_NAME = "Protein";
            private static final String LABEL_TAG = "label";
            private static final String MEASURES_TAG = "measures";
            private static final String UNIT_TAG = "unit";
            private static final String VALUE_TAG = "value";

        // parse the JSON Response to retrieve the Nutrient details for the food item selected.
            @Override
            public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {

                StringBuilder result = new StringBuilder();
                String JSONResponse = new BasicResponseHandler().handleResponse(httpResponse);
                try {

                    JSONObject response_object = new JSONObject((JSONResponse));
                    JSONObject report_object = response_object.getJSONObject(REPORT_TAG);
                    String sr_version = report_object.getString(VERSION_NUMBER_SR);
                    if (sr_version.equals(VERSION_NUMBER)) {
                        JSONObject food_object = report_object.getJSONObject(FOOD_TAG);
                        result.append(" Food Description:\t");
                        result.append(food_object.getString(NAME_TAG));
                        result.append("\n");
                        JSONArray nutrients_array = food_object.getJSONArray(NUTRIENTS_TAG);
                        for( int index =0; index < nutrients_array.length(); index++){
                            JSONObject nutrient_item = (JSONObject) nutrients_array.get(index);
                            String nutrient_name = nutrient_item.getString(NAME_TAG);

                            if( nutrient_name.equals(NUTRIENT_PROTEIN_NAME)){
                                JSONArray measure_array = nutrient_item.getJSONArray(MEASURES_TAG);
                                if(  measure_array.length() > 0){
                                    JSONObject measure_item = (JSONObject) measure_array.get(0);
                                    String measure = measure_item.getString(LABEL_TAG);
                                    if(!measure.isEmpty()){
                                        result.append(MEASURES_TAG);
                                        result.append(" :\t");
                                        result.append(measure_item.getString(LABEL_TAG));
                                        result.append("\n");
                                    }

                                    result.append(NUTRIENT_PROTEIN_NAME);
                                    result.append(" :\t");
                                    result.append(measure_item.getString(VALUE_TAG));
                                    result.append(nutrient_item.getString(UNIT_TAG));




                                }

                            }
                        }

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

                Log.d("Get Food Details ResponseHandler", result.toString());

                return result.toString();
            }
        }

    }


  }
