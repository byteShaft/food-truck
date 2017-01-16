package com.byteshaft.foodtruck.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.foodtruck.R;
import com.byteshaft.foodtruck.customer.MainActivity;
import com.byteshaft.foodtruck.truckowner.TruckList;
import com.byteshaft.foodtruck.utils.AppGlobals;
import com.byteshaft.foodtruck.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginButton;
    private Button mRegisterButton;
    private TextView mForgotPasswordTextView;
    private String mPasswordString;
    private String mEmailString;

    private HttpRequest request;

    private static LoginActivity sInstance;

    public static LoginActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        sInstance = this;
        mEmail = (EditText) findViewById(R.id.email_address);
        mPassword = (EditText) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.login);
        mRegisterButton = (Button) findViewById(R.id.register);
        mLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        mForgotPasswordTextView = (TextView) findViewById(R.id.tv_forgot_password);
        mForgotPasswordTextView.setOnClickListener(this);
    }

    public boolean validate() {
        boolean valid = true;

        mEmailString = mEmail.getText().toString();
        mPasswordString = mPassword.getText().toString();
        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.
                matcher(mEmailString).matches()) {
            mEmail.setError("enter a valid email address");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (mPasswordString.isEmpty() || mPassword.length() < 4) {
            mPassword.setError("Enter minimum 4 alphanumeric characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }
        return valid;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                if (validate()) {
                    loginUser(mEmailString, mPasswordString);
                }
                break;
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.tv_forgot_password:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void loginUser(String email, String password) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%suser/login", AppGlobals.BASE_URL));
        request.send(getUserLoginData(email, password));
        Helpers.showProgressDialog(LoginActivity.this, "Logging In");
    }

    private String getUserLoginData(String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(LoginActivity.this, "Login Failed!",
                                "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        AppGlobals.alertDialog(LoginActivity.this, "Login Failed!",
                                "provide a valid EmailAddress");
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        AppGlobals.alertDialog(LoginActivity.this, "Login Failed!",
                                "Please enter correct password");
                        break;
                    case HttpURLConnection.HTTP_FORBIDDEN:
                        Toast.makeText(getApplicationContext(), "Please activate your account !",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), CodeConfirmationActivity.class);
                        startActivity(intent);
                        break;
                    case HttpURLConnection.HTTP_OK:
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String username = jsonObject.getString(AppGlobals.KEY_FULL_NAME);
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String token = jsonObject.getString(AppGlobals.KEY_TOKEN);

                            //saving details
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FULL_NAME, username);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            AppGlobals.saveUserLogin(true);
                            AppGlobals.saveUserActive(true);
                            finish();
                            startActivity(new Intent(this, TruckList.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }
}
