package com.byteshaft.foodtruck.customer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.byteshaft.foodtruck.R;
import com.byteshaft.foodtruck.utils.AppGlobals;

/**
 * Created by s9iper1 on 1/24/17.
 */

public class AboutActivity extends AppCompatActivity {

    private TextView aboutContent;
    private String aboutText = "<i>Food Truck Connexion</i>is a business and not just an app. Hence a high class platform,  <i>Food Truck Connexion</i> the most latest technology for locating and connecting food trucks across the county to consumers. Our user friendly customized features makes it seamless for users to locate favorite food trucks in any state via global positioning system (GPS), social media, phone or email. <br/> <br/>" +
            "Food Truck owners create a profile, and their locations from the app. With just a click on the app, customers get immediate access to food trucks which broadcasts their exact location and profile to all app users. All State Food Truck provide users with a list of food trucks starting from current location. Users can also browse for trucks, utilize an in-app search, or track highly ranked trucks. Users can tap on a food truck’s name which has  directions to its specific location for easy access and will be able to record and keep track of their favorite food trucks.<br/> <br/>" +
            "<i>Food Truck Connexion</i> is designed and built to be  a user-friendly platform. Our platform’s vision is to help bridge the gap between food trucks and their customers and be able to get reviews which will help with competitiveness in the food truck  industry. <i>Food Truck Connexion</i> has highly talented entrepreneurs as owners with resources and capabilities to help make a strong connection between food truck  owners and customers.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_about);
        aboutContent = (TextView) findViewById(R.id.about);
        aboutContent.setTypeface(AppGlobals.typefaceNormal);
        aboutContent.setText(Html.fromHtml(aboutText));
    }
}
