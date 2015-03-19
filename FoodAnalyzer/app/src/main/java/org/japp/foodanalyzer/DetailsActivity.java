package org.japp.foodanalyzer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

// Activity to display the Food item details.
public class DetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // get the extra data from the intent and set it to the text field.
        Bundle bundle = getIntent().getExtras();
        String food_details = bundle.getString("Result");

        // If the result text is empty then display the error message.
       if (food_details.isEmpty())
            food_details.concat("Error retrieving Food Item details.");

        TextView food_details_text = (TextView) findViewById(R.id.food_details);
        food_details_text.setText(food_details);
        Log.d("DetailsActivity", food_details);


    }

}
