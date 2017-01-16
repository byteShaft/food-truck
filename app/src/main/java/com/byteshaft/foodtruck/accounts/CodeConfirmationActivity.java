package com.byteshaft.foodtruck.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class CodeConfirmationActivity extends AppCompatActivity implements
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private EditText codeField;
    private Button confirmButton;
    private String mCodeText;
    private HttpRequest request;
    private String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("Code Confirmation");
        setContentView(R.layout.activity_code_confirmation);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        codeField = (EditText) findViewById(R.id.et_code);
        emailId = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL);
        System.out.println(emailId);
        confirmButton = (Button) findViewById(R.id.button_activate_account);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    activateUser(emailId, mCodeText);
                }
            }
        });
    }

    public boolean validate() {
        boolean valid = true;
        mCodeText = codeField.getText().toString();
        if (mCodeText.trim().isEmpty()) {
            codeField.setError("required");
        } else {
            codeField.setError(null);
        }
        return valid;
    }

    private void activateUser(String email, String email_otp) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%suser/activate", AppGlobals.BASE_URL));
        request.send(getUserActivationData(email, email_otp));
        Helpers.showProgressDialog(CodeConfirmationActivity.this, "Activating User");
    }

    private String getUserActivationData(String email, String email_otp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("email_otp", email_otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:{
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        Toast.makeText(getApplicationContext(), "Please enter correct account activation key", Toast.LENGTH_LONG).show();
                        break;
                    case HttpURLConnection.HTTP_OK:
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String username = jsonObject.getString(AppGlobals.KEY_FULL_NAME);
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String token = jsonObject.getString(AppGlobals.KEY_TOKEN);
                            /// saving details

                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FULL_NAME, username);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            AppGlobals.saveUserActive(true);
                            LoginActivity.getInstance().finish();
                            RegisterActivity.getInstance().finish();
                            finish();
                            startActivity(new Intent(getApplicationContext(), TruckList.class));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);

    }
}
