package com.byteshaft.foodtruck.tabfragment;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.foodtruck.R;
import com.byteshaft.foodtruck.truckowner.AddNewTruck;
import com.byteshaft.foodtruck.truckowner.TruckDetail;
import com.byteshaft.foodtruck.truckowner.TruckList;
import com.byteshaft.foodtruck.utils.AppGlobals;
import com.byteshaft.requests.HttpRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import static com.byteshaft.foodtruck.utils.Helpers.locationEnabled;

/**
 * Created by s9iper1 on 1/12/17.
 */

public class FoodTruckFragment extends Fragment implements
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private View mBaseView;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private CustomAdapter customAdapter;
    private CustomView viewHolder;
    private HttpRequest request;
    private String nextUrl;
    private ArrayList<TruckDetail> truckDetails;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public double lat;
    public double lng;
    private int counter = 0;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!((AppCompatActivity) getActivity()).getSupportActionBar().isShowing()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
        mBaseView = inflater.inflate(R.layout.truck_fragment, container, false);
        truckDetails = new ArrayList<TruckDetail>();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Food Trucks");
        mRecyclerView = (RecyclerView) mBaseView.findViewById(R.id.truck_list);
        progressBar = (ProgressBar) mBaseView.findViewById(R.id.progress_bar);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()
                .getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.canScrollVertically(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        customAdapter = new CustomAdapter(truckDetails, getActivity());
        mRecyclerView.setAdapter(customAdapter);
        mRecyclerView.addOnItemTouchListener(new CustomAdapter(truckDetails, getActivity()
                .getApplicationContext(),
                new TruckList.OnItemClickListener() {
                    @Override
                    public void onItem(TruckDetail truckDetail) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), AddNewTruck.class);
                        intent.putExtra("id", truckDetail.getId());
                        intent.putExtra("name", truckDetail.getTruckName());
                        intent.putExtra("image", truckDetail.getImageUrl().toString());
                        intent.putExtra("address", truckDetail.getAddress());
                        intent.putExtra("location", truckDetail.getLatLng());
                        intent.putExtra("phone", truckDetail.getContactNumber());
                        intent.putExtra("products", truckDetail.getProducts());
                        intent.putExtra("facebook", truckDetail.getFacebookUrl());
                        intent.putExtra("website", truckDetail.getWebsiteUrl());
                        intent.putExtra("instagram", truckDetail.getInstagramUrl());
                        intent.putExtra("twitter", truckDetail.getTwitterUrl());
                        startActivity(intent);
                    }
                }));
        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG", "onResume");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            if (locationEnabled()) {
                buildGoogleApiClient();
                mGoogleApiClient.connect();
            } else {
                notifyUser();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("True");
                        if (!locationEnabled()) {
                            // notify user
                            notifyUser();
                        } else {
                            buildGoogleApiClient();
                            mGoogleApiClient.connect();
                        }


                } else {
                    Toast.makeText(getActivity(), "Permission denied!", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }

    private void notifyUser() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();

        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("TAG", "Location changed called" + "Lat " +
                location.getLatitude() + ", Lng "+ location.getLongitude());
        lat = location.getLatitude();
        lng = location.getLongitude();
        counter++;
        if (counter == 3) {
            getTrucksByLocation(lat+","+lng);
        }
    }


    private void getTrucksByLocation(String search) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%strucks/filter-by-location?base_location=%s", AppGlobals.BASE_URL, search));
        request.send();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                progressBar.setVisibility(View.GONE);
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        Log.i("TAG", "Truck "+ request.getResponseText());
                        try {
                            JSONObject mainData = new JSONObject(request.getResponseText());
                            nextUrl = mainData.getString("next");
                            JSONArray jsonArray = mainData.getJSONArray("results");
                            if (jsonArray.length() > 0) {

                            } else {
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
                                customAdapter.notifyDataSetChanged();
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
        progressBar.setVisibility(View.GONE);
    }

    public void setFullscreen(Activity activity) {
        if (Build.VERSION.SDK_INT > 10) {
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;

            if (isImmersiveAvailable()) {
                flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        } else {
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static boolean isImmersiveAvailable() {
        return android.os.Build.VERSION.SDK_INT >= 19;
    }

    class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
            RecyclerView.OnItemTouchListener {

        private ArrayList<TruckDetail> items;
        private TruckList.OnItemClickListener mListener;
        private GestureDetector mGestureDetector;
        private Activity mActivity;

        public CustomAdapter(ArrayList<TruckDetail> truckDetails, Context context,
                             TruckList.OnItemClickListener listener) {
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
                }
            });

            viewHolder.websiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!truckDetail.getWebsiteUrl().contains("http")) {
                        Toast.makeText(mActivity, "url not valid", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(truckDetail.getWebsiteUrl()));
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

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItem(items.get(rv.getChildPosition(childView)));
                return true;
            }
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
        void onItem(TruckDetail truckDetail);
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
