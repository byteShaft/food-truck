package com.byteshaft.foodtruck.customer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.foodtruck.R;
import com.byteshaft.foodtruck.utils.AppGlobals;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Locale;

/**
 * Created by s9iper1 on 1/21/17.
 */

public class TruckDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView topImage;
    private ImageView foodTruckImage;
    private TextView mTruckName;
    private TextView mAddress;
    private TextView mProducts;
    private ImageButton mFacebookButton;
    private ImageButton mWebsiteButton;
    private ImageButton mTwitterButton;
    private ImageButton mInstagramButton;
    private ImageButton mCallButton;
    private ImageButton mLocationButton;
    private RatingBar mRatingBar;

    private String mFacebookUrl;
    private String mWebsiteUrl;
    private String mTwitterUrl;
    private String mInstagramUrl;
    private String mContact;
    private String mLocationUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_detail);
        mRatingBar = (RatingBar) findViewById(R.id.rating);
        mTruckName = (TextView) findViewById(R.id.truck_name);
        mAddress = (TextView) findViewById(R.id.truck_address);
        mProducts = (TextView) findViewById(R.id.products);

        mFacebookButton = (ImageButton) findViewById(R.id.facebook);
        mWebsiteButton = (ImageButton) findViewById(R.id.website);
        mTwitterButton = (ImageButton) findViewById(R.id.twitter);
        mInstagramButton = (ImageButton) findViewById(R.id.instagram);
        mCallButton = (ImageButton) findViewById(R.id.contact);
        mLocationButton = (ImageButton) findViewById(R.id.navigate);

        mFacebookButton.setOnClickListener(this);
        mWebsiteButton.setOnClickListener(this);
        mTwitterButton.setOnClickListener(this);
        mInstagramButton.setOnClickListener(this);
        mCallButton.setOnClickListener(this);
        mLocationButton.setOnClickListener(this);

        topImage = (ImageView) findViewById(R.id.top_image);
        foodTruckImage = (ImageView) findViewById(R.id.food_truck_image);

        mTruckName.setText(getIntent().getStringExtra("name"));
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        mAddress.setText(getIntent().getStringExtra("address"));
        mProducts.setText(getIntent().getStringExtra("products"));
        mTruckName.setTypeface(AppGlobals.typefaceBold);
        mAddress.setTypeface(AppGlobals.typefaceNormal);
        mProducts.setTypeface(AppGlobals.typefaceBold);
        mRatingBar.setRating(getIntent().getFloatExtra("rating", 0));

        mFacebookUrl = getIntent().getStringExtra("facebook");
        mWebsiteUrl = getIntent().getStringExtra("website");
        mTwitterUrl = getIntent().getStringExtra("twitter");
        mInstagramUrl = getIntent().getStringExtra("instagram");
        mContact = getIntent().getStringExtra("phone");
        mLocationUrl = getIntent().getStringExtra("location");
        System.out.println("Location lat long " + mLocationUrl);

        Picasso.with(this)
                .load(getIntent().getStringExtra("image"))
                .resize(150, 150)
                .centerCrop()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        foodTruckImage.setImageBitmap(getDropShadow(bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
//        foodTruckImage.setImageBitmap(getDropShadow());
    }

    private Bitmap getDropShadow(Bitmap bitmap) {

        if (bitmap == null) return null;
        int think = 6;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int newW = w - (think);
        int newH = h - (think);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);
        Bitmap sbmp = Bitmap.createScaledBitmap(bitmap, newW, newH, false);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bmp);

        // Right
        Shader rshader = new LinearGradient(newW, 0, w, 0, Color.GRAY, Color.LTGRAY, Shader.TileMode.CLAMP);
        paint.setShader(rshader);
        c.drawRect(newW, think, w, newH, paint);

        // Bottom
        Shader bshader = new LinearGradient(0, newH, 0, h, Color.GRAY, Color.LTGRAY, Shader.TileMode.CLAMP);
        paint.setShader(bshader);
        c.drawRect(think, newH, newW, h, paint);

        //Corner
        Shader cchader = new LinearGradient(0, newH, 0, h, Color.LTGRAY, Color.LTGRAY, Shader.TileMode.CLAMP);
        paint.setShader(cchader);
        c.drawRect(newW, newH, w, h, paint);


        c.drawBitmap(sbmp, 0, 0, null);

        return bmp;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook:
                if (!mFacebookUrl.contains("http")) {
                    Toast.makeText(this, "No valid url provided", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mFacebookUrl)));

                break;
            case R.id.website:
                if (!mWebsiteUrl.contains("http")) {
                    Toast.makeText(this, "No valid url provided", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mWebsiteUrl)));
                break;
            case R.id.twitter:
                if (!mFacebookUrl.contains("http")) {
                    Toast.makeText(this, "No valid url provided", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(mFacebookUrl));
                    startActivity(i);
                }
                break;
            case R.id.instagram:
                if (!mFacebookUrl.contains("http")) {
                    Toast.makeText(this, "No valid url provided", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(mFacebookUrl));
                    startActivity(i);
                }
                break;
            case R.id.contact:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mContact));
                startActivity(intent);
                break;
            case R.id.navigate:
                String[] latLng = mLocationUrl.split(",");
                String uri = String.format(Locale.ENGLISH, "geo:%s,%s", latLng[0], latLng[1]);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(mapIntent);

                break;
        }
    }
}
