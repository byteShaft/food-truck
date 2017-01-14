package com.byteshaft.foodtruck.accounts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.foodtruck.MainActivity;
import com.byteshaft.foodtruck.R;
import com.byteshaft.foodtruck.utils.AppGlobals;
import com.byteshaft.foodtruck.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private Button mRegisterButton;
    private EditText mUsername;
    private EditText mEmailAddress;
    private EditText mPassword;
    private EditText mVerifyPassword;

    private String mUsernameString;
    private String mEmailAddressString;
    private String mVerifyPasswordString;
    private String mPasswordString;


    private HttpRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUsername = (EditText) findViewById(R.id.user_name);
        mEmailAddress = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mVerifyPassword = (EditText) findViewById(R.id.verify_password);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                if (validateEditText()) {
                    registerUser(mUsernameString, mEmailAddressString, mPasswordString);
                }
        }
    }

    private boolean validateEditText() {
        boolean valid = true;
        mPasswordString = mPassword.getText().toString();
        mVerifyPasswordString = mVerifyPassword.getText().toString();
        mEmailAddressString = mEmailAddress.getText().toString();
        mUsernameString = mUsername.getText().toString();

        if (mUsernameString.trim().isEmpty()) {
            mUsername.setError("required");
        } else {
            mUsername.setError(null);
        }

        if (mPasswordString.trim().isEmpty() || mPasswordString.length() < 3) {
            mPassword.setError("enter at least 3 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (mVerifyPasswordString.trim().isEmpty() || mVerifyPasswordString.length() < 3 ||
                !mVerifyPasswordString.equals(mPasswordString)) {
            mVerifyPassword.setError("password does not match");
            valid = false;
        } else {
            mVerifyPassword.setError(null);
        }

        if (mEmailAddressString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddressString).matches()) {
            mEmailAddress.setError("please provide a valid email");
            valid = false;
        } else {
            mEmailAddress.setError(null);
        }
        return valid;
    }

    private void registerUser(String username, String email, String password) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%suser/register", AppGlobals.BASE_URL));
        request.send(getRegisterData(username, password, email));
        Helpers.showProgressDialog(RegisterActivity.this, "Registering User ");
    }

    private String getRegisterData(String username, String password, String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("full_name", username);
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
                Log.i("Tag", "Response " + request.getResponseText());
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(this, "Registration Failed!",
                                "Please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        AppGlobals.alertDialog(RegisterActivity.this, "Registration Failed!",
                                "Email already in use");
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        Toast.makeText(getApplicationContext(), "Activation code has been sent " +
                                "to your Email Address", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String username = jsonObject.getString(AppGlobals.KEY_FULL_NAME);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String id = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            // saving details
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FULL_NAME, username);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, id);
                            AppGlobals.saveUserLogin(true);
                            LoginActivity.getInstance().finish();
                            finish();
                            startActivity(new Intent(this, MainActivity.class));
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
