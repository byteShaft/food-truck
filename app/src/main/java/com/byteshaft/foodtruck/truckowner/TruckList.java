package com.byteshaft.foodtruck.truckowner;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.foodtruck.R;
import com.byteshaft.foodtruck.utils.AppGlobals;
import com.byteshaft.foodtruck.utils.Helpers;
import com.byteshaft.foodtruck.utils.SimpleDividerItemDecoration;
import com.byteshaft.requests.HttpRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;


public class TruckList extends AppCompatActivity implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener {

    public RecyclerView mRecyclerView;
    private CustomView viewHolder;
    private CustomAdapter mAdapter;
    private HttpRequest request;
    public ArrayList<TruckDetail> truckDetails;
    private TextView truckTextView;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static TruckList sInstance;

    public static TruckList getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_truck_list);
        sInstance = this;
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        truckDetails = new ArrayList<>();
        Log.i("TAG" , ""+ AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.owner_truck_list_recycler_view);
        truckTextView = (TextView) findViewById(R.id.no_truck_text_view);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mAdapter = new CustomAdapter(truckDetails, this);
        mRecyclerView.setAdapter(mAdapter);
        getTruckDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_new_truck) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Permission Request");
                alertDialogBuilder.setMessage("Location permission is required to navigate users exactly to your truck. " +
                        "please grant location permission to proceed.")
                        .setCancelable(false).setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(TruckList.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                });
                alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                if (!locationEnabled()) {
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage("Location is not enabled");
                    dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                            //get gps
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub

                        }
                    });
                    dialog.show();
                } else {
                    startActivity(new Intent(getApplicationContext(), AddNewTruck.class));

                }

                return true;
            }
        }

            return super.onOptionsItemSelected(item);
    }

    private boolean locationEnabled() {
        LocationManager lm = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!locationEnabled()) {
                        // notify user
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setMessage("Location is not enabled");
                        dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                // TODO Auto-generated method stub
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                //get gps
                            }
                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                // TODO Auto-generated method stub

                            }
                        });
                        dialog.show();
                    } else {
                        startActivity(new Intent(getApplicationContext(), AddNewTruck.class));

                    }
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void getTruckDetails() {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%suser/trucks", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
        Helpers.showProgressDialog(TruckList.this, "Fetching truck details");
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                    Log.i("TAG", "Truck "+ request.getResponseText());
                        try {
                            JSONArray jsonArray = new JSONArray(request.getResponseText());
                            if (jsonArray.length() > 0) {

                            } else {
                                truckTextView.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TruckDetail truckDetail = new TruckDetail();
                                truckDetail.setId(jsonObject.getInt("id"));
                                truckDetail.setTruckName(jsonObject.getString("name"));
                                truckDetail.setAddress(jsonObject.getString("address"));
                                truckDetail.setLatLng(jsonObject.getString("location"));
                                truckDetail.setContactNumber(jsonObject.getString("phone_number"));
                                truckDetail.setProducts(jsonObject.getString("products"));
                                truckDetail.setImageUrl(Uri.parse(jsonObject.getString("photo")
                                        .replace("http://localhost/", AppGlobals.SERVER_IP)));
                                truckDetail.setRating(jsonObject.getString("ratings"));
                                truckDetail.setWebsiteUrl(jsonObject.getString("website"));
                                truckDetail.setFacebookUrl(jsonObject.getString("facebook"));
                                truckDetail.setInstagramUrl(jsonObject.getString("instagram"));
                                truckDetail.setTwitterUrl(jsonObject.getString("twitter"));
                                truckDetails.add(truckDetail);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);

    }

    class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<TruckDetail> items;
        private OnItemClickListener mListener;
        private GestureDetector mGestureDetector;
        private Activity mActivity;

        public CustomAdapter(ArrayList<TruckDetail> truckDetails, Context context,
                             OnItemClickListener listener) {
            this.items = truckDetails;
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        public CustomAdapter(ArrayList<TruckDetail> truckDetails, Activity activity) {
            this.items = truckDetails;
            this.mActivity = activity;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.delegate_owner_truck, parent, false);
            viewHolder = new CustomView(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            final TruckDetail truckDetail = items.get(position);
            viewHolder.truckName.setText(truckDetail.getTruckName());
            viewHolder.truckAddress.setText(truckDetail.getAddress());
            viewHolder.products.setText(truckDetail.getProducts());
            viewHolder.truckName.setTypeface(AppGlobals.typefaceNormal);
            viewHolder.products.setTypeface(AppGlobals.typefaceNormal);
            viewHolder.truckAddress.setTypeface(AppGlobals.typefaceNormal);
            Picasso.with(mActivity)
                    .load(truckDetail.getImageUrl())
                    .resize(150, 150)
                    .centerCrop()
                    .into(viewHolder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {


                        }
                    });
            viewHolder.facebookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!truckDetail.getFacebookUrl().contains("http")) {
                        Toast.makeText(mActivity, "url not valid", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    newFacebookIntent(truckDetail.getFacebookUrl());
                }
            });

            viewHolder.websiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!truckDetail.getWebsiteUrl().contains("http")) {
                        Toast.makeText(mActivity, "url not valid", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse(truckDetail.getWebsiteUrl()));
                    startActivity(intent);
                }
            });

            viewHolder.twitterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!truckDetail.getTwitterUrl().contains("http")) {
                        Toast.makeText(mActivity, "url not valid", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(truckDetail.getTwitterUrl()));
                    startActivity(intent);

                }
            });

            viewHolder.instagramButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!truckDetail.getInstagramUrl().contains("http")) {
                        Toast.makeText(mActivity, "url not valid", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Uri uri = Uri.parse(truckDetail.getInstagramUrl());
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(truckDetail.getInstagramUrl())));
                    }

                }
            });

        }

        public Intent newFacebookIntent(String url) {
            Uri uri = Uri.parse(url);
            try {
                ApplicationInfo applicationInfo = getPackageManager()
                        .getApplicationInfo("com.facebook.katana", 0);
                if (applicationInfo.enabled) {
                    // http://stackoverflow.com/a/24547437/1048340
                    uri = Uri.parse("fb://facewebmodal/f?href=" + url);
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
            return new Intent(Intent.ACTION_VIEW, uri);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
//            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
//                mListener.onItem(items.get(rv.getChildPosition(childView)), (TextView)
//                        rv.findViewHolderForAdapterPosition(rv.getChildPosition(childView)).
//                                itemView.findViewById(R.id.specific_category_title));
//                return true;
//            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface OnItemClickListener {
        void onItem(Integer item, TextView textView);
    }

    // custom viewHolder to access xml elements requires a view in constructor
    public static class CustomView extends RecyclerView.ViewHolder {
        public TextView truckName;
        public TextView products;
        public ImageView imageView;
        public TextView truckAddress;
        public ImageButton facebookButton;
        public ImageButton websiteButton;
        public ImageButton twitterButton;
        public ImageButton instagramButton;

        public CustomView(View itemView) {
            super(itemView);
            truckName = (TextView) itemView.findViewById(R.id.truck_name);
            products = (TextView) itemView.findViewById(R.id.products);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            truckAddress = (TextView) itemView.findViewById(R.id.truck_address);
            facebookButton = (ImageButton) itemView.findViewById(R.id.facebook_button);
            websiteButton = (ImageButton) itemView.findViewById(R.id.website_button);
            twitterButton = (ImageButton) itemView.findViewById(R.id.twitter_button);
            instagramButton = (ImageButton) itemView.findViewById(R.id.instrgram_button);
        }
    }
}
